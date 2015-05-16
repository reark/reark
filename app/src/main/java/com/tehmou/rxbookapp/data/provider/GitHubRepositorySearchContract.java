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
    static final String TABLE_NAME = "repository_searches";
    public static final Uri CONTENT_URI = Uri.parse("content://" + GithubContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);
    private static final Type TYPE = new TypeToken<GitHubRepositorySearch>() {}.getType();

    @Override
    public String getTableName() {
        return TABLE_NAME;
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

    @Override
    public Type getType() {
        return TYPE;
    }
}
