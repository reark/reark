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

import com.tehmou.rxbookapp.pojo.GitHubRepository;

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
        final int match = URI_MATCHER.match(uri);
        String tableName = getTableName(match);
        String idColumn = getIdColumnName(match);
        String idStr = uri.getLastPathSegment();
        String where = getWhere(selection, idColumn, idStr);
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
        final int match = URI_MATCHER.match(uri);
        String tableName = getTableName(match);
        String idColumn = getIdColumnName(match);
        String idStr = uri.getLastPathSegment();

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = getDefaultSortOrder(match);
        }

        String where = getWhere(selection, idColumn, idStr);
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

        final int match = URI_MATCHER.match(uri);
        String idStr = uri.getLastPathSegment();
        String idColumn = getIdColumnName(match);
        String tableName = getTableName(match);

        String where = getWhere(selection, idColumn, idStr);
        int count = db.update(tableName, values, where, selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private String getWhere(final String selection,
                            final String idColumn,
                            final String idStr) {
        String where = null;
        if (idColumn != null) {
            where = idColumn + " = " + idStr;
            if (TextUtils.isEmpty(selection)) {
                where += " AND " + selection;
            }
        } else if (!TextUtils.isEmpty(selection)) {
            where = selection;
        }
        return where;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
        throw new SQLException("Problem while inserting into uri: " + uri);
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

    private String getTableName(final int match) {
        switch (match) {
            case REPOSITORIES:
            case REPOSITORIES_ID:
                return GitHubRepositorySearchContract.TABLE_NAME;
            case REPOSITORIES_SEARCH:
            case REPOSITORIES_SEARCH_ID:
                return GitHubRepositorySearchContract.TABLE_NAME;
            case REPOSITORIES_SEARCH_MAPPING:
            case REPOSITORIES_SEARCH_MAPPING_ID:
                return GitHubRepositorySearchMappingContract.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + match);
        }
    }

    private String getIdColumnName(final int match) {
        switch (match) {
            case REPOSITORIES:
            case REPOSITORIES_SEARCH:
            case REPOSITORIES_SEARCH_MAPPING:
                return null;
            case REPOSITORIES_ID:
                return GitHubRepositoryContract.ID;
            case REPOSITORIES_SEARCH_ID:
                return GitHubRepositorySearchContract.ID;
            case REPOSITORIES_SEARCH_MAPPING_ID:
                return GitHubRepositorySearchMappingContract.ID;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + match);
        }
    }

    private String getDefaultSortOrder(final int match) {
        switch (match) {
            case REPOSITORIES:
                return GitHubRepositoryContract.SORT_ORDER_DEFAULT;
            case REPOSITORIES_SEARCH:
                return GitHubRepositorySearchContract.SORT_ORDER_DEFAULT;
            case REPOSITORIES_SEARCH_MAPPING:
                return GitHubRepositorySearchMappingContract.SORT_ORDER_DEFAULT;
            case REPOSITORIES_ID:
            case REPOSITORIES_SEARCH_ID:
            case REPOSITORIES_SEARCH_MAPPING_ID:
                return null;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + match);
        }
    }
}
