package com.tehmou.rxbookapp.data.base.store;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.tehmou.rxbookapp.data.base.contract.DatabaseContract;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 26/04/15.
 */
abstract public class ContentProviderStoreBase<T, U> {
    private static final String TAG = ContentProviderStoreBase.class.getSimpleName();

    final protected ContentResolver contentResolver;
    final private Map<Uri, Subject<T, T>> subjectMap = new HashMap<>();
    final private DatabaseContract<T> databaseContract;

    public ContentProviderStoreBase(ContentResolver contentResolver,
                                    DatabaseContract<T> databaseContract) {
        this.contentResolver = contentResolver;
        this.databaseContract = databaseContract;
        this.contentResolver.registerContentObserver(
                getContentUri(), true, contentObserver);
    }

    final private ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.v(TAG, "onChange(" + uri + ")");
            if (subjectMap.containsKey(uri)) {
                subjectMap.get(uri).onNext(query(uri));
            }
        }
    };

    public void put(T item) {
        insertOrUpdate(item);
    }

    public Observable<T> getStream(U id) {
        Log.v(TAG, "getStream(" + id + ")");
        final T item = query(id);
        final Observable<T> observable = lazyGetSubject(id);
        if (item != null) {
            Log.v(TAG, "Found existing item for id=" + id);
            return observable.startWith(item);
        }
        return observable;
    }

    private Observable<T> lazyGetSubject(U id) {
        Log.v(TAG, "lazyGetSubject(" + id + ")");
        Uri uri = getUriForId(id);
        if (!subjectMap.containsKey(uri)) {
            Log.v(TAG, "Creating subject for id=" + id);
            subjectMap.put(uri, PublishSubject.<T>create());
        }
        return subjectMap.get(uri);
    }

    public void insertOrUpdate(T item) {
        Uri uri = getUriForId(getIdFor(item));
        ContentValues values = getContentValuesForItem(item);
        if (contentResolver.update(uri, values, null, null) == 0) {
            final Uri resultUri = contentResolver.insert(uri, values);
            Log.v(TAG, "Inserted at " + resultUri);
        } else {
            Log.v(TAG, "Updated at " + uri);
        }
    }

    protected T query(U id) {
        return query(getUriForId(id));
    }

    protected T query(Uri uri) {
        Cursor cursor = contentResolver.query(uri,
                databaseContract.getProjection(), null, null, null);
        T value = null;
        if (cursor != null) {
            value = databaseContract.read(cursor);
            cursor.close();
        }
        if (value == null) {
            Log.v(TAG, "Could not find with id: " + uri);
        }
        Log.d(TAG, "" + value);
        return value;
    }

    protected ContentValues getContentValuesForItem(T item) {
        return databaseContract.getContentValuesForItem(item);
    }

    protected Uri getUriForId(U id) {
        return Uri.withAppendedPath(getContentUri(), id.toString());
    }

    abstract protected U getIdFor(T item);
    abstract protected Uri getContentUri();
}
