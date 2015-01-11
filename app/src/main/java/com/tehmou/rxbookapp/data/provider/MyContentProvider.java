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

import java.util.HashMap;

/**
 * Created by ttuo on 10/01/15.
 */
public class MyContentProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.tehmou.rxbookapp.data.provider.MyContentProvider";

    static final int REPOSITORIES = 1;
    static final int REPOSITORIES_ID = 2;

    static final UriMatcher URI_MATCHER;
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(PROVIDER_NAME, "repositories", REPOSITORIES);
        URI_MATCHER.addURI(PROVIDER_NAME, "repositories/#", REPOSITORIES_ID);
    }

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;

    static final String DATABASE_NAME = "database";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE = GitHubRepositoryContract.CREATE_DB_TABLE;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(GitHubRepositoryContract.DROP_DB_TABLE);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        databaseHelper = new DatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();
        if (db != null) {
            return true;
        }
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        switch (URI_MATCHER.match(uri)) {
            case REPOSITORIES:
                count = db.delete(GitHubRepositoryContract.TABLE_NAME, selection, selectionArgs);
                break;
            case REPOSITORIES_ID:
                String idStr = uri.getLastPathSegment();
                String where = GitHubRepositoryContract.ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                count = db.delete(GitHubRepositoryContract.TABLE_NAME, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case REPOSITORIES:
                return GitHubRepositoryContract.MULTIPLE_REPOSITORY_MIME_TYPE;
            case REPOSITORIES_ID:
                return GitHubRepositoryContract.SINGLE_REPOSITORY_MIME_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != REPOSITORIES) {
            throw new IllegalArgumentException(
                    "Unsupported URI for insertion. " + uri);
        }
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if (URI_MATCHER.match(uri) == REPOSITORIES) {
            long id = db.insert(GitHubRepositoryContract.TABLE_NAME, null, values);
            return getUriForId(id, uri);
        } else {
            long id = db.insertWithOnConflict(GitHubRepositoryContract.TABLE_NAME,
                    null, values, SQLiteDatabase.CONFLICT_REPLACE);
            return getUriForId(id, uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (URI_MATCHER.match(uri)) {
            case REPOSITORIES:
                builder.setTables(GitHubRepositoryContract.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = GitHubRepositoryContract.SORT_ORDER_DEFAULT;
                }
                break;
            case REPOSITORIES_ID:
                builder.setTables(GitHubRepositoryContract.TABLE_NAME);
                builder.appendWhere(GitHubRepositoryContract.ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        selection,
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
        int count = 0;
        switch (URI_MATCHER.match(uri)) {
            case REPOSITORIES:
                count = db.update(GitHubRepositoryContract.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case REPOSITORIES_ID:
                String idStr = uri.getLastPathSegment();
                String where = GitHubRepositoryContract.ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                count = db.update(GitHubRepositoryContract.TABLE_NAME,
                        values, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
        throw new SQLException("Problem while inserting into uri: " + uri);
    }

}
