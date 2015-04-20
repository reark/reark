package com.tehmou.rxbookapp.data;

import com.tehmou.rxbookapp.network.FetchState;
import com.tehmou.rxbookapp.network.INetworkService;
import com.tehmou.rxbookapp.network.INetworkServiceListener;
import com.tehmou.rxbookapp.network.NetworkApi;
import com.tehmou.rxbookapp.network.NetworkService;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;
import com.tehmou.rxbookapp.pojo.UserSettings;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Notification;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;


/**
 * Created by ttuo on 19/03/14.
 */
public class DataLayer extends DataLayerBase {
    private static final String TAG = DataLayer.class.getSimpleName();
    private final Context context;
    protected final UserSettingsStore userSettingsStore;
    private final Observable<FetchState> networkRequestStateObservable;

    public DataLayer(ContentResolver contentResolver,
                     Context context) {
        super(contentResolver);
        this.context = context;
        userSettingsStore = new UserSettingsStore(contentResolver);

        Log.d(TAG, "Bind NetworkService");
        final Subject<FetchState, FetchState> networkRequestStateSubject = PublishSubject.create();
        networkRequestStateObservable = networkRequestStateSubject;
        Intent intent = new Intent(context, NetworkService.class);
        context.bindService(
                intent,
                new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        Log.d(TAG, "onServiceConnected(" + name + ")");
                        INetworkService networkService = INetworkService.Stub.asInterface(service);
                        try {
                            networkService.addStateListener(new INetworkServiceListener.Stub() {
                                        @Override
                                        public void handleStateChange(String uri, int networkRequestState) throws RemoteException {
                                            Log.d(TAG, "handleStateChange(" + uri + ", " + networkRequestState + ")");
                                            networkRequestStateSubject.onNext(
                                                    new FetchState(uri, networkRequestState)
                                            );
                                        }
                                    });
                        } catch (RemoteException e) {
                            Log.e(TAG, "Error adding stateListener", e);
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        Log.d(TAG, "onServiceDisconnected(" + name + ")");
                    }
                },
                0);
    }

    public Observable<FetchState> getFetchStateForUri(String uri) {
        return networkRequestStateObservable;
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

    public Observable<Notification<GitHubRepository>> getGitHubRepository(Integer repositoryId) {
        return gitHubRepositoryStore.getStream(repositoryId).materialize();
    }

    public Observable<Notification<GitHubRepository>> fetchAndGetGitHubRepository(Integer repositoryId) {
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
        return userSettingsStore.getStream(UserSettingsStore.DEFAULT_USER_ID);
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
        Observable<Notification<GitHubRepository>> call(int repositoryId);
    }

    public static interface FetchAndGetGitHubRepository extends GetGitHubRepository {

    }

    public static interface GetGitHubRepositorySearch {
        Observable<GitHubRepositorySearch> call(String search);
    }
}
