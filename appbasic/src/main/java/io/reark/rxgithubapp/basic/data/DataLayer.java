package io.reark.rxgithubapp.basic.data;

import android.content.Intent;
import android.support.annotation.NonNull;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.data.utils.DataLayerUtils;
import io.reark.reark.network.fetchers.UriFetcherManager;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.basic.data.stores.GitHubRepositorySearchStore;
import io.reark.rxgithubapp.basic.data.stores.GitHubRepositoryStore;
import io.reark.rxgithubapp.basic.data.stores.NetworkRequestStatusStore;
import io.reark.rxgithubapp.basic.data.stores.UserSettingsStore;
import io.reark.rxgithubapp.shared.network.GitHubService;
import io.reark.rxgithubapp.shared.network.fetchers.GitHubRepositorySearchFetcher;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;
import io.reark.rxgithubapp.shared.pojo.UserSettings;
import rx.Observable;

/**
 * Created by ttuo on 27/06/16.
 */
public class DataLayer {
    private static final String TAG = DataLayer.class.getSimpleName();

    private final UriFetcherManager fetcherManager;
    private final NetworkRequestStatusStore networkRequestStatusStore;
    private final GitHubRepositoryStore gitHubRepositoryStore;
    private final GitHubRepositorySearchStore gitHubRepositorySearchStore;
    private final UserSettingsStore userSettingsStore;
    public static final int DEFAULT_USER_ID = 0;

    public DataLayer(UriFetcherManager fetcherManager,
                     NetworkRequestStatusStore networkRequestStatusStore,
                     GitHubRepositoryStore gitHubRepositoryStore,
                     GitHubRepositorySearchStore gitHubRepositorySearchStore,
                     UserSettingsStore userSettingsStore) {
        this.fetcherManager = fetcherManager;
        this.networkRequestStatusStore = networkRequestStatusStore;
        this.gitHubRepositoryStore = gitHubRepositoryStore;
        this.gitHubRepositorySearchStore = gitHubRepositorySearchStore;
        this.userSettingsStore = userSettingsStore;
    }

    public Observable<GitHubRepository> fetchAndGetGitHubRepository(int repositoryId) {
        Intent intent = new Intent();
        intent.putExtra("serviceUriString", GitHubService.REPOSITORY.toString());
        intent.putExtra("id", repositoryId);
        fetcherManager.findFetcher(GitHubService.REPOSITORY).fetch(intent);
        return getGitHubRepository(repositoryId);
    }

    public Observable<GitHubRepository> getGitHubRepository(int repositoryId) {
        return gitHubRepositoryStore.getStream(repositoryId);
    }

    @NonNull
    public Observable<UserSettings> getUserSettings() {
        return userSettingsStore.getStream(DEFAULT_USER_ID);
    }

    public void setUserSettings(@NonNull UserSettings userSettings) {
        Preconditions.checkNotNull(userSettings, "User Settings cannot be null.");

        userSettingsStore.put(userSettings);
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
        final Observable<DataStreamNotification<GitHubRepositorySearch>> gitHubRepositoryStream =
                getGitHubRepositorySearch(searchString);
        fetchGitHubRepositorySearch(searchString);
        return gitHubRepositoryStream;
    }

    private void fetchGitHubRepositorySearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string Store cannot be null.");

        Log.d(TAG, "fetchGitHubRepositorySearch(" + searchString + ")");
        Intent intent = new Intent();
        intent.putExtra("serviceUriString", GitHubService.REPOSITORY_SEARCH.toString());
        intent.putExtra("searchString", searchString);
        fetcherManager.findFetcher(GitHubService.REPOSITORY_SEARCH).fetch(intent);
    }
}
