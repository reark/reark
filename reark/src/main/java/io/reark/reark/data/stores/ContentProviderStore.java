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

import io.reark.reark.data.stores.cores.ContentProviderStoreCore;
import io.reark.reark.utils.Preconditions;

/**
 * ContentProviderStore is a convenience implementation of ContentProviderStoreCoreBase for
 * uniquely identifiable data types. The class provides a high-level API with id based get/put
 * semantics and access to the content provider updates via a non-completing Observable.
 *
 * Data stores should extend this abstract class to provide type specific data serialization,
 * comparison, id mapping, and content provider projection.
 *
 * @param <T> Type of the id used in this store.
 * @param <U> Type of the data this store contains.
 * @param <R> Non-null type or wrapper for the data this store contains.
 */
public class ContentProviderStore<T, U, R> extends DefaultStore<T, U, R> {

    @NonNull
    private final ContentProviderStoreCore<T, U> core;

    protected ContentProviderStore(@NonNull final ContentProviderStoreCore<T, U> core,
                                   @NonNull final GetIdForItem<T, U> getIdForItem,
                                   @NonNull final GetNullSafe<U, R> getNullSafe,
                                   @NonNull final GetEmptyValue<R> getEmptyValue) {
        super(core, getIdForItem, getNullSafe, getEmptyValue);

        this.core = Preconditions.get(core);
    }
}
