package com.tehmou.rxbookapp.data.provider;

import android.content.ContentUris;
import android.content.Context;
import android.content.UriMatcher;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by ttuo on 10/01/15.
 */
public class MyContentProvider extends ContentProviderBase {
    public static final String PROVIDER_NAME = "com.tehmou.rxbookapp.data.provider.MyContentProvider";

    static final int REPOSITORIES = 1;
    static final int REPOSITORIES_ID = 2;
    static final int REPOSITORIES_SEARCH = 3;
    static final int REPOSITORIES_SEARCH_ID = 4;

    static final String DATABASE_NAME = "database";
    static final int DATABASE_VERSION = 2;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(GitHubRepositoryContract.CREATE_DB_TABLE);
            db.execSQL(GitHubRepositorySearchContract.CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(GitHubRepositoryContract.DROP_DB_TABLE);
            db.execSQL(GitHubRepositorySearchContract.DROP_DB_TABLE);
            onCreate(db);
        }
    }

    @Override
    protected String getWhere(final String selection,
                            final String idColumn,
                            final String idStr) {
        String where = null;
        if (idColumn != null) {
            where = idColumn + " = " + idStr;
            if (!TextUtils.isEmpty(selection)) {
                where += " AND " + selection;
            }
        } else if (!TextUtils.isEmpty(selection)) {
            where = selection;
        }
        return where;
    }

    @Override
    protected Uri getUriForId(long id, Uri uri) {
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
            default:
                return null;
        }
    }

    @Override
    protected String getTableName(final int match) {
        switch (match) {
            case REPOSITORIES:
            case REPOSITORIES_ID:
                return GitHubRepositoryContract.TABLE_NAME;
            case REPOSITORIES_SEARCH:
            case REPOSITORIES_SEARCH_ID:
                return GitHubRepositorySearchContract.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + match);
        }
    }

    @Override
    protected String getIdColumnName(final int match) {
        switch (match) {
            case REPOSITORIES:
            case REPOSITORIES_SEARCH:
                return null;
            case REPOSITORIES_ID:
                return GitHubRepositoryContract.ID;
            case REPOSITORIES_SEARCH_ID:
                return GitHubRepositorySearchContract.ID;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + match);
        }
    }

    @Override
    protected String getDefaultSortOrder(final int match) {
        switch (match) {
            case REPOSITORIES:
                return GitHubRepositoryContract.SORT_ORDER_DEFAULT;
            case REPOSITORIES_SEARCH:
                return GitHubRepositorySearchContract.SORT_ORDER_DEFAULT;
            case REPOSITORIES_ID:
            case REPOSITORIES_SEARCH_ID:
                return null;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + match);
        }
    }

    @Override
    protected SQLiteOpenHelper createDatabaseHelper(Context context) {
        return new DatabaseHelper(context);
    }

    @Override
    protected void createUriMatcher() {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(PROVIDER_NAME, "repositories", REPOSITORIES);
        URI_MATCHER.addURI(PROVIDER_NAME, "repositories/#", REPOSITORIES_ID);
        URI_MATCHER.addURI(PROVIDER_NAME, "repositories_search", REPOSITORIES_SEARCH);
        URI_MATCHER.addURI(PROVIDER_NAME, "repositories_search/#", REPOSITORIES_SEARCH_ID);
    }
}
