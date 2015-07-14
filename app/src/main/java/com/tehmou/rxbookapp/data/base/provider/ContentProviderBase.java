package com.tehmou.rxbookapp.data.base.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 11/01/15.
 */
abstract public class ContentProviderBase extends ContentProvider {
    private static final String TAG = ContentProviderBase.class.getSimpleName();
    protected SQLiteDatabase db;
    protected SQLiteOpenHelper databaseHelper;
    protected UriMatcher URI_MATCHER;

    @Override
    public boolean onCreate() {
        createUriMatcher();
        Context context = getContext();
        databaseHelper = createDatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @NonNull String[] selectionArgs) {
        Preconditions.checkNotNull(uri, "URI cannot be null.");
        Preconditions.checkNotNull(selectionArgs, "Selection arguments cannot be null.");

        final int match = URI_MATCHER.match(uri);
        String tableName = getTableName(match);
        String where = getWhere(match, uri);
        int count = db.delete(tableName, where, selectionArgs);
        if (count > 0) {
            notifyChange(match, uri);
        }
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        String tableName = getTableName(match);
        db.insertWithOnConflict(tableName,
                null, values, SQLiteDatabase.CONFLICT_REPLACE);
        notifyChange(match, uri);
        return uri;
    }

    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        Preconditions.checkNotNull(uri, "URI cannot be null.");

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        final int match = URI_MATCHER.match(uri);
        String tableName = getTableName(match);
        String where = getWhere(match, uri);

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = getDefaultSortOrder(match);
        }

        builder.setTables(tableName);
        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        where,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
        cursor.setNotificationUri(
                getContext().getContentResolver(),
                uri);
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @NonNull ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        Preconditions.checkNotNull(uri, "URI cannot be null.");
        Preconditions.checkNotNull(values, "Content Values cannot be null.");

        if (selection != null) {
            Log.e(TAG, "selection not supported");
        }
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        final int match = URI_MATCHER.match(uri);
        String tableName = getTableName(match);
        String where = getWhere(match, uri);

        int count = db.update(tableName, values, where, selectionArgs);
        if (count > 0) {
            notifyChange(match, uri);
        }
        return count;
    }

    @Nullable
    abstract protected String getWhere(final int match, Uri uri);

    @NonNull
    abstract protected String getTableName(final int match);

    @NonNull
    abstract protected String getDefaultSortOrder(final int match);

    @NonNull
    abstract protected SQLiteOpenHelper createDatabaseHelper(@NonNull final Context context);

    abstract protected void createUriMatcher();

    abstract protected void notifyChange(final int match, @NonNull Uri uri);
}
