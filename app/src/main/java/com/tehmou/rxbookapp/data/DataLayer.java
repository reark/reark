package com.tehmou.rxbookapp.data;

import com.tehmou.rxbookapp.data.provider.UserSettingsContract;
import com.tehmou.rxbookapp.data.stores.GitHubRepositorySearchStore;
import com.tehmou.rxbookapp.data.stores.GitHubRepositoryStore;
import com.tehmou.rxbookapp.data.stores.UserSettingsStore;
import com.tehmou.rxbookapp.network.NetworkApi;
import com.tehmou.rxbookapp.network.NetworkService;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;
import com.tehmou.rxbookapp.pojo.NetworkRequestStatus;
import com.tehmou.rxbookapp.pojo.UserSettings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;


/**
 * Created by ttuo on 19/03/14.
 */
public class DataLayer extends DataLayerBase {
    private static final String TAG = DataLayer.class.getSimpleName();
    private final Context context;
    protected final UserSettingsStore userSettingsStore;

    public DataLayer(ContentResolver contentResolver,
                     Context context) {
        super(contentResolver);
        this.context = context;
        userSettingsStore = new UserSettingsStore(contentResolver);
    }

    public Observable<GitHubRepositorySearch> fetchAndGetGitHubRepositorySearch(final String searchString) {
        fetchGitHubRepositorySearch(searchString);
        return gitHubRepositorySearchStore.getStream(searchString);
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

    public static interface GetUserSettings {
        Observable<UserSettings> call();
    }

    public static interface SetUserSettings {
        void call(UserSettings userSettings);
    }

    public static interface GetGitHubRepository {
        Observable<GitHubRepository> call(int repositoryId);
    }

    public static interface FetchAndGetGitHubRepository extends GetGitHubRepository {

    }

    public static interface GetGitHubRepositorySearch {
        Observable<GitHubRepositorySearch> call(String search);
    }
}
