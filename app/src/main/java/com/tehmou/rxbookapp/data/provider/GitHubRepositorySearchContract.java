package com.tehmou.rxbookapp.data.provider;

import android.net.Uri;

/**
 * Created by ttuo on 11/01/15.
 */
public class GitHubRepositorySearchContract implements DatabaseContract {
    public static final String SINGLE_REPOSITORY_SEARCH_MIME_TYPE =
            "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.repositorysearch";
    public static final String MULTIPLE_REPOSITORY_SEARCH_MIME_TYPE =
            "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.repositorysearch";

    static final String TABLE_NAME = "repository_searches";
    public static final Uri CONTENT_URI = Uri.parse("content://" + MyContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);

    public static final String ID = "id";
    public static final String SEARCH = "search";
    public static final String ITEMS = "list";

    private static final String SORT_ORDER_DEFAULT = SEARCH + " ASC";

    private static final String CREATE_DB_TABLE = " CREATE TABLE " + TABLE_NAME
            + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SEARCH + " TEXT NOT NULL, "
            + ITEMS + " TEXT NOT NULL);";

    private static final String DROP_DB_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    @Override
    public String getDefaultSortOrder() {
        return SORT_ORDER_DEFAULT;
    }

    @Override
    public String getCreateTable() {
        return CREATE_DB_TABLE;
    }

    @Override
    public String getDropTable() {
        return DROP_DB_TABLE;
    }

    @Override
    public String getName() {
        return TABLE_NAME;
    }

    @Override
    public String getIdColumnName() {
        return ID;
    }

    @Override
    public String getSingleMimeType() {
        return SINGLE_REPOSITORY_SEARCH_MIME_TYPE;
    }

    @Override
    public String getMultipleMimeType() {
        return MULTIPLE_REPOSITORY_SEARCH_MIME_TYPE;
    }
}
