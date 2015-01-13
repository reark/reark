package com.tehmou.rxbookapp.data.provider;

import android.net.Uri;

/**
 * Created by ttuo on 11/01/15.
 */
public class GitHubRepositoryContract extends SerializedJsonContract {
    private static final String SINGLE_REPOSITORY_MIME_TYPE =
            "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.repository";
    private static final String MULTIPLE_REPOSITORY_MIME_TYPE =
            "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.repository";
    private static final String TABLE_NAME = "repositories";
    public static final Uri CONTENT_URI = Uri.parse("content://" + MyContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getSingleMimeType() {
        return SINGLE_REPOSITORY_MIME_TYPE;
    }

    @Override
    public String getMultipleMimeType() {
        return MULTIPLE_REPOSITORY_MIME_TYPE;
    }
}
