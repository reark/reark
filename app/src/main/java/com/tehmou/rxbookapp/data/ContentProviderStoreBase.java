package com.tehmou.rxbookapp.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tehmou.rxbookapp.data.provider.GitHubRepositoryContract;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

import java.lang.reflect.Type;
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
    final private Type type;

    public ContentProviderStoreBase(ContentResolver contentResolver,
                                    Type type) {
        this.contentResolver = contentResolver;
        this.type = type;
    }

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

    protected void insertOrUpdate(T item) {
        Uri uri = Uri.withAppendedPath(GitHubRepositoryContract.CONTENT_URI, "" + getIdFor(item));
        ContentValues values = new ContentValues();
        values.put(GitHubRepositoryContract.ID, getIdFor(item).toString());
        values.put(GitHubRepositoryContract.JSON, new Gson().toJson(item));
        if (contentResolver.update(uri, values, null, null) == 0) {
            final Uri resultUri = contentResolver.insert(uri, values);
            Log.v(TAG, "Inserted at " + resultUri);
        } else {
            Log.v(TAG, "Updated at " + uri);
        }
    }

    protected T query(U id) {
        Uri uri = Uri.withAppendedPath(GitHubRepositoryContract.CONTENT_URI, "" + id);
        Cursor cursor = contentResolver.query(uri, GitHubRepositoryContract.PROJECTION, null, null, null);
        T gitHubRepository = null;
        if (cursor.moveToFirst()) {
            final String json = cursor.getString(cursor.getColumnIndex(GitHubRepositoryContract.JSON));
            gitHubRepository = new Gson().fromJson(json, type);
        } else {
            Log.e(TAG, "Could not find with id: " + id);
        }
        cursor.close();
        Log.d(TAG, "" + gitHubRepository);
        return gitHubRepository;
    }

    abstract protected U getIdFor(T item);
}
