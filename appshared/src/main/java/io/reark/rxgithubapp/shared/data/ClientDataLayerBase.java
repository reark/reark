package io.reark.rxgithubapp.shared.data;

import android.support.annotation.NonNull;

import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;
import io.reark.rxgithubapp.shared.pojo.UserSettings;

/**
 * Created by ttuo on 28/06/16.
 */
public class ClientDataLayerBase extends DataLayerBase {
    protected final StoreInterface<Integer, UserSettings> userSettingsStore;

    public ClientDataLayerBase(@NonNull StoreInterface<Integer, NetworkRequestStatus> networkRequestStatusStore,
                               @NonNull StoreInterface<Integer, GitHubRepository> gitHubRepositoryStore,
                               @NonNull StoreInterface<String, GitHubRepositorySearch> gitHubRepositorySearchStore,
                               @NonNull StoreInterface<Integer, UserSettings> userSettingsStore) {
        super(networkRequestStatusStore, gitHubRepositoryStore, gitHubRepositorySearchStore);
        Preconditions.checkNotNull(userSettingsStore,
                "User Settings Store cannot be null.");

        this.userSettingsStore = userSettingsStore;
    }
}
