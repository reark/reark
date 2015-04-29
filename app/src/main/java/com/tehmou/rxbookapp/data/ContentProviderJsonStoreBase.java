package com.tehmou.rxbookapp.data;

import com.tehmou.rxbookapp.data.provider.DatabaseContract;
import com.tehmou.rxbookapp.data.provider.SerializedJsonContract;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.lang.reflect.Type;

/**
 * Created by ttuo on 11/01/15.
 */
abstract public class ContentProviderJsonStoreBase<T, U> extends ContentProviderStoreBase<T, U> {
    private static final String TAG = ContentProviderJsonStoreBase.class.getSimpleName();

    final private DatabaseContract<T> databaseContract;
    final protected Type type;

    protected ContentProviderJsonStoreBase(ContentResolver contentResolver,
                                           DatabaseContract<T> databaseContract,
                                           Type type) {
        super(contentResolver);
        this.databaseContract = databaseContract;
        this.type = type;
    }

    @Override
    protected ContentValues getContentValuesForItem(T item) {
        return databaseContract.getContentValuesForItem(item);
    }

    @Override
    protected T query(Uri uri) {
        Cursor cursor = contentResolver.query(uri, SerializedJsonContract.PROJECTION, null, null, null);
        T value = null;
        if (cursor != null) {
            value = databaseContract.read(cursor, type);
            cursor.close();
        }
        if (value == null) {
            Log.v(TAG, "Could not find with id: " + uri);
        }
        Log.d(TAG, "" + value);
        return value;
    }

    @Override
    protected Uri getUriForId(U id) {
        return Uri.withAppendedPath(getContentUri(), id.toString());
    }
}
