package com.tehmou.rxbookapp.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by ttuo on 11/01/15.
 */
abstract public class ContentProviderBase extends ContentProvider {
    protected SQLiteDatabase db;
    protected SQLiteOpenHelper databaseHelper;
    protected UriMatcher URI_MATCHER;

    @Override
    public boolean onCreate() {
        createUriMatcher();
        Context context = getContext();
        databaseHelper = createDatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();
        if (db != null) {
            return true;
        }
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = URI_MATCHER.match(uri);
        String tableName = getTableName(match);
        String idColumn = getIdColumnName(match);
        String idStr = uri.getLastPathSegment();
        String where = getWhere(selection, idColumn, idStr,
                getDatabaseContractForMatch(match));
        int count = db.delete(tableName, where, selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        String tableName = getTableName(match);
        String idColumn = getIdColumnName(match);
        long id;
        if (idColumn != null) {
            db.insertWithOnConflict(tableName,
                    null, values, SQLiteDatabase.CONFLICT_REPLACE);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        } else {
            id = db.insert(tableName, null, values);
            getContext().getContentResolver().notifyChange(uri, null);
            return getUriForId(id, uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        final int match = URI_MATCHER.match(uri);
        String tableName = getTableName(match);
        String idColumn = getIdColumnName(match);
        String idStr = uri.getLastPathSegment();

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = getDefaultSortOrder(match);
        }

        String where = getWhere(selection, idColumn, idStr,
                getDatabaseContractForMatch(match));
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
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        final int match = URI_MATCHER.match(uri);
        String idStr = uri.getLastPathSegment();
        String idColumn = getIdColumnName(match);
        String tableName = getTableName(match);

        String where = getWhere(selection, idColumn, idStr,
                getDatabaseContractForMatch(match));
        int count = db.update(tableName, values, where, selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    protected String getWhere(final String selection,
                              final String idColumn,
                              final String idStr,
                              DatabaseContract databaseContract) {
        String where = null;
        if (idColumn != null) {
            where = idColumn + " = " + databaseContract.getIdSqlString(idStr);
            if (!TextUtils.isEmpty(selection)) {
                where += " AND " + selection;
            }
        } else if (!TextUtils.isEmpty(selection)) {
            where = selection;
        }
        return where;
    }

    protected Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
        throw new SQLException("Problem while inserting into uri: " + uri);
    }

    abstract protected DatabaseContract getDatabaseContractForMatch(final int match);
    abstract protected String getTableName(final int match);
    abstract protected String getIdColumnName(final int match);
    abstract protected String getDefaultSortOrder(final int match);
    abstract protected SQLiteOpenHelper createDatabaseHelper(final Context context);
    abstract protected void createUriMatcher();
}
