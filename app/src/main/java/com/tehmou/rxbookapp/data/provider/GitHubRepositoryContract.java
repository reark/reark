package com.tehmou.rxbookapp.data.provider;

import android.content.ContentValues;
import android.net.Uri;

import com.google.gson.Gson;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

/**
 * Created by ttuo on 11/01/15.
 */
public class GitHubRepositoryContract extends SerializedJsonContract<GitHubRepository> {
    private static final String SINGLE_MIME_TYPE =
            "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.repository";
    private static final String MULTIPLE_MIME_TYPE =
            "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.repository";

    private static final String TABLE_NAME = "repositories";
    public static final Uri CONTENT_URI = Uri.parse("content://" + GithubContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);

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
        return ID + " INTEGER PRIMARY KEY AUTOINCREMENT";
    }

    @Override
    public ContentValues getContentValuesForItem(GitHubRepository item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(JSON, new Gson().toJson(item));
        return contentValues;
    }

    public String getWhere(Uri uri) {
        return ID + " = " + uri.getLastPathSegment();
    }
}
