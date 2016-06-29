package io.reark.rxgithubapp.basic.data;

import android.content.Intent;
import android.support.annotation.NonNull;

import io.reark.reark.network.fetchers.UriFetcherManager;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.basic.data.stores.GitHubRepositorySearchStore;
import io.reark.rxgithubapp.basic.data.stores.GitHubRepositoryStore;
import io.reark.rxgithubapp.basic.data.stores.NetworkRequestStatusStore;
import io.reark.rxgithubapp.basic.data.stores.UserSettingsStore;
import io.reark.rxgithubapp.shared.data.ClientDataLayerBase;
import io.reark.rxgithubapp.shared.network.GitHubService;

/**
 * Created by ttuo on 27/06/16.
 */
public class DataLayer extends ClientDataLayerBase {
    private static final String TAG = DataLayer.class.getSimpleName();

    private final UriFetcherManager fetcherManager;

    public DataLayer(@NonNull UriFetcherManager fetcherManager,
                     @NonNull NetworkRequestStatusStore networkRequestStatusStore,
                     @NonNull GitHubRepositoryStore gitHubRepositoryStore,
                     @NonNull GitHubRepositorySearchStore gitHubRepositorySearchStore,
                     @NonNull UserSettingsStore userSettingsStore) {
        super(networkRequestStatusStore,
                gitHubRepositoryStore,
                gitHubRepositorySearchStore,
                userSettingsStore);

        Preconditions.checkNotNull(fetcherManager, "Context cannot be null.");
        Preconditions.checkNotNull(userSettingsStore, "User Settings Store cannot be null.");

        this.fetcherManager = fetcherManager;
    }

    @Override
    protected void fetchGitHubRepository(@NonNull Integer repositoryId) {
        Preconditions.checkNotNull(repositoryId, "Repository Id cannot be null.");

        Intent intent = new Intent();
        intent.putExtra("serviceUriString", GitHubService.REPOSITORY.toString());
        intent.putExtra("id", repositoryId);
        fetcherManager.findFetcher(GitHubService.REPOSITORY).fetch(intent);
    }

    @Override
    protected void fetchGitHubRepositorySearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string Store cannot be null.");

        Log.d(TAG, "fetchGitHubRepositorySearch(" + searchString + ")");
        Intent intent = new Intent();
        intent.putExtra("serviceUriString", GitHubService.REPOSITORY_SEARCH.toString());
        intent.putExtra("searchString", searchString);
        fetcherManager.findFetcher(GitHubService.REPOSITORY_SEARCH).fetch(intent);
    }
}
