package com.tehmou.rxbookapp.data.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tehmou.rxbookapp.data.base.contract.DatabaseContract;
import com.tehmou.rxbookapp.data.base.contract.SerializedJsonContract;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;
import com.tehmou.rxbookapp.pojo.NetworkRequestStatus;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by ttuo on 26/04/15.
 */
public class NetworkRequestStatusContract extends SerializedJsonContract<NetworkRequestStatus> {
    private static final String TABLE_NAME = "network_request_statuses";
    public static final Uri CONTENT_URI = Uri.parse("content://" + GithubContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);
    private static final Type TYPE = new TypeToken<GitHubRepository>() {}.getType();

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
