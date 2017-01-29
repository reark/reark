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

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.reark.reark.data.stores.StoreItem;
import io.reark.reark.data.stores.interfaces.StoreCoreInterface;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import rx.Observable;
import rx.subjects.PublishSubject;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * ContentProviderStore is a convenience implementation of ContentProviderStoreCoreBase for
 * uniquely identifiable data types. The class provides a high-level API with id based get/put
 * semantics and access to the content provider updates via a non-completing Observable.
 *
 * Data stores should extend this abstract class to provide type specific data serialization,
 * comparison, id mapping, and content provider projection.
 *
 * @param <T> Type of the id used in this store core.
 * @param <U> Type of the data this store core contains.
 */
public abstract class ContentProviderStoreCore<T, U>
        extends ContentProviderStoreCoreBase<U> implements StoreCoreInterface<T, U> {

    private static final String TAG = ContentProviderStoreCore.class.getSimpleName();

    @NonNull
    private final PublishSubject<StoreItem<T, U>> subjectCache = PublishSubject.create();

    protected ContentProviderStoreCore(@NonNull final ContentResolver contentResolver) {
        super(contentResolver);
    }

    protected ContentProviderStoreCore(@NonNull final ContentResolver contentResolver,
                                       final int groupingTimeout,
                                       final int groupMaxSize) {
        super(contentResolver, groupingTimeout, groupMaxSize);
    }

    @NonNull
    @Override
    protected ContentObserver getContentObserver() {
        return new ContentObserver(createHandler(getClass().getSimpleName())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);

                getOnce(uri)
                        .doOnNext(item -> Log.v(TAG, format("onChange(%1s)", uri)))
                        .map(item -> new StoreItem<>(getIdForUri(uri), item))
                        .subscribe(subjectCache::onNext,
                                   error -> Log.e(TAG, "Cannot retrieve the item: " + uri, error));
            }
        };
    }

    /**
     * Get a full stream of items with no identifier filtering. Whenever a store receives a new
     * item with the id, it pushes it to the stream.
     *
     * @return An observable that first emits all future items as they are inserted into the store.
     */
    @NonNull
    protected Observable<StoreItem<T, U>> getStream() {
        return subjectCache.asObservable();
    }

    @Override
    public void put(@NonNull final T id, @NonNull final U item) {
        checkNotNull(id);

        put(Preconditions.get(item), getUriForId(id));
    }

    @NonNull
    @Override
    public Observable<U> getCached(@NonNull final T id) {
        checkNotNull(id);

        return getOnce(getUriForId(id));
    }

    @NonNull
    @Override
    public Observable<U> getStream(@NonNull final T id) {
        checkNotNull(id);

        return subjectCache
                .filter(item -> item.id().equals(id))
                .map(StoreItem::item)
                .asObservable();
    }

    /**
     * Returns unique Uri for the given id in the content provider of this store.
     *
     * @param id Store item id for which the Uri should be resolved.
     * @return Resolved content provider Uri.
     */
    @NonNull
    protected abstract Uri getUriForId(@NonNull final T id);

    /**
     * Returns id for the unique Uri in the content provider of this store.
     *
     * @param uri Content provider Uri for which the id should be resolved.
     * @return Resolved store item id.
     */
    @NonNull
    protected abstract T getIdForUri(@NonNull final Uri uri);

}
