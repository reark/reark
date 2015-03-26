package com.tehmou.rxbookapp.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 11/01/15.
 */
public class HashStoreBase<T> {
    static private int counter = 0;

    private static final String TAG = HashStoreBase.class.getSimpleName();
    final private Map<String, T> hash = new ConcurrentHashMap<>();
    final private Map<String, Subject<T, T>> subjectsHash = new ConcurrentHashMap<>();

    public String put(T item) {
        String key = "" + counter++;
        hash.put(key, item);
        return key;
    }

    public void put(String search, T item) {
        hash.put(search, item);
        if (subjectsHash.containsKey(search)) {
            subjectsHash.get(search).onNext(item);
        }
    }

    public Observable<T> getStream(String id) {
        if (!subjectsHash.containsKey(id)) {
            subjectsHash.put(id, PublishSubject.create());
        }
        if (hash.containsKey(id)) {
            // Give the last value we have to the subscriber immediately.
            // We do this with BehaviorSubject.
            final Subject<T, T> subject = BehaviorSubject.create(hash.get(id));
            subjectsHash.get(id).subscribe(subject);
            return subject;
        } else {
            // Return the subject that will give the subscriber all
            // subsequent updates to the store value.
            return subjectsHash.get(id);
        }
    }
}
