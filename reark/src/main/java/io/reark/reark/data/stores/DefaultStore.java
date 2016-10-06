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

import android.support.annotation.NonNull;

import io.reark.reark.data.stores.cores.StoreCoreInterface;
import io.reark.reark.utils.Preconditions;
import rx.Observable;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

/**
 * DefaultStore is a simple implementation of store logic. It can be used with any data types by
 * providing a function for deducing the id of an item. This could be done, for instance, with
 * T getId(U item).
 *
 * The DefaultStore works with any StoreCore instance.
 *
 * @param <T> Type of the id used in this store.
 * @param <U> Type of the data this store contains.
 */
public class DefaultStore<T, U> implements StoreInterface<T, U> {

    @NonNull
    private final StoreCoreInterface<T, U> core;

    @NonNull
    private final GetIdForItem<T, U> getIdForItem;

    public DefaultStore(@NonNull final StoreCoreInterface<T, U> core,
                        @NonNull final GetIdForItem<T, U> getIdForItem) {
        this.core = get(core);
        this.getIdForItem = get(getIdForItem);
    }

    @Override
    public void put(@NonNull final U item) {
        checkNotNull(item);

        core.put(getIdForItem.call(item), item);
    }

    @NonNull
    @Override
    public Observable<U> getOnce(@NonNull final T id) {
        checkNotNull(id);

        return core.getCached(id);
    }

    @NonNull
    @Override
    public Observable<U> getOnceAndStream(@NonNull final T id) {
        checkNotNull(id);

        return Observable.concat(
                getOnce(id),
                core.getStream(id));
    }

    public interface GetIdForItem<T, U> {
        T call(U item);
    }
}
