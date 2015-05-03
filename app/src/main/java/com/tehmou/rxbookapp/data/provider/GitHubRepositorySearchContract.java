package com.tehmou.rxbookapp.data.provider;

import android.content.ContentValues;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tehmou.rxbookapp.data.base.contract.SerializedJsonContract;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;

import java.lang.reflect.Type;

/**
 * Created by ttuo on 11/01/15.
 */
public class GitHubRepositorySearchContract extends SerializedJsonContract<GitHubRepositorySearch> {
    private static final String SINGLE_MIME_TYPE =
            "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.repositorysearch";
    private static final String MULTIPLE_MIME_TYPE =
            "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.repositorysearch";

    static final String TABLE_NAME = "repository_searches";
    public static final Uri CONTENT_URI = Uri.parse("content://" + GithubContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);
    private static final Type TYPE = new TypeToken<GitHubRepositorySearch>() {}.getType();

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

    @Override
    public ContentValues getContentValuesForItem(GitHubRepositorySearch item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getSearch());
        contentValues.put(JSON, new Gson().toJson(item));
        return contentValues;
    }

    public String getWhere(Uri uri) {
        return ID + " = '" + uri.getLastPathSegment() + "'";
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}
