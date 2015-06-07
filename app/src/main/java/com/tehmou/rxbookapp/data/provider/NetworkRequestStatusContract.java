package com.tehmou.rxbookapp.data.provider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.tehmou.rxbookapp.data.base.contract.SerializedJsonContract;
import com.tehmou.rxbookapp.pojo.NetworkRequestStatus;

import android.content.ContentValues;
import android.net.Uri;

import java.lang.reflect.Type;

/**
 * Created by ttuo on 26/04/15.
 */
public class NetworkRequestStatusContract extends SerializedJsonContract<NetworkRequestStatus> {
    private static final String TABLE_NAME = "network_request_statuses";
    public static final Uri CONTENT_URI = Uri.parse("content://" + GithubContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);
    private static final Type TYPE = new TypeToken<NetworkRequestStatus>() {}.getType();

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getCreateIdColumn() {
        return ID + " INTEGER PRIMARY KEY";
    }

    @Override
    public ContentValues getContentValuesForItem(NetworkRequestStatus item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getUri().hashCode());
        contentValues.put(JSON, new Gson().toJson(item));
        return contentValues;
    }

    @Override
    protected Type getType() {
        return TYPE;
    }
}
