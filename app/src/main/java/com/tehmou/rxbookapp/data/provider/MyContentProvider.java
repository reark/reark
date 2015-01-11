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
    static final int REPOSITORIES_SEARCH = 3;
    static final int REPOSITORIES_SEARCH_ID = 4;
    static final int REPOSITORIES_SEARCH_MAPPING = 5;
    static final int REPOSITORIES_SEARCH_MAPPING_ID = 6;

    static final UriMatcher URI_MATCHER;
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(PROVIDER_NAME, "repositories", REPOSITORIES);
        URI_MATCHER.addURI(PROVIDER_NAME, "repositories/#", REPOSITORIES_ID);
        URI_MATCHER.addURI(PROVIDER_NAME, "repositories_search", REPOSITORIES);
        URI_MATCHER.addURI(PROVIDER_NAME, "repositories_search/#", REPOSITORIES_ID);
        URI_MATCHER.addURI(PROVIDER_NAME, "repositories_search_mapping", REPOSITORIES);
        URI_MATCHER.addURI(PROVIDER_NAME, "repositories_search_mapping/#", REPOSITORIES_ID);
    }

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;

    static final String DATABASE_NAME = "database";
    static final int DATABASE_VERSION = 1;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(GitHubRepositoryContract.CREATE_DB_TABLE);
            db.execSQL(GitHubRepositorySearchContract.CREATE_DB_TABLE);
            db.execSQL(GitHubRepositorySearchMappingContract.CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(GitHubRepositoryContract.DROP_DB_TABLE);
            db.execSQL(GitHubRepositorySearchContract.DROP_DB_TABLE);
            db.execSQL(GitHubRepositorySearchMappingContract.DROP_DB_TABLE);
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
        String tableName;
        String idColumn = null;
        String idStr = null;
        switch (URI_MATCHER.match(uri)) {
            case REPOSITORIES:
                tableName = GitHubRepositoryContract.TABLE_NAME;
                break;
            case REPOSITORIES_ID:
                tableName = GitHubRepositoryContract.TABLE_NAME;
                idColumn = GitHubRepositoryContract.ID;
                idStr = uri.getLastPathSegment();
                break;
            case REPOSITORIES_SEARCH:
                tableName = GitHubRepositorySearchContract.TABLE_NAME;
                break;
            case REPOSITORIES_SEARCH_ID:
                tableName = GitHubRepositorySearchContract.TABLE_NAME;
                idColumn = GitHubRepositorySearchContract.ID;
                idStr = uri.getLastPathSegment();
                break;
            case REPOSITORIES_SEARCH_MAPPING:
                tableName = GitHubRepositorySearchMappingContract.TABLE_NAME;
                break;
            case REPOSITORIES_SEARCH_MAPPING_ID:
                tableName = GitHubRepositorySearchMappingContract.TABLE_NAME;
                idColumn = GitHubRepositorySearchMappingContract.ID;
                idStr = uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        String where = "";
        if (idStr != null) {
            where = idColumn + " = " + idStr;
            if (TextUtils.isEmpty(selection)) {
                where += " AND " + selection;
            }
        } else if (!TextUtils.isEmpty(selection)) {
            where = selection;
        }
        int count = db.delete(tableName, where, selectionArgs);
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
            case REPOSITORIES_SEARCH:
                return GitHubRepositorySearchContract.MULTIPLE_REPOSITORY_SEARCH_MIME_TYPE;
            case REPOSITORIES_SEARCH_ID:
                return GitHubRepositorySearchContract.SINGLE_REPOSITORY_SEARCH_MIME_TYPE;
            case REPOSITORIES_SEARCH_MAPPING:
                return GitHubRepositorySearchMappingContract.MULTIPLE_REPOSITORY_SEARCH_MAPPING_MIME_TYPE;
            case REPOSITORIES_SEARCH_MAPPING_ID:
                return GitHubRepositorySearchMappingContract.SINGLE_REPOSITORY_SEARCH_MAPPING_MIME_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String tableName;
        boolean insertWithId = false;
        switch (URI_MATCHER.match(uri)) {
            case REPOSITORIES:
                tableName = GitHubRepositoryContract.TABLE_NAME;
                break;
            case REPOSITORIES_ID:
                tableName = GitHubRepositoryContract.TABLE_NAME;
                insertWithId = true;
                break;
            case REPOSITORIES_SEARCH:
                tableName = GitHubRepositorySearchContract.TABLE_NAME;
                break;
            case REPOSITORIES_SEARCH_ID:
                tableName = GitHubRepositorySearchContract.TABLE_NAME;
                insertWithId = true;
                break;
            case REPOSITORIES_SEARCH_MAPPING:
                tableName = GitHubRepositorySearchMappingContract.TABLE_NAME;
                break;
            case REPOSITORIES_SEARCH_MAPPING_ID:
                tableName = GitHubRepositorySearchMappingContract.TABLE_NAME;
                insertWithId = true;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }
        long id;
        if (insertWithId) {
            id = db.insertWithOnConflict(tableName,
                    null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } else {
            id = db.insert(tableName, null, values);
        }
        return getUriForId(id, uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        String tableName;
        String where = "";
        switch (URI_MATCHER.match(uri)) {
            case REPOSITORIES:
                tableName = GitHubRepositoryContract.TABLE_NAME;
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = GitHubRepositoryContract.SORT_ORDER_DEFAULT;
                }
                break;
            case REPOSITORIES_ID:
                tableName = GitHubRepositoryContract.TABLE_NAME;
                where = GitHubRepositoryContract.ID + " = " + uri.getLastPathSegment();
                break;
            case REPOSITORIES_SEARCH:
                tableName = GitHubRepositorySearchContract.TABLE_NAME;
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = GitHubRepositorySearchContract.SORT_ORDER_DEFAULT;
                }
                break;
            case REPOSITORIES_SEARCH_ID:
                tableName = GitHubRepositorySearchContract.TABLE_NAME;
                where = GitHubRepositorySearchContract.ID + " = " + uri.getLastPathSegment();
                break;
            case REPOSITORIES_SEARCH_MAPPING:
                tableName = GitHubRepositorySearchMappingContract.TABLE_NAME;
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = GitHubRepositorySearchMappingContract.SORT_ORDER_DEFAULT;
                }
                break;
            case REPOSITORIES_SEARCH_MAPPING_ID:
                tableName = GitHubRepositorySearchMappingContract.TABLE_NAME;
                where = GitHubRepositorySearchMappingContract.ID + " = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        builder.setTables(tableName);
        builder.appendWhere(where);
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
        String idColumn = null;
        String idStr = null;
        String tableName;
        switch (URI_MATCHER.match(uri)) {
            case REPOSITORIES:
                tableName = GitHubRepositoryContract.TABLE_NAME;
                break;
            case REPOSITORIES_ID:
                tableName = GitHubRepositoryContract.TABLE_NAME;
                idStr = uri.getLastPathSegment();
                idColumn = GitHubRepositoryContract.ID;
                break;
            case REPOSITORIES_SEARCH:
                tableName = GitHubRepositorySearchContract.TABLE_NAME;
                break;
            case REPOSITORIES_SEARCH_ID:
                tableName = GitHubRepositorySearchContract.TABLE_NAME;
                idStr = uri.getLastPathSegment();
                idColumn = GitHubRepositorySearchContract.ID;
                break;
            case REPOSITORIES_SEARCH_MAPPING:
                tableName = GitHubRepositorySearchMappingContract.TABLE_NAME;
                break;
            case REPOSITORIES_SEARCH_MAPPING_ID:
                tableName = GitHubRepositorySearchMappingContract.TABLE_NAME;
                idStr = uri.getLastPathSegment();
                idColumn = GitHubRepositorySearchMappingContract.ID;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        String where = "";
        if (idStr != null) {
            where = idColumn + " = " + idStr;
            if (TextUtils.isEmpty(selection)) {
                where += " AND " + selection;
            }
        } else if (!TextUtils.isEmpty(selection)) {
            where = selection;
        }
        int count = db.update(tableName, values, where, selectionArgs);
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
