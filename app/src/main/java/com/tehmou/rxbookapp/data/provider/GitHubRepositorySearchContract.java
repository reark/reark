package com.tehmou.rxbookapp.data.provider;

import android.net.Uri;

/**
 * Created by ttuo on 11/01/15.
 */
public class GitHubRepositorySearchContract extends SerializedJsonContract {
    private static final String SINGLE_MIME_TYPE =
            "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.repositorysearch";
    private static final String MULTIPLE_MIME_TYPE =
            "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.repositorysearch";

    static final String TABLE_NAME = "repository_searches";
    public static final Uri CONTENT_URI = Uri.parse("content://" + MyContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getSingleMimeType() {
        return SINGLE_MIME_TYPE;
    }

    @Override
    public String getMultipleMimeType() {
        return MULTIPLE_MIME_TYPE;
    }

    @Override
    protected String getCreateIdColumn() {
        return ID + " STRING PRIMARY KEY";
    }

    public String getIdSqlString(String id) {
        return "'" + id + "'";
    }
}
