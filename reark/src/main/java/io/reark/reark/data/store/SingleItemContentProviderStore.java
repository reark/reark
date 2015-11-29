package io.reark.reark.data.store;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

import static java.lang.String.format;
import static rx.Observable.concat;

/**
 * Created by ttuo on 26/04/15.
 */
public abstract class SingleItemContentProviderStore<T, U> extends ContentProviderStore<T> {

    private static final String TAG = SingleItemContentProviderStore.class.getSimpleName();

    @NonNull
    private final PublishSubject<StoreItem<T>> subjectCache = PublishSubject.create();

    protected SingleItemContentProviderStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
    }

    @NonNull
    @Override
    protected ContentObserver getContentObserver() {
        return new ContentObserver(createHandler(getClass().getSimpleName())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);

                getOne(uri)
                        .doOnNext(item -> Log.v(TAG, format("onChange(%1s)", uri)))
                        .map(it -> new StoreItem<>(uri, it))
                        .subscribe(subjectCache::onNext,
                                   error -> Log.e(TAG, "Cannot retrieve the item: " + uri, error));
            }
        };
    }

    public void put(@NonNull T item) {
        Preconditions.checkNotNull(item, "Item cannot be null.");

        put(item, getUriForItem(item));
    }

    @NonNull
    public Observable<List<T>> get(@NonNull U id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        final Uri uri = getUriForKey(id);
        return get(uri);
    }

    @NonNull
    public Observable<T> getOne(@NonNull U id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        final Uri uri = getUriForKey(id);
        return getOne(uri);
    }

    @NonNull
    public Observable<T> getStream(@NonNull U id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");
        Log.v(TAG, "getStream(" + id + ")");

        return concat(getOne(id).filter(it -> it != null),
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
    private Uri getUriForItem(@NonNull T item) {
        Preconditions.checkNotNull(item, "Item cannot be null.");

        return getUriForKey(getIdFor(item));
    }

    @NonNull
    public abstract Uri getUriForKey(@NonNull U id);

    @NonNull
    protected abstract U getIdFor(@NonNull T item);

}
