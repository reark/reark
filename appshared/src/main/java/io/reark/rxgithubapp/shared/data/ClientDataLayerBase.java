package io.reark.rxgithubapp.shared.data;

import android.support.annotation.NonNull;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.data.utils.DataLayerUtils;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.shared.network.fetchers.GitHubRepositorySearchFetcher;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;
import io.reark.rxgithubapp.shared.pojo.UserSettings;
import rx.Observable;

/**
 * Created by ttuo on 28/06/16.
 */
public abstract class ClientDataLayerBase extends DataLayerBase {
    private static final String TAG = ClientDataLayerBase.class.getSimpleName();
    public static final int DEFAULT_USER_ID = 0;

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

    @NonNull
    public Observable<DataStreamNotification<GitHubRepositorySearch>> getGitHubRepositorySearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string Store cannot be null.");

        Log.d(TAG, "getGitHubRepositorySearch(" + searchString + ")");
        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(
                        GitHubRepositorySearchFetcher.getUniqueId(searchString).hashCode());
        final Observable<GitHubRepositorySearch> gitHubRepositorySearchObservable =
                gitHubRepositorySearchStore.getStream(searchString);
        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, gitHubRepositorySearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<GitHubRepositorySearch>> fetchAndGetGitHubRepositorySearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string Store cannot be null.");

        Log.d(TAG, "fetchAndGetGitHubRepositorySearch(" + searchString + ")");
        fetchGitHubRepositorySearch(searchString);
        final Observable<DataStreamNotification<GitHubRepositorySearch>> gitHubRepositoryStream =
                getGitHubRepositorySearch(searchString);
        return gitHubRepositoryStream;
    }

    protected abstract void fetchGitHubRepositorySearch(@NonNull String searchString);

    @NonNull
    public Observable<GitHubRepository> getGitHubRepository(@NonNull Integer repositoryId) {
        Preconditions.checkNotNull(repositoryId, "Repository Id cannot be null.");

        return gitHubRepositoryStore.getStream(repositoryId);
    }

    @NonNull
    public Observable<GitHubRepository> fetchAndGetGitHubRepository(@NonNull Integer repositoryId) {
        Preconditions.checkNotNull(repositoryId, "Repository Id cannot be null.");

        fetchGitHubRepository(repositoryId);
        return getGitHubRepository(repositoryId);
    }

    protected abstract void fetchGitHubRepository(@NonNull Integer repositoryId);

    @NonNull
    public Observable<UserSettings> getUserSettings() {
        return userSettingsStore.getStream(DEFAULT_USER_ID);
    }

    public void setUserSettings(@NonNull UserSettings userSettings) {
        Preconditions.checkNotNull(userSettings, "User Settings cannot be null.");

        userSettingsStore.put(userSettings);
    }
}
