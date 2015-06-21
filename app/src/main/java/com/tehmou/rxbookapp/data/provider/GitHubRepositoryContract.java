package com.tehmou.rxbookapp.data.provider;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tehmou.rxbookapp.data.base.contract.SerializedJsonContract;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

import java.lang.reflect.Type;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 11/01/15.
 */
public class GitHubRepositoryContract extends SerializedJsonContract<GitHubRepository> {
    private static final String TABLE_NAME = "repositories";
    public static final Uri CONTENT_URI = Uri.parse("content://" + GithubContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);
    private static final Type TYPE = new TypeToken<GitHubRepository>() {}.getType();

    @NonNull
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @NonNull
    @Override
    protected String getCreateIdColumn() {
        return ID + " INTEGER PRIMARY KEY AUTOINCREMENT";
    }

    @NonNull
    @Override
    public ContentValues getContentValuesForItem(@NonNull GitHubRepository item) {
        Preconditions.checkNotNull(item, "Github Repository cannot be null.");

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(JSON, new Gson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected Type getType() {
        return TYPE;
    }
}
