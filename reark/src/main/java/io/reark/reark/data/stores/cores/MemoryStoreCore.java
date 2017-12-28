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

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import io.reark.reark.data.stores.StoreItem;
import io.reark.reark.data.stores.interfaces.StoreCoreInterface;
import io.reark.reark.utils.Log;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

/**
 * A simple StoreCore that only uses an in-memory ConcurrentHashMap to persist the data. This means
 * that the MemoryStoreCore cannot be shared across Android processes and it will be destroyed with
 * the app.
 *
 * @param <T> Type of the id used in this store core.
 * @param <U> Type of the data this store core contains.
 */
public class MemoryStoreCore<T, U> implements StoreCoreInterface<T, U> {
    private static final String TAG = MemoryStoreCore.class.getSimpleName();

    @NonNull
    private final BiFunction<U, U, U> putMergeFunction;

    @NonNull
    private final Map<Integer, U> cache = new ConcurrentHashMap<>(10);

    @NonNull
    private final PublishSubject<StoreItem<T, U>> subject = PublishSubject.create();

    @NonNull
    private final ConcurrentMap<Integer, Subject<U>> subjectCache = new ConcurrentHashMap<>(20, 0.75f, 4);

    public MemoryStoreCore() {
        this((v1, v2) -> v2);
    }

    public MemoryStoreCore(@NonNull final BiFunction<U, U, U> putMergeFunction) {
        this.putMergeFunction = get(putMergeFunction);
    }

    @NonNull
    @Override
    public Maybe<U> getCached(@NonNull final T id) {
        checkNotNull(id);

        final U value = cache.get(getHashCodeForId(id));

        return value == null
                ? Maybe.empty()
                : Maybe.just(value);
    }

    @NonNull
    @Override
    public Single<List<U>> getCached() {
        return Observable.fromIterable(cache.keySet())
                .map(cache::get)
                .toList();
    }

    @NonNull
    @Override
    public Observable<U> getStream(@NonNull final T id) {
        checkNotNull(id);

        int hash = getHashCodeForId(id);
        subjectCache.putIfAbsent(hash, PublishSubject.create());

        return subjectCache.get(hash);
    }

    @NonNull
    @Override
    public Observable<U> getStream() {
        return subject.map(StoreItem::item);
    }

    @NonNull
    @Override
    public Single<Boolean> put(@NonNull final T id, @NonNull final U item) {
        checkNotNull(id);
        checkNotNull(item);

        final int hash = getHashCodeForId(id);
        U newItem = item;
        boolean valuesEqual = false;

        if (cache.containsKey(hash)) {
            final U currentItem = cache.get(hash);

            valuesEqual = newItem.equals(currentItem);

            if (!valuesEqual) {
                Log.v(TAG, "Merging values at " + id);
                try {
                    newItem = putMergeFunction.apply(currentItem, newItem);
                } catch (Exception e) {
                    return Single.error(e);
                }
                valuesEqual = newItem.equals(currentItem);
            }
        }

        if (valuesEqual) {
            Log.v(TAG, "Data already up to date at " + id);
            return Single.just(false);
        }

        cache.put(hash, newItem);
        subject.onNext(new StoreItem<>(id, newItem));

        if (subjectCache.containsKey(hash)) {
            subjectCache.get(hash).onNext(newItem);
        }

        return Single.just(true);
    }

    @NonNull
    @Override
    public Single<Boolean> delete(@NonNull final T id) {
        return Single.fromCallable(() -> cache.remove(getHashCodeForId(id)) != null);
    }

    protected int getHashCodeForId(@NonNull final T id) {
        checkNotNull(id);

        return id.hashCode();
    }
}
