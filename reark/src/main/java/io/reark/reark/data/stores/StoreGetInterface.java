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

import rx.Observable;

/**
 * Interface for stores from which one can get data in the form of a single item or a stream of
 * items. All store operations are asynchronous, and thus we always return observables. This is to
 * enable StoreCores that use asynchronous methods in their persistence.
 *
 * @param <T> Type of the id used in this store.
 * @param <U> Type of the data this store contains.
 */
public interface StoreGetInterface<T, U> {
    /**
     * Get the latest item in the store with a specific identifier. The returned observable always
     * completes, unlike in its sibling getOnceAndStream.
     *
     * @param id The identifier of the requested object, as defined by the store.
     * @return An observable that either returns the latest item with the requested id and
     * completes, or in case no item is found, just an empty completing observable.
     */
    Observable<U> getOnce(T id);

    /**
     * Get a full stream of items with the specified identifier. Whenever a store receives a new
     * item with the id, it pushes it to the stream.
     *
     * Depending on the store implementation, it is recommended that getStreams emits the last item
     * _immediately_ as part of the stream. This, however, depends on the desired behavior. We
     * recommend using the same kind of behavior across your app. See, for instance, DefaultStore.
     *
     * @param id The identifier of the requested object, as defined by the store.
     * @return An observable that first emits the latest item, if it has one, immediately as the
     * first item of the observable, and then emits all further items with the same id as they are
     * inserted into the store.
     */
    Observable<U> getOnceAndStream(T id);
}
