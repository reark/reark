package com.tehmou.rxbookapp.data.stores;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.tehmou.rxbookapp.data.base.store.ContentProviderStoreBase;
import com.tehmou.rxbookapp.data.schematicProvider.GitHubProvider;
import com.tehmou.rxbookapp.data.schematicProvider.JsonIdColumns;
import com.tehmou.rxbookapp.data.schematicProvider.UserSettingsColumns;
import com.tehmou.rxbookapp.pojo.NetworkRequestStatus;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 26/04/15.
 */
public class NetworkRequestStatusStore extends ContentProviderStoreBase<NetworkRequestStatus, Integer> {
    private static final String TAG = NetworkRequestStatusStore.class.getSimpleName();

    public NetworkRequestStatusStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
    }

    @NonNull
    @Override
    protected Integer getIdFor(@NonNull NetworkRequestStatus item) {
        Preconditions.checkNotNull(item, "Network Request Status cannot be null.");
        return item.getUri().hashCode();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return GitHubProvider.NetworkRequestStatuses.NETWORK_REQUEST_STATUSES;
    }

    @Nullable
    @Override
    protected NetworkRequestStatus query(@NonNull Uri uri) {
        return super.query(uri);
    }

    @Override
    public void insertOrUpdate(@NonNull NetworkRequestStatus item) {
        Preconditions.checkNotNull(item, "Network Request Status cannot be null.");

        Log.v(TAG, "insertOrUpdate(" + item.getStatus() + ", " + item.getUri() + ")");
        super.insertOrUpdate(item);
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { UserSettingsColumns.ID, UserSettingsColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(NetworkRequestStatus item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(JsonIdColumns.ID, item.getUri().hashCode());
        contentValues.put(JsonIdColumns.JSON, new Gson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected NetworkRequestStatus read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON));
        final NetworkRequestStatus value = new Gson().fromJson(json, NetworkRequestStatus.class);
        return value;
    }
}
