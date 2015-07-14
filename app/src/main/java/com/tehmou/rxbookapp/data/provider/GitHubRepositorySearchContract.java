package com.tehmou.rxbookapp.data.provider;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tehmou.rxbookapp.data.base.contract.SerializedJsonContract;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;

import java.lang.reflect.Type;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 11/01/15.
 */
public class GitHubRepositorySearchContract extends SerializedJsonContract<GitHubRepositorySearch> {
    static final String TABLE_NAME = "repositorySearches";
    public static final Uri CONTENT_URI = Uri.parse("content://" + GithubContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);
    private static final Type TYPE = new TypeToken<GitHubRepositorySearch>() {}.getType();

    @NonNull
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @NonNull
    @Override
    protected String getCreateIdColumn() {
        return ID + " STRING PRIMARY KEY";
    }

    @NonNull
    @Override
    public ContentValues getContentValuesForItem(@NonNull GitHubRepositorySearch item) {
        Preconditions.checkNotNull(item, "Git Repository Search cannot be null.");

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getSearch());
        contentValues.put(JSON, new Gson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    public Type getType() {
        return TYPE;
    }
}
