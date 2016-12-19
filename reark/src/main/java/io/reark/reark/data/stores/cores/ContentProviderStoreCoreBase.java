/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.reark.data.stores.cores;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reark.reark.utils.Log;
import io.reark.reark.utils.ObjectLockHandler;
import io.reark.reark.utils.Preconditions;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static io.reark.reark.utils.Preconditions.checkNotNull;

/**
 * ContentProviderStoreCoreBase implements an Observable based item store that uses a content provider as
 * its data backing store.
 *
 * All content provider operations are threaded. The store executes put operations in order, but
 * provides no guarantee for the execution order between get and put operations.
 *
 * This in an abstract class that implements the content provider access and expects extending
 * classes to implement data type specific methods.
 *
 * @param <U> Type of the data this store core contains.
 */
public abstract class ContentProviderStoreCoreBase<U> {

    private final String TAG = getClass().getSimpleName();

    @NonNull
    private static final ContentProviderOperation NO_OPERATION = ContentProviderOperation.newInsert(Uri.EMPTY).build();

    @NonNull
    private final ContentResolver contentResolver;

    @NonNull
    private final PublishSubject<Pair<U, Uri>> updateSubject = PublishSubject.create();

    @NonNull
    private final ObjectLockHandler<Uri> locker = new ObjectLockHandler<>();

    @Nullable
    private Subscription updateSubscription;

    private final int groupingTimeout;

    private final int groupMaxSize;

    protected ContentProviderStoreCoreBase(@NonNull final ContentResolver contentResolver) {
        this(contentResolver, 10, 30);
    }

    protected ContentProviderStoreCoreBase(@NonNull final ContentResolver contentResolver,
                                           final int groupingTimeout,
                                           final int groupMaxSize) {
        this.contentResolver = Preconditions.get(contentResolver);
        this.groupingTimeout = groupingTimeout;
        this.groupMaxSize = groupMaxSize;

        initialize();
    }

    private void initialize() {
        this.contentResolver.registerContentObserver(getContentUri(), true, getContentObserver());

        // Observable transforming updates and inserts to ContentProviderOperations
        Observable<ContentProviderOperation> operationObservable = updateSubject
                .flatMap(pair -> createOperation(pair.first, pair.second));

        // Group the operations to a list that should be executed in one batch. The default
        // grouping logic is suitable for pojo stores, but some stores may need to provide
        // their own grouping logic, if for example buffering delays are undesirable.
        updateSubscription = groupOperations(operationObservable)
                .observeOn(Schedulers.computation())
                .map(ArrayList::new)
                .doOnNext(list -> Log.v(TAG, "Grouped list of " + list.size()))
                .doOnNext(operations -> {
                    try {
                        ContentProviderResult[] result = contentResolver.applyBatch(getAuthority(), operations);
                        Log.v(TAG, String.format("Applied %s operations", result.length));
                    } catch (RemoteException | OperationApplicationException e) {
                        Log.e(TAG, "Error applying operations", e);
                    }
                })
                .flatMap(Observable::from)
                .map(ContentProviderOperation::getUri)
                .subscribe(locker::release,
                        // On error we can't release the processing lock. Any further update
                        // operations for this object will likely be blocked until app is restarted.
                        error -> Log.e(TAG, "Error while handling data! Processing may be blocked.", error));
    }

    @NonNull
    public static Handler createHandler(@NonNull final String name) {
        checkNotNull(name);

        HandlerThread handlerThread = new HandlerThread(name);
        handlerThread.start();
        return new Handler(handlerThread.getLooper());
    }

    /**
     * Implements grouping logic for batching the content provider operations. The default
     * logic buffers the operations with debounced timer, while applying a hard limit for the
     * number of operations. The data is serialized into a binder transaction, and an attempt
     * to pass a too large batch of operations will result in a failed binder transaction.
     */
    @NonNull
    protected Observable<List<ContentProviderOperation>> groupOperations(@NonNull final Observable<ContentProviderOperation> source) {
        return source.publish(stream -> stream.buffer(
                Observable.amb(
                        stream.debounce(groupingTimeout, TimeUnit.MILLISECONDS),
                        stream.skip(groupMaxSize - 1))
                        .first() // Complete observable after the first reached trigger
                        .repeatWhen(observable -> observable))); // Resubscribe immediately for the next buffer
    }

    @NonNull
    private Observable<ContentProviderOperation> createOperation(@NonNull final U item, @NonNull final Uri uri) {
        return Observable
                .fromCallable(() -> {
                    // We block until this Uri is freed for operations
                    try {
                        locker.acquire(uri);
                        Log.v(TAG, "Locked URI " + uri);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Thread interrupted", e);
                        return NO_OPERATION;
                    }

                    final Cursor cursor = contentResolver.query(uri, getProjection(), null, null, null);
                    U newItem = item;

                    if (cursor == null || !cursor.moveToFirst()) {
                        if (cursor != null) {
                            cursor.close();
                        }

                        Log.v(TAG, "Create insertion operation for " + uri);
                        return ContentProviderOperation.newInsert(uri)
                                .withValues(getContentValuesForItem(newItem))
                                .build();
                    }

                    final U currentItem = read(cursor);
                    boolean valuesEqual = newItem.equals(currentItem);

                    if (!valuesEqual) {
                        Log.v(TAG, "Merging values at " + uri);
                        newItem = mergeValues(currentItem, newItem);
                        valuesEqual = newItem.equals(currentItem);
                    }

                    cursor.close();

                    if (valuesEqual) {
                        Log.v(TAG, "Data already up to date at " + uri);

                        // No ContentResolverOperation created, so we need to release and free
                        // the Uri lock already here. For the other possible operations the lock
                        // will be released after the created operation has been executed.
                        locker.release(uri);
                        return NO_OPERATION;
                    }

                    Log.v(TAG, "Create update operation for " + uri);
                    return ContentProviderOperation.newUpdate(uri)
                            .withValues(getContentValuesForItem(newItem))
                            .build();

                })
                // Filter out values that didn't result in a valid operation
                .filter(operation -> !NO_OPERATION.equals(operation))
                .subscribeOn(Schedulers.io());
    }

    protected void put(@NonNull final U item, @NonNull final Uri uri) {
        checkNotNull(item);
        checkNotNull(uri);

        updateSubject.onNext(new Pair<>(item, uri));
    }

    @NonNull
    protected Observable<List<U>> getAllOnce(@NonNull final Uri uri) {
        checkNotNull(uri);

        return Observable.just(uri)
                .observeOn(Schedulers.io())
                .map(this::queryList);
    }

    @NonNull
    protected Observable<U> getOnce(@NonNull final Uri uri) {
        return getAllOnce(Preconditions.get(uri))
                .filter(list -> !list.isEmpty())
                .doOnNext(list -> {
                    if (list.size() > 1) {
                        Log.w(TAG, String.format("%s items found in a get for a single item", list.size()));
                    }
                })
                .map(queryResults -> queryResults.get(0));
    }

    @NonNull
    private List<U> queryList(@NonNull final Uri uri) {
        Cursor cursor = contentResolver.query(uri, getProjection(), null, null, null);
        List<U> list = new ArrayList<>(10);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                list.add(read(cursor));
            }
            while (cursor.moveToNext()) {
                list.add(read(cursor));
            }
            cursor.close();
        }
        if (list.isEmpty()) {
            Log.v(TAG, "Could not find with id: " + uri);
        }

        return list;
    }

    @NonNull
    protected ContentResolver getContentResolver() {
        return contentResolver;
    }

    @NonNull
    protected abstract String getAuthority();

    @NonNull
    protected abstract ContentObserver getContentObserver();

    @NonNull
    protected abstract Uri getContentUri();

    @NonNull
    protected abstract String[] getProjection();

    @NonNull
    protected abstract U read(@NonNull final Cursor cursor);

    @NonNull
    protected abstract ContentValues getContentValuesForItem(@NonNull final U item);

    @NonNull
    protected U mergeValues(@NonNull final U oldItem, @NonNull final U newItem) {
        return newItem; // Default behavior is new values overriding
    }
}
