package com.tehmou.rxbookapp.data;

import com.tehmou.rxbookapp.data.stores.GitHubRepositorySearchStore;
import com.tehmou.rxbookapp.data.stores.GitHubRepositoryStore;
import com.tehmou.rxbookapp.data.stores.NetworkRequestStatusStore;

/**
 * Created by ttuo on 16/04/15.
 */
abstract public class DataLayerBase {
    protected final NetworkRequestStatusStore networkRequestStatusStore;
    protected final GitHubRepositoryStore gitHubRepositoryStore;
    protected final GitHubRepositorySearchStore gitHubRepositorySearchStore;

    public DataLayerBase(NetworkRequestStatusStore networkRequestStatusStore,
                         GitHubRepositoryStore gitHubRepositoryStore,
                         GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        this.networkRequestStatusStore = networkRequestStatusStore;
        this.gitHubRepositoryStore = gitHubRepositoryStore;
        this.gitHubRepositorySearchStore = gitHubRepositorySearchStore;
    }
}
