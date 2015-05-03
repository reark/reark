package com.tehmou.rxbookapp.data.provider;

import android.content.ContentValues;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tehmou.rxbookapp.data.base.contract.SerializedJsonContract;
import com.tehmou.rxbookapp.pojo.UserSettings;

import java.lang.reflect.Type;

/**
 * Created by ttuo on 11/01/15.
 */
public class UserSettingsContract extends SerializedJsonContract<UserSettings> {
    public static final int DEFAULT_USER_ID = 0;

    private static final String SINGLE_MIME_TYPE =
            "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.usersettings";
    private static final String MULTIPLE_MIME_TYPE =
            "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.usersettings";

    private static final String TABLE_NAME = "usersettings";
    public static final Uri CONTENT_URI = Uri.parse("content://" + GithubContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);
    public static final Type TYPE = new TypeToken<UserSettings>() {}.getType();

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
    public ContentValues getContentValuesForItem(UserSettings item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, DEFAULT_USER_ID);
        contentValues.put(JSON, new Gson().toJson(item));
        return contentValues;
    }

    public String getWhere(Uri uri) {
        return ID + " = " + uri.getLastPathSegment();
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}
