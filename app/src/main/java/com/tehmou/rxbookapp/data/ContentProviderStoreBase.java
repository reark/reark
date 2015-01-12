package com.tehmou.rxbookapp.data;

import android.content.ContentResolver;
import android.content.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 11/01/15.
 */
abstract public class ContentProviderStoreBase<T, U> {
    private static final String TAG = ContentProviderStoreBase.class.getSimpleName();

    final protected ContentResolver contentResolver;

    public ContentProviderStoreBase(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    abstract protected void insertOrUpdate(T item);
    abstract protected T query(U id);

    public void put(T item) {
        insertOrUpdate(item);
    }

    public Observable<T> getStream(U id) {
        T item = query(id);
        if (item != null) {
            return Observable.just(item);
        }
        return PublishSubject.create();
    }
}
