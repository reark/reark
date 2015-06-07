package com.tehmou.rxbookapp.data.stores;

import com.tehmou.rxbookapp.data.base.store.ContentProviderStoreBase;
import com.tehmou.rxbookapp.data.provider.NetworkRequestStatusContract;
import com.tehmou.rxbookapp.pojo.NetworkRequestStatus;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

/**
 * Created by ttuo on 26/04/15.
 */
public class NetworkRequestStatusStore extends ContentProviderStoreBase<NetworkRequestStatus, Integer> {
    private static final String TAG = NetworkRequestStatusStore.class.getSimpleName();

    public NetworkRequestStatusStore(ContentResolver contentResolver) {
        super(contentResolver, new NetworkRequestStatusContract());
    }

    @Override
    protected Integer getIdFor(NetworkRequestStatus item) {
        return item.getUri().hashCode();
    }

    @Override
    public Uri getContentUri() {
        return NetworkRequestStatusContract.CONTENT_URI;
    }

    @Override
    protected NetworkRequestStatus query(Uri uri) {
        return super.query(uri);
    }

    @Override
    public void insertOrUpdate(NetworkRequestStatus item) {
        Log.v(TAG, "insertOrUpdate(" + item.getStatus() + ", " + item.getUri() + ")");
        super.insertOrUpdate(item);
    }
}
