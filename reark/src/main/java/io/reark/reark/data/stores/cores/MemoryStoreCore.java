package io.reark.reark.data.stores.cores;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reark.reark.data.stores.StoreItem;
import io.reark.reark.utils.Log;
import rx.Observable;
import rx.functions.Func2;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 27/06/16.
 */
public class MemoryStoreCore<T, U> implements StoreCoreInterface<T, U> {
    private static final String TAG = MemoryStoreCore.class.getSimpleName();

    private final Func2<U, U, U> putMergeFunction;
    private final Map<Integer, U> cache = new ConcurrentHashMap<>();
    private final Subject<StoreItem<T, U>, StoreItem<T, U>> subject = PublishSubject.create();
    private final ConcurrentMap<Integer, Subject<U, U>> subjectCache = new ConcurrentHashMap<>(20, 0.75f, 4);

    public MemoryStoreCore() {
        this((v1, v2) -> v2);
    }

    public MemoryStoreCore(Func2<U, U, U> putMergeFunction) {
        this.putMergeFunction = putMergeFunction;
    }

    protected Observable<StoreItem<T, U>> getStream() {
        return subject.asObservable();
    }

    public Observable<U> getStream(T id) {
        int hash = getHashCodeForId(id);
        subjectCache.putIfAbsent(hash, PublishSubject.<U>create());
        return subjectCache.get(hash)
                .asObservable();
    }

    public void put(T id, U item) {
        final int hash = getHashCodeForId(id);
        boolean valuesEqual = false;

        if (cache.containsKey(hash)) {
            U currentItem = cache.get(hash);

            valuesEqual = item.equals(currentItem);

            if (!valuesEqual) {
                Log.v(TAG, "Merging values at " + id);
                item = putMergeFunction.call(currentItem, item);
                valuesEqual = item.equals(currentItem);
            }
        }

        if (valuesEqual) {
            Log.v(TAG, "Data already up to date at " + id);
            return;
        }

        cache.put(hash, item);
        subject.onNext(new StoreItem<>(id, item));
        if (subjectCache.containsKey(hash)) {
            subjectCache.get(hash).onNext(item);
        }
    }

    public Observable<U> getCached(T id) {
        return Observable.just(cache.get(getHashCodeForId(id)));
    }

    protected int getHashCodeForId(T id) {
        return id.hashCode();
    }
}
