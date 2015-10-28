package io.reark.rxbookapp.data;

import android.support.annotation.NonNull;

import io.reark.reark.utils.Preconditions;
import io.reark.rxbookapp.data.stores.GitHubRepositorySearchStore;
import io.reark.rxbookapp.data.stores.GitHubRepositoryStore;
import io.reark.rxbookapp.data.stores.NetworkRequestStatusStore;

/**
 * Created by ttuo on 16/04/15.
 */
abstract public class DataLayerBase {
    protected final NetworkRequestStatusStore networkRequestStatusStore;
    protected final GitHubRepositoryStore gitHubRepositoryStore;
    protected final GitHubRepositorySearchStore gitHubRepositorySearchStore;

    public DataLayerBase(@NonNull NetworkRequestStatusStore networkRequestStatusStore,
                         @NonNull GitHubRepositoryStore gitHubRepositoryStore,
                         @NonNull GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        Preconditions.checkNotNull(networkRequestStatusStore,
                                   "Network Request Status Store cannot be null.");
        Preconditions.checkNotNull(gitHubRepositoryStore,
                                   "GitHub Repository Store cannot be null.");
        Preconditions.checkNotNull(gitHubRepositorySearchStore,
                                   "GitHub Repository Search Store cannot be null.");

        this.networkRequestStatusStore = networkRequestStatusStore;
        this.gitHubRepositoryStore = gitHubRepositoryStore;
        this.gitHubRepositorySearchStore = gitHubRepositorySearchStore;
    }
}
