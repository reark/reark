package com.tehmou.rxbookapp.data;

import com.google.gson.Gson;

import com.tehmou.rxbookapp.data.provider.SerializedJsonContract;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 11/01/15.
 */
abstract public class ContentProviderJsonStoreBase<T, U> extends ContentProviderStoreBase<T, U> {
    private static final String TAG = ContentProviderJsonStoreBase.class.getSimpleName();

    final protected Type type;

    protected ContentProviderJsonStoreBase(ContentResolver contentResolver, Type type) {
        super(contentResolver);
        this.type = type;
    }

    @Override
    protected ContentValues getValuesForItem(T item) {
        ContentValues values = new ContentValues();
        values.put(SerializedJsonContract.ID, getIdFor(item).toString());
        values.put(SerializedJsonContract.JSON, new Gson().toJson(item));
        return values;
    }

    @Override
    protected T query(Uri uri) {
        Cursor cursor = contentResolver.query(uri, SerializedJsonContract.PROJECTION, null, null, null);
        T value = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                final String json = cursor.getString(cursor.getColumnIndex(SerializedJsonContract.JSON));
                value = new Gson().fromJson(json, type);
            } else {
                Log.e(TAG, "Could not find with id: " + uri);
            }
            cursor.close();
        }
        Log.d(TAG, "" + value);
        return value;
    }

    @Override
    protected Uri getUriForId(U id) {
        return Uri.withAppendedPath(getContentUri(), id.toString());
    }
}
