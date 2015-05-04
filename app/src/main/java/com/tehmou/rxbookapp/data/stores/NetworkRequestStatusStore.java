package com.tehmou.rxbookapp.data.stores;

import android.content.ContentResolver;
import android.net.Uri;

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
    public NetworkRequestStatusStore(ContentResolver contentResolver) {
        super(contentResolver,
                new NetworkRequestStatusContract());
    }

    @Override
    protected NetworkRequestStatus.Key getIdFor(NetworkRequestStatus item) {
        return new NetworkRequestStatus.Key(item.getUri(), item.getOwner());
    }

    @Override
    public Uri getContentUri() {
        return GitHubRepositoryContract.CONTENT_URI;
    }

    @Override
    protected NetworkRequestStatus query(Uri uri) {
        return super.query(uri);
    }

    @Override
    protected Uri getUriForId(NetworkRequestStatus.Key id) {
        Uri uri = getContentUri();
        uri = Uri.withAppendedPath(uri, id.getOwner());
        uri = Uri.withAppendedPath(uri, "" + id.getUri().hashCode());
        return uri;
    }
}
