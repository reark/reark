package com.tehmou.rxbookapp.data;

import com.tehmou.rxbookapp.data.provider.UserSettingsContract;
import com.tehmou.rxbookapp.data.stores.GitHubRepositorySearchStore;
import com.tehmou.rxbookapp.data.stores.GitHubRepositoryStore;
import com.tehmou.rxbookapp.data.stores.NetworkRequestStatusStore;
import com.tehmou.rxbookapp.data.stores.UserSettingsStore;
import com.tehmou.rxbookapp.data.utils.DataLayerUtils;
import com.tehmou.rxbookapp.network.NetworkService;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;
import com.tehmou.rxbookapp.pojo.NetworkRequestStatus;
import com.tehmou.rxbookapp.pojo.UserSettings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import rx.Observable;


/**
 * Created by ttuo on 19/03/14.
 */
public class DataLayer extends DataLayerBase {
    private static final String TAG = DataLayer.class.getSimpleName();
    private final Context context;
    protected final UserSettingsStore userSettingsStore;

    public DataLayer(Context context,
                     UserSettingsStore userSettingsStore,
                     NetworkRequestStatusStore networkRequestStatusStore,
                     GitHubRepositoryStore gitHubRepositoryStore,
                     GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        super(networkRequestStatusStore, gitHubRepositoryStore, gitHubRepositorySearchStore);
        this.context = context;
        this.userSettingsStore = userSettingsStore;
    }

    public Observable<DataStreamNotification<GitHubRepositorySearch>> getGitHubRepositorySearch(final String searchString) {
        final Uri uri = gitHubRepositorySearchStore.getUriForKey(searchString);
        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());
        final Observable<GitHubRepositorySearch> gitHubRepositorySearchObservable =
                gitHubRepositorySearchStore.getStream(searchString);
        return DataLayerUtils.createDataStreamNotificationObservable(
                        networkRequestStatusObservable, gitHubRepositorySearchObservable);
    }

    public Observable<DataStreamNotification<GitHubRepositorySearch>> fetchAndGetGitHubRepositorySearch(final String searchString) {
        final Observable<DataStreamNotification<GitHubRepositorySearch>> gitHubRepositoryStream =
                getGitHubRepositorySearch(searchString);
        fetchGitHubRepositorySearch(searchString);
        return gitHubRepositoryStream;
    }

    private void fetchGitHubRepositorySearch(final String searchString) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("contentUriString", gitHubRepositorySearchStore.getContentUri().toString());
        intent.putExtra("searchString", searchString);
        context.startService(intent);
    }

    public Observable<GitHubRepository> getGitHubRepository(Integer repositoryId) {
        return gitHubRepositoryStore.getStream(repositoryId);
    }

    public Observable<GitHubRepository> fetchAndGetGitHubRepository(Integer repositoryId) {
        fetchGitHubRepository(repositoryId);
        return getGitHubRepository(repositoryId);
    }

    private void fetchGitHubRepository(Integer repositoryId) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("contentUriString", gitHubRepositoryStore.getContentUri().toString());
        intent.putExtra("id", repositoryId);
        context.startService(intent);
    }

    public Observable<UserSettings> getUserSettings() {
        return userSettingsStore.getStream(UserSettingsContract.DEFAULT_USER_ID);
    }

    public void setUserSettings(UserSettings userSettings) {
        userSettingsStore.insertOrUpdate(userSettings);
    }

    public interface GetUserSettings {
        Observable<UserSettings> call();
    }

    public interface SetUserSettings {
        void call(UserSettings userSettings);
    }

    public interface GetGitHubRepository {
        Observable<GitHubRepository> call(int repositoryId);
    }

    public interface FetchAndGetGitHubRepository extends GetGitHubRepository {

    }

    public interface GetGitHubRepositorySearch {
        Observable<DataStreamNotification<GitHubRepositorySearch>> call(String search);
    }
}
