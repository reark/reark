package com.tehmou.rxbookapp.data.stores;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tehmou.rxbookapp.data.base.store.ContentProviderStoreBase;
import com.tehmou.rxbookapp.data.provider.GitHubRepositoryContract;
import com.tehmou.rxbookapp.data.provider.NetworkRequestStatusContract;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.NetworkRequestStatus;

/**
 * Created by ttuo on 26/04/15.
 */
public class NetworkRequestStatusStore extends ContentProviderStoreBase<NetworkRequestStatus, NetworkRequestStatus.Key> {
    private static final String TAG = NetworkRequestStatusStore.class.getSimpleName();

    public NetworkRequestStatusStore(ContentResolver contentResolver) {
        super(contentResolver, new NetworkRequestStatusContract());
    }

    @Override
    protected NetworkRequestStatus.Key getIdFor(NetworkRequestStatus item) {
        return new NetworkRequestStatus.Key(item.getUri(), item.getOwner());
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
    public Uri getUriForKey(NetworkRequestStatus.Key id) {
        Uri uri = getContentUri();
        if (id.getOwner() == null) {
            uri = Uri.withAppendedPath(uri, "uri_hash/" + id.getUri().hashCode());
        } else {
            uri = Uri.withAppendedPath(uri, "owner/" + id.getOwner());
            uri = Uri.withAppendedPath(uri, "uri_hash/" + id.getUri().hashCode());
        }
        return uri;
    }

    @Override
    public void insertOrUpdate(NetworkRequestStatus item) {
        Log.v(TAG, "insertOrUpdate(" + item.getStatus() + ", " + item.getOwner() + ", " + item.getUri() + ")");
        super.insertOrUpdate(item);
    }
}
