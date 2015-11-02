package io.reark.rxgithubapp.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.data.utils.DataLayerUtils;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.data.stores.GitHubRepositorySearchStore;
import io.reark.rxgithubapp.data.stores.GitHubRepositoryStore;
import io.reark.rxgithubapp.data.stores.NetworkRequestStatusStore;
import io.reark.rxgithubapp.data.stores.UserSettingsStore;
import io.reark.rxgithubapp.network.NetworkService;
import io.reark.rxgithubapp.network.fetchers.GitHubRepositorySearchFetcher;
import io.reark.rxgithubapp.pojo.GitHubRepository;
import io.reark.rxgithubapp.pojo.GitHubRepositorySearch;
import io.reark.rxgithubapp.pojo.UserSettings;
import rx.Observable;


/**
 * Created by ttuo on 19/03/14.
 */
public class DataLayer extends DataLayerBase {
    private static final String TAG = DataLayer.class.getSimpleName();
    private final Context context;
    protected final UserSettingsStore userSettingsStore;
    public static final int DEFAULT_USER_ID = 0;

    public DataLayer(@NonNull Context context,
                     @NonNull UserSettingsStore userSettingsStore,
                     @NonNull NetworkRequestStatusStore networkRequestStatusStore,
                     @NonNull GitHubRepositoryStore gitHubRepositoryStore,
                     @NonNull GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        super(networkRequestStatusStore, gitHubRepositoryStore, gitHubRepositorySearchStore);

        Preconditions.checkNotNull(context, "Context cannot be null.");
        Preconditions.checkNotNull(userSettingsStore, "User Settings Store cannot be null.");

        this.context = context;
        this.userSettingsStore = userSettingsStore;
    }

    @NonNull
    public Observable<DataStreamNotification<GitHubRepositorySearch>> getGitHubRepositorySearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string Store cannot be null.");

        final Uri uri = gitHubRepositorySearchStore.getUriForKey(searchString);
        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());
        final Observable<GitHubRepositorySearch> gitHubRepositorySearchObservable =
                gitHubRepositorySearchStore.getStream(searchString);
        return DataLayerUtils.createDataStreamNotificationObservable(
                        networkRequestStatusObservable, gitHubRepositorySearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<GitHubRepositorySearch>> fetchAndGetGitHubRepositorySearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string Store cannot be null.");

        final Observable<DataStreamNotification<GitHubRepositorySearch>> gitHubRepositoryStream =
                getGitHubRepositorySearch(searchString);
        fetchGitHubRepositorySearch(searchString);
        return gitHubRepositoryStream;
    }

    private void fetchGitHubRepositorySearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string Store cannot be null.");

        Intent intent = new Intent(context, NetworkService.class);

        // Fetchers can be identified either by contentUri, which selects first fetcher
        // implementing the content type, or an identifier that can be used to choose a
        // specific fetcher from multiple fetchers that return the same value types.
        intent.putExtra("contentUriString", gitHubRepositorySearchStore.getContentUri().toString());
        intent.putExtra("fetcherIdentifier", GitHubRepositorySearchFetcher.IDENTIFIER);

        intent.putExtra("searchString", searchString);
        context.startService(intent);
    }

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

    private void fetchGitHubRepository(@NonNull Integer repositoryId) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("contentUriString", gitHubRepositoryStore.getContentUri().toString());
        intent.putExtra("id", repositoryId);
        context.startService(intent);
    }

    @NonNull
    public Observable<UserSettings> getUserSettings() {
        return userSettingsStore.getStream(DEFAULT_USER_ID);
    }

    public void setUserSettings(@NonNull UserSettings userSettings) {
        Preconditions.checkNotNull(userSettings, "User Settings cannot be null.");

        userSettingsStore.put(userSettings);
    }

    public interface GetUserSettings {
        @NonNull
        Observable<UserSettings> call();
    }

    public interface SetUserSettings {
        void call(@NonNull UserSettings userSettings);
    }

    public interface GetGitHubRepository {
        @NonNull
        Observable<GitHubRepository> call(int repositoryId);
    }

    public interface FetchAndGetGitHubRepository extends GetGitHubRepository {

    }

    public interface GetGitHubRepositorySearch {
        @NonNull
        Observable<DataStreamNotification<GitHubRepositorySearch>> call(@NonNull String search);
    }
}
