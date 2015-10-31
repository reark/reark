package com.tehmou.rxandroidarchitecture.data.base.store;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tehmou.rxandroidarchitecture.utils.Preconditions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 26/04/15.
 */
abstract public class SingleItemContentProviderStoreBase<T, U> extends ContentProviderStoreBase<T> {
    private static final String TAG = SingleItemContentProviderStoreBase.class.getSimpleName();

    final private ConcurrentMap<Uri, Subject<T, T>> subjectMap = new ConcurrentHashMap<>();

    public SingleItemContentProviderStoreBase(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
        Preconditions.checkNotNull(contentResolver, "Content Resolver cannot be null.");
    }

    @NonNull
    @Override
    protected ContentObserver getContentObserver() {
        return new ContentObserver(createHandler(this.getClass().getSimpleName())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                Log.v(TAG, "onChange(" + uri + ")");

                if (subjectMap.containsKey(uri)) {
                    subjectMap.get(uri).onNext(queryOne(uri));
                }
            }
        };
    }

    public void put(@NonNull T item) {
        Preconditions.checkNotNull(item, "Item cannot be null.");

        insertOrUpdate(item, getUriForItem(item));
    }

    @NonNull
    public Observable<T> getStream(@NonNull U id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        Log.v(TAG, "getStream(" + id + ")");
        final T item = query(id);
        final Observable<T> observable = lazyGetSubject(id);
        if (item != null) {
            Log.v(TAG, "Found existing item for id=" + id);
            return observable.startWith(item);
        }
        return observable;
    }

    @NonNull
    private Observable<T> lazyGetSubject(@NonNull U id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        Log.v(TAG, "lazyGetSubject(" + id + ")");
        final Uri uri = getUriForKey(id);
        subjectMap.putIfAbsent(uri, PublishSubject.<T>create());
        return subjectMap.get(uri);
    }

    @Nullable
    protected T query(@NonNull U id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        final Uri uri = getUriForKey(id);
        return queryOne(uri);
    }

    @NonNull
    private Uri getUriForItem(@NonNull T item) {
        Preconditions.checkNotNull(item, "Item cannot be null.");
        return getUriForKey(getIdFor(item));
    }

    @NonNull
    abstract public Uri getUriForKey(@NonNull U id);

    @NonNull
    abstract protected U getIdFor(@NonNull T item);
}
