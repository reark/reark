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
package io.reark.reark.data.stores;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static java.lang.String.format;
import static rx.Observable.concat;

/**
 * SingleItemContentProviderStore is a convenience implementation of ContentProviderStore for
 * uniquely identifiable data types. The class provides a high-level API with id based get/put
 * semantics and access to the content provider updates via a non-completing Observable.
 *
 * Data stores should extend this abstract class to provide type specific data serialization,
 * comparison, id mapping, and content provider projection.
 *
 * @param <T> Type of the data this store contains.
 * @param <U> Type of the id used in this store.
 */
public abstract class SingleItemContentProviderStore<T, U> extends ContentProviderStore<T>
        implements StoreInterface<U, T> {
    private static final String TAG = SingleItemContentProviderStore.class.getSimpleName();

    @NonNull
    private final PublishSubject<StoreItem<T>> subjectCache = PublishSubject.create();

    protected SingleItemContentProviderStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
    }

    @NonNull
    @Override
    protected ContentObserver getContentObserver() {
        return new ContentObserver(createHandler(getClass().getSimpleName())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);

                getOne(uri)
                        .doOnNext(item -> Log.v(TAG, format("onChange(%1s)", uri)))
                        .map(item -> new StoreItem<>(uri, item))
                        .subscribe(subjectCache::onNext,
                                   error -> Log.e(TAG, "Cannot retrieve the item: " + uri, error));
            }
        };
    }

    /**
     * Inserts the given item to the store, or updates the store if an item with the same id
     * already exists.
     *
     * Any open stream Observables for the item's id will emit this new value.
     */
    public void put(@NonNull T item) {
        Preconditions.checkNotNull(item, "Item cannot be null.");

        put(item, getUriForItem(item));
    }

    /**
     * Returns a completing Observable of all items matching the id.
     *
     * This method can for example be used to request all the contents of this store
     * by providing an empty id.
     */
    @NonNull
    public Observable<List<T>> get(@NonNull U id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        final Uri uri = getUriForId(id);
        return get(uri);
    }

    /**
     * Returns a completing Observable that either emits the first existing item in the store
     * matching the id, or emits null if the store does not contain the requested id.
     */
    @NonNull
    public Observable<T> getOne(@NonNull U id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        final Uri uri = getUriForId(id);
        return getOne(uri);
    }

    /**
     * Returns a non-completing Observable of all new and updated items.
     */
    @NonNull
    public Observable<T> getStream() {
        Log.v(TAG, "getStream()");

        return subjectCache.map(StoreItem::item);
    }

    /**
     * Returns a non-completing Observable of items matching the id. The Observable emits the first
     * matching item existing in the store, if any, and continues to emit all new matching items.
     */
    @NonNull
    public Observable<T> getStream(@NonNull U id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");
        Log.v(TAG, "getStream(" + id + ")");

        return concat(getOne(id).filter(item -> item != null),
                      getItemObservable(id))
                .subscribeOn(Schedulers.computation());
    }

    /**
     * Returns unique Uri for the given id in the content provider of this store.
     */
    @NonNull
    protected abstract Uri getUriForId(@NonNull U id);

    @NonNull
    private Observable<T> getItemObservable(@NonNull U id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return subjectCache
                .filter(item -> item.uri().equals(getUriForId(id)))
                .doOnNext(item -> Log.v(TAG, "getItemObservable(" + item + ')'))
                .map(StoreItem::item);
    }

    @NonNull
    private Uri getUriForItem(@NonNull T item) {
        Preconditions.checkNotNull(item, "Item cannot be null.");

        return getUriForId(getIdFor(item));
    }

    @NonNull
    protected abstract U getIdFor(@NonNull T item);

}
