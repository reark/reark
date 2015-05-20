package com.tehmou.rxbookapp.data;

import android.content.ContentResolver;
import android.net.Uri;

import com.tehmou.rxbookapp.data.stores.GitHubRepositorySearchStore;
import com.tehmou.rxbookapp.data.stores.GitHubRepositoryStore;
import com.tehmou.rxbookapp.data.stores.NetworkRequestStatusStore;
import com.tehmou.rxbookapp.pojo.NetworkRequestStatus;
import com.tehmou.rxbookapp.pojo.UserSettings;

import java.util.List;

import rx.Observable;

/**
 * Created by ttuo on 16/04/15.
 */
abstract public class DataLayerBase {
    protected final NetworkRequestStatusStore networkRequestStatusStore;
    protected final GitHubRepositoryStore gitHubRepositoryStore;
    protected final GitHubRepositorySearchStore gitHubRepositorySearchStore;

    public DataLayerBase(ContentResolver contentResolver) {
        networkRequestStatusStore = new NetworkRequestStatusStore(contentResolver);
        gitHubRepositoryStore = new GitHubRepositoryStore(contentResolver);
        gitHubRepositorySearchStore = new GitHubRepositorySearchStore(contentResolver);
    }
}
