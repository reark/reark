package com.tehmou.rxbookapp.data.provider;

import android.net.Uri;

/**
 * Created by ttuo on 11/01/15.
 */
public class GitHubRepositoryContract {
    private GitHubRepositoryContract() { }

    static final String SINGLE_REPOSITORY_MIME_TYPE =
            "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.repository";
    static final String MULTIPLE_REPOSITORY_MIME_TYPE =
            "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.repository";

    static final String TABLE_NAME = "repositories";
    public static final Uri CONTENT_URI = Uri.parse("content://" + MyContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);

    public static final String ID = "id";
    public static final String JSON = "json";

    public static final String[] PROJECTION = new String[] { ID, JSON };

    public static final String SORT_ORDER_DEFAULT = ID + " ASC";

    public static final String CREATE_DB_TABLE = " CREATE TABLE " + TABLE_NAME
            + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + JSON + " TEXT NOT NULL);";

    public static final String DROP_DB_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
