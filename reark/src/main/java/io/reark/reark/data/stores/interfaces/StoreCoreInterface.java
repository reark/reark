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
package io.reark.reark.data.stores.interfaces;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * StoreCore is the underlying persistence mechanism of a store. It is not mandatory for a store to
 * use one, but the default implementations and base classes all use StoreCores for modularity.
 *
 * The idea behind StoreCore is that store logic is the higher-level part, whereas StoreCore is a
 * simple container that knows how to persist data in some form. This could be, for instance,
 * program memory or a content provider, but it could as well be direct disk I/O or
 * Android SharedPreferences.
 *
 * @param <T> Type of the id used in this store core.
 * @param <U> Type of the data this store core contains.
 */
public interface StoreCoreInterface<T, U> {
    /**
     * Takes an identifier and a item to be persisted. Unlike stores, StoreCores have no way of
     * deducing the id from the item, but it is left as responsibility of a store. Therefore it is
     * possible to persist even items that do not have an id (since the id is separately given).
     * This might be useful, for instance, if one wanted to make a store that contains data of type
     * List, in which there is no concept of an identifier.
     *
     * @param id Id of the persisted item.
     * @param item The persisted item.
     * @return Single that emits true if value was updated or inserted, and false otherwise.
     */
    @NonNull
    Single<Boolean> put(@NonNull final T id, @NonNull final U item);

    /**
     * Takes an identifier to be deleted, and returns Single that emits when the operation has been
     * executed.
     *
     * @param id Id of the persisted item.
     * @return Single that emits true if value was deleted, and false otherwise.
     */
    @NonNull
    Single<Boolean> delete(@NonNull final T id);

    /**
     * Takes an identifier and returns an Observable that emits that item as soon as it is read from
     * the underlying persisting structure (in case the store uses an in-memory strategy this could
     * mean the item is emitted synchronously). In case no item with the specified id is available,
     * the Observable completes without emitting any items.
     *
     * @param id Identifier for the data item to be retrieved from cache.
     * @return Maybe that emits the data item for the given id, or in case no data item is in
     * the cache, simply completes without emitting any items.
     */
    @NonNull
    Maybe<U> getCached(@NonNull final T id);

    /**
     * Returns an Observable that emits all items from the underlying persisting structure (in case
     * the store uses an in-memory strategy this could mean the items are emitted synchronously).
     * In case no items exist, the Observable completes without emitting any items.
     *
     * @return Single that emits a list all data items and completes. In case no items are
     * in the cache, an empty list is emitted.
     */
    @NonNull
    Single<List<U>> getCached();

    /**
     * Takes an identifier and returns an Observable that emits all matching _future_ items that are
     * put into the core. Unlike most store getStream equivalents, the StoreCore getStream does not
     * attempt to insert the last cached value into the stream. This is simply a stream for all
     * future data items.
     *
     * @param id Identifier for the stream of data items.
     * @return An Observable that does not immediately return anything, but emits all future items
     * matching the given identifier that are put into the core.
     */
    @NonNull
    Observable<U> getStream(@NonNull final T id);

    /**
     * Returns an Observable that emits all _future_ items that are put into the core. Unlike most
     * store getStream equivalents, the StoreCore getStream does not attempt to insert the last
     * cached value into the stream. This is simply a stream for all future data items.
     *
     * @return An Observable that does not immediately return anything, but emits all future items
     * that are put into the core.
     */
    @NonNull
    Observable<U> getStream();
}
