package com.tehmou.rxbookapp.data.stores;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.tehmou.rxandroidarchitecture.data.base.store.SingleItemContentProviderStoreBase;
import com.tehmou.rxandroidarchitecture.pojo.NetworkRequestStatus;
import com.tehmou.rxandroidarchitecture.utils.Preconditions;
import com.tehmou.rxbookapp.data.schematicProvider.GitHubProvider;
import com.tehmou.rxbookapp.data.schematicProvider.JsonIdColumns;
import com.tehmou.rxbookapp.data.schematicProvider.UserSettingsColumns;

/**
 * Created by ttuo on 26/04/15.
 */
public class NetworkRequestStatusStore extends SingleItemContentProviderStoreBase<NetworkRequestStatus, Integer> {
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

    @Override
    public void put(@NonNull NetworkRequestStatus item) {
        Preconditions.checkNotNull(item, "Network Request Status cannot be null.");

        Log.v(TAG, "insertOrUpdate(" + item.getStatus() + ", " + item.getUri() + ")");
        super.put(item);
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

    @NonNull
    @Override
    public Uri getUriForKey(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return GitHubProvider.NetworkRequestStatuses.withId(id);
    }
}
