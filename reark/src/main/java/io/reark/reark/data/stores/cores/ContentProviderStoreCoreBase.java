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
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import io.reark.reark.data.stores.cores.operations.CoreOperation;
import io.reark.reark.data.stores.cores.operations.CoreOperationResult;
import io.reark.reark.data.stores.cores.operations.CoreValue;
import io.reark.reark.data.stores.cores.operations.CoreValueDelete;
import io.reark.reark.data.stores.cores.operations.CoreValuePut;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.ObjectLockHandler;
import io.reark.reark.utils.Preconditions;
import rx.Observable;
import rx.Single;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

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

    static final int DEFAULT_GROUPING_TIMEOUT_MS = 100;

    static final int DEFAULT_GROUP_MAX_SIZE = 30;

    @NonNull
    private final ContentResolver contentResolver;

    @NonNull
    private final PublishSubject<CoreValue<U>> operationSubject = PublishSubject.create();

    @NonNull
    private final ConcurrentMap<Integer, Subject<Boolean, Boolean>> completionNotifiers = new ConcurrentHashMap<>(20, 0.75f, 4);

    @NonNull
    private final ObjectLockHandler<Uri> locker = new ObjectLockHandler<>();

    @Nullable
    private Subscription updateSubscription;

    private final int groupMaxSize;

    private final int groupingTimeout;

    private int nextOperationIndex = 0;

    protected ContentProviderStoreCoreBase(@NonNull final ContentResolver contentResolver) {
        this(contentResolver, DEFAULT_GROUPING_TIMEOUT_MS, DEFAULT_GROUP_MAX_SIZE);
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
        contentResolver.registerContentObserver(getContentUri(), true, getContentObserver());

        // Observable transforming inserts, updates and deletes to ContentProviderOperations
        Observable<CoreOperation> operationObservable = operationSubject
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .flatMap(this::createCoreOperation);

        // Group the operations to a list that should be executed in one batch. The default
        // grouping logic is suitable for pojo stores, but some stores may need to provide
        // their own grouping logic if for example buffering delays are undesirable.
        updateSubscription = groupOperations(operationObservable)
                .observeOn(Schedulers.computation())
                .doOnNext(operations -> Log.v(TAG, "Grouped list of " + operations.size()))
                .concatMap(this::applyOperations)
                .subscribe(this::release,
                        // On error we can't release the processing lock, as the Uri reference
                        // is lost. It's perhaps better to error out of the subscription than
                        // to leave some of the Uris locked and continue.
                        error -> Log.e(TAG, "Error while handling data operations!", error));
    }

    @NonNull
    private Observable<CoreOperationResult> applyOperations(@NonNull final List<CoreOperation> operations) {
        return Observable.fromCallable(() -> {
                    ArrayList<ContentProviderOperation> contentOperations = contentOperations(operations);
                    return contentResolver.applyBatch(getAuthority(), contentOperations);
                })
                .doOnNext(result -> Log.v(TAG, String.format("Applied %s operations", result.length)))
                .flatMap(Observable::from)
                .zipWith(operations, CoreOperationResult::new);
    }

    @NonNull
    private static ArrayList<ContentProviderOperation> contentOperations(@NonNull final List<CoreOperation> operations) {
        ArrayList<ContentProviderOperation> contentOperations = new ArrayList<>(operations.size());
        for (CoreOperation operation : operations) {
            contentOperations.add(operation.contentOperation());
        }
        return contentOperations;
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
     * logic buffers the operations with debounced timer while applying a hard limit for the
     * number of operations.
     *
     * The data is serialized into a binder transaction. An attempt to pass here a too large
     * batch of operations will result in a failed binder transaction.
     */
    @NonNull
    protected <R> Observable<List<R>> groupOperations(@NonNull final Observable<R> source) {
        return source.publish(stream -> stream.buffer(
                Observable.merge(
                        stream.window(groupMaxSize).skip(1),
                        stream.debounce(groupingTimeout, TimeUnit.MILLISECONDS))))
                .filter(list -> !list.isEmpty());
    }

    @NonNull
    private Observable<CoreOperation> createCoreOperation(@NonNull final CoreValue<U> value) {
        Observable<CoreOperation> valueObservable;

        switch (value.type()) {
            case PUT:
                valueObservable = createCoreOperation((CoreValuePut<U>) value);
                break;
            case DELETE:
                valueObservable = createCoreOperation((CoreValueDelete<U>) value);
                break;
            default:
                throw new IllegalStateException("Unknown value type " + value.type());
        }

        return valueObservable
                .onErrorReturn(__ -> value.noOperation())
                .doOnNext(this::releaseIfNoOp)
                .filter(CoreOperation::isValid);
    }

    @NonNull
    private Observable<CoreOperation> createCoreOperation(@NonNull final CoreValueDelete<U> value) {
        return Observable
                .fromCallable(() -> {
                    Uri uri = value.uri();

                    // We block until this Uri is freed for operations. Failure to lock
                    // will throw, which we'll catch later. This will ensure that we don't try
                    // to execute multiple operations for a same Uri in a same batch.
                    lock(uri);

                    Log.v(TAG, "Create delete contentOperation for " + uri);
                    return value.toDeleteOperation();
                });
    }

    @NonNull
    private Observable<CoreOperation> createCoreOperation(@NonNull final CoreValuePut<U> value) {
        return Observable
                .fromCallable(() -> {
                    Uri uri = value.uri();

                    lock(uri);

                    final Cursor cursor = contentResolver.query(uri, getProjection(), null, null, null);

                    if (cursor == null || !cursor.moveToFirst()) {
                        if (cursor != null) {
                            cursor.close();
                        }

                        Log.v(TAG, "Create insertion contentOperation for " + uri);
                        return value.toInsertOperation(getContentValuesForItem(value.item()));
                    }

                    final U currentItem = read(cursor);
                    final U newItem = mergedItem(currentItem, value.item());

                    cursor.close();

                    if (!newItem.equals(currentItem)) {
                        Log.v(TAG, "Create update contentOperation for " + uri);
                        return value.toUpdateOperation(getContentValuesForItem(newItem));
                    }

                    Log.v(TAG, "Data already up to date at " + uri);
                    return value.noOperation();
                });
    }

    private void releaseIfNoOp(@NonNull final CoreOperation operation) {
        if (!operation.isValid()) {
            release(new CoreOperationResult(operation, false));
        }
    }

    private void lock(@NonNull final Uri uri) throws InterruptedException {
        locker.acquire(uri);
    }

    private void release(@NonNull final CoreOperationResult operation) {
        try {
            locker.release(operation.uri());
            // Remove the operation completion listener and emit whether the operation was executed.
            completionNotifiers.remove(operation.id()).onNext(operation.success());
        } catch (IllegalStateException e) {
            // Release may throw if the lock wasn't successfully acquired.
            Log.w(TAG, "Couldn't release lock!", e);
        }
    }

    @NonNull
    private U mergedItem(@NonNull final U currentItem, @NonNull final U newItem) {
        if (newItem.equals(currentItem)) {
            return newItem;
        }

        Log.v(TAG, "Merging values");
        return mergeValues(currentItem, newItem);
    }

    @NonNull
    protected Single<Boolean> put(@NonNull final Uri uri, @NonNull final U item) {
        checkNotNull(item);
        checkNotNull(uri);

        return createModifyingOperation(index -> CoreValuePut.create(index, uri, item));
    }

    @NonNull
    protected Single<Boolean> delete(@NonNull final Uri uri) {
        checkNotNull(uri);

        return createModifyingOperation(index -> CoreValueDelete.create(index, uri));
    }

    @NonNull
    private Single<Boolean> createModifyingOperation(@NonNull final Func1<Integer, CoreValue<U>> valueFunc) {
        int index = ++nextOperationIndex;

        completionNotifiers.put(index, PublishSubject.create());
        operationSubject.onNext(valueFunc.call(index));

        return completionNotifiers.get(index)
                .first()
                .toSingle();
    }

    @NonNull
    protected Observable<List<U>> getAllOnce(@NonNull final Uri uri) {
        checkNotNull(uri);

        return Observable.fromCallable(() -> queryList(uri))
                .subscribeOn(Schedulers.io());
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
