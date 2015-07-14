package com.tehmou.rxbookapp.data.provider;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tehmou.rxbookapp.data.base.contract.SerializedJsonContract;
import com.tehmou.rxbookapp.pojo.UserSettings;

import java.lang.reflect.Type;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 11/01/15.
 */
public class UserSettingsContract extends SerializedJsonContract<UserSettings> {
    public static final int DEFAULT_USER_ID = 0;

    private static final String TABLE_NAME = "userSettings";
    public static final Uri CONTENT_URI = Uri.parse("content://" + GithubContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);
    public static final Type TYPE = new TypeToken<UserSettings>() {}.getType();

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
    public ContentValues getContentValuesForItem(@NonNull UserSettings item) {
        Preconditions.checkNotNull(item, "User Settings cannot be null.");

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, DEFAULT_USER_ID);
        contentValues.put(JSON, new Gson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    public Type getType() {
        return TYPE;
    }
}
