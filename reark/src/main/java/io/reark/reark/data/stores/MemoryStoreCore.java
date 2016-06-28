package io.reark.reark.data.stores;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 27/06/16.
 */
public class MemoryStoreCore<T, U> {
    private final Map<Integer, U> cache = new ConcurrentHashMap<>();
    private final Subject<StoreItem<T, U>, StoreItem<T, U>> subject = PublishSubject.create();
    private final ConcurrentMap<Integer, Subject<U, U>> subjectCache = new ConcurrentHashMap<>(20, 0.75f, 4);

    protected Observable<StoreItem<T, U>> getStream() {
        return subject.asObservable();
    }

    protected Observable<U> getStream(T id) {
        int hash = getHashCodeForId(id);
        subjectCache.putIfAbsent(hash, PublishSubject.<U>create());
        return subjectCache.get(hash)
                .asObservable();
    }

    protected void put(T id, U item) {
        int hash = getHashCodeForId(id);
        cache.put(hash, item);
        subject.onNext(new StoreItem<>(id, item));
        if (subjectCache.containsKey(hash)) {
            subjectCache.get(hash).onNext(item);
        }
    }

    protected U get(T id) {
        return cache.get(getHashCodeForId(id));
    }

    protected boolean contains(T id) {
        return cache.containsKey(getHashCodeForId(id));
    }

    protected int getHashCodeForId(T id) {
        return id.hashCode();
    }
}
