package io.reark.rxgithubapp.basic.data.stores;

import android.support.v4.util.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 27/06/16.
 */
public class MemoryStoreCore<T, U> {
    private final Map<Integer, T> cache = new ConcurrentHashMap<>();
    private final Subject<Pair<U, T>, Pair<U, T>> subject = PublishSubject.create();
    private final ConcurrentMap<Integer, Subject<T, T>> subjectCache = new ConcurrentHashMap<>(20, 0.75f, 4);

    protected Observable<Pair<U, T>> getStreamWithIds() {
        return subject
                .asObservable()
                .onBackpressureBuffer();
    }

    protected Observable<T> getStream() {
        return subject
                .asObservable()
                .map(pair -> pair.second)
                .onBackpressureBuffer();
    }

    protected Observable<T> getStream(U id) {
        int hash = getHashCodeForId(id);
        if (!subjectCache.containsKey(hash)) {
            if (cache.containsKey(hash)) {
                subjectCache.put(hash, BehaviorSubject.create(cache.get(hash)));
            } else {
                subjectCache.put(hash, BehaviorSubject.create());
            }
        }
        return subjectCache.get(hash);
    }

    protected void put(U id, T item) {
        int hash = getHashCodeForId(id);
        cache.put(hash, item);
        subject.onNext(new Pair<>(id, item));
        if (subjectCache.containsKey(hash)) {
            subjectCache.get(hash).onNext(item);
        }
    }

    protected T get(U id) {
        return cache.get(getHashCodeForId(id));
    }

    protected boolean contains(U id) {
        return cache.containsKey(getHashCodeForId(id));
    }

    protected int getHashCodeForId(U id) {
        return id.hashCode();
    }
}
