package io.reark.reark.data.store;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 26/04/15.
 */
abstract public class SingleItemContentProviderStore<T, U> extends ContentProviderStore<T> {
    private static final String TAG = SingleItemContentProviderStore.class.getSimpleName();

    final private ConcurrentMap<Uri, Subject<T, T>> subjectMap = new ConcurrentHashMap<>();

    public SingleItemContentProviderStore(@NonNull ContentResolver contentResolver) {
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
                    queryOne(uri).subscribe(t -> subjectMap.get(uri).onNext(t));
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

        return query(id)
                .flatMap(item -> {
                    final Observable<T> observable = lazyGetSubject(id);
                    if (item != null) {
                        Log.v(TAG, "Found existing item for id=" + id);
                        return observable.startWith(item);
                    }
                    return observable;
                })
                .subscribeOn(AndroidSchedulers.mainThread());

    }

    @NonNull
    private Observable<T> lazyGetSubject(@NonNull U id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        Log.v(TAG, "lazyGetSubject(" + id + ")");
        final Uri uri = getUriForKey(id);
        subjectMap.putIfAbsent(uri, PublishSubject.<T>create());
        return subjectMap.get(uri);
    }

    @NonNull
    protected Observable<T> query(@NonNull U id) {
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
