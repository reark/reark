package com.tehmou.rxbookapp.data.provider;

import android.net.Uri;

/**
 * Created by ttuo on 11/01/15.
 */
public class GitHubRepositorySearchMappingContract {
    private GitHubRepositorySearchMappingContract() { }

    public static final String SINGLE_REPOSITORY_SEARCH_MAPPING_MIME_TYPE =
            "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.repositorysearchmapping";
    public static final String MULTIPLE_REPOSITORY_SEARCH_MAPPING_MIME_TYPE =
            "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.repositorysearchmapping";

    static final String TABLE_NAME = "repository_search_mappings";
    public static final Uri CONTENT_URI = Uri.parse("content://" + MyContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);

    public static final String ID = "id";
    public static final String SEARCH_ID = "search_id";
    public static final String REPOSITORY_ID = "repository_id";

    public static final String SORT_ORDER_DEFAULT = ID + " ASC";

    public static final String CREATE_DB_TABLE = " CREATE TABLE " + TABLE_NAME
            + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SEARCH_ID + " INTEGER, "
            + REPOSITORY_ID + "INTEGER);";

    public static final String DROP_DB_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
