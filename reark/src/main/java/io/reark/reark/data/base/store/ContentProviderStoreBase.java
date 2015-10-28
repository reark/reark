package io.reark.reark.data.base.store;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reark.reark.utils.Preconditions;

/**
 * Created by ttuo on 26/04/15.
 */
public abstract class ContentProviderStoreBase<T> {
    private static final String TAG = ContentProviderStoreBase.class.getSimpleName();

    @NonNull
    final protected ContentResolver contentResolver;

    @NonNull
    private final ContentObserver contentObserver = getContentObserver();

    public ContentProviderStoreBase(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
        this.contentResolver.registerContentObserver(
                getContentUri(), true, contentObserver);
    }

    @NonNull
    protected static Handler createHandler(String name) {
        HandlerThread handlerThread = new HandlerThread(name);
        handlerThread.start();
        return new Handler(handlerThread.getLooper());
    }

    protected void insertOrUpdate(T item, Uri uri) {
        Preconditions.checkNotNull(item, "Item to be inserted cannot be null");

        Log.v(TAG, "insertOrUpdate to " + uri);
        ContentValues values = getContentValuesForItem(item);
        Log.v(TAG, "values(" + values + ")");
        if (contentResolver.update(uri, values, null, null) == 0) {
            final Uri resultUri = contentResolver.insert(uri, values);
            Log.v(TAG, "Inserted at " + resultUri);
        } else {
            Log.v(TAG, "Updated at " + uri);
        }
    }

    @NonNull
    protected List<T> queryList(Uri uri) {
        Preconditions.checkNotNull(uri, "Uri cannot be null");

        Cursor cursor = contentResolver.query(uri,
                getProjection(), null, null, null);
        List<T> list = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                list.add(read(cursor));
            }
            while (cursor.moveToNext()) {
                list.add(read(cursor));
            }
            cursor.close();
        }
        if (list.size() == 0) {
            Log.v(TAG, "Could not find with uri: " + uri);
        }
        return list;
    }

    @Nullable
    protected T queryOne(Uri uri) {
        final List<T> queryResults = queryList(uri);

        if (queryResults.size() == 0) {
            return null;
        } else if (queryResults.size() > 1) {
            Log.w(TAG, "Multiple items found in a query for a single item");
        }

        return queryResults.get(0);
    }

    @NonNull
    abstract protected Uri getContentUri();

    @NonNull
    protected abstract ContentObserver getContentObserver();

    @NonNull
    protected abstract String[] getProjection();

    @NonNull
    protected abstract T read(Cursor cursor);

    @NonNull
    protected abstract ContentValues getContentValuesForItem(T item);
}
