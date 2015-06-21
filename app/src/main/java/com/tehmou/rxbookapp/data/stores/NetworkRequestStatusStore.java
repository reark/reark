package com.tehmou.rxbookapp.data.stores;

import com.tehmou.rxbookapp.data.base.store.ContentProviderStoreBase;
import com.tehmou.rxbookapp.data.provider.NetworkRequestStatusContract;
import com.tehmou.rxbookapp.pojo.NetworkRequestStatus;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 26/04/15.
 */
public class NetworkRequestStatusStore extends ContentProviderStoreBase<NetworkRequestStatus, Integer> {
    private static final String TAG = NetworkRequestStatusStore.class.getSimpleName();

    public NetworkRequestStatusStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver, new NetworkRequestStatusContract());
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
        return NetworkRequestStatusContract.CONTENT_URI;
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
}
