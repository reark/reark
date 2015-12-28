package io.reark.reark.data.store;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

import static rx.Observable.concat;

/**
 * Created by ttuo on 26/04/15.
 */
abstract public class SingleItemContentProviderStore<T, U> extends ContentProviderStore<T> {

    private static final String TAG = SingleItemContentProviderStore.class.getSimpleName();

    @NonNull
    final private PublishSubject<StoreItem<T>> subjectCache = PublishSubject.create();

    public SingleItemContentProviderStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
    }

    @NonNull
    @Override
    protected ContentObserver getContentObserver() {
        return new ContentObserver(createHandler(getClass().getSimpleName())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);

                queryOne(uri)
                        .doOnNext(item -> Log.v(TAG, "onChange(" + uri + ')'))
                        .map(it -> new StoreItem<>(uri, it))
                        .subscribe(subjectCache);
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

        return concat(query(id).filter(it -> it != null),
                      getItemObservable(id))
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    private Observable<T> getItemObservable(@NonNull U id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return subjectCache
                .filter(it -> it.uri().equals(getUriForKey(id)))
                .doOnNext(it -> Log.v(TAG, "getItemObservable(" + it + ')'))
                .map(StoreItem::item);
    }

    @NonNull
    protected Observable<T> query(@NonNull U id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return queryOne(getUriForKey(id));
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
