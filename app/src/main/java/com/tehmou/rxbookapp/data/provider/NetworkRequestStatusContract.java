package com.tehmou.rxbookapp.data.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tehmou.rxbookapp.data.base.contract.DatabaseContract;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;
import com.tehmou.rxbookapp.pojo.NetworkRequestStatus;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by ttuo on 26/04/15.
 */
public class NetworkRequestStatusContract implements DatabaseContract<NetworkRequestStatus> {
    private static final Type TYPE = new TypeToken<NetworkRequestStatus>() {}.getType();

    public static final String OWNER = "owner";
    public static final String URI_HASH = "uriHash";
    public static final String JSON = "json";

    static final String TABLE_NAME = "network_request_statuses";
    public static final Uri CONTENT_URI = Uri.parse("content://" + GithubContentProvider.PROVIDER_NAME + "/" + TABLE_NAME);

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public String getCreateTable() {
        return " CREATE TABLE " + TABLE_NAME
                + " ( " + OWNER + " STRING NOT NULL, "
                + URI_HASH + " INTEGER, "
                + JSON + " TEXT NOT NULL);";
    }

    @Override
    public String getDropTable() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    @Override
    public ContentValues getContentValuesForItem(NetworkRequestStatus item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(OWNER, item.getOwner());
        contentValues.put(URI_HASH, item.getUri().hashCode());
        contentValues.put(JSON, new Gson().toJson(item));
        return contentValues;
    }

    @Override
    public NetworkRequestStatus read(Cursor cursor) {
        if (cursor.moveToFirst()) {
            final String json = cursor.getString(cursor.getColumnIndex(JSON));
            return new Gson().fromJson(json, TYPE);
        }
        return null;
    }

    @Override
    public String[] getProjection() {
        return new String[]{OWNER, URI_HASH, JSON};
    }
}

