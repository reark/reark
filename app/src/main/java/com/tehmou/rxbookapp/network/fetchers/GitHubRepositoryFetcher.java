package com.tehmou.rxbookapp.network.fetchers;

import com.tehmou.rxandroidarchitecture.network.fetchers.FetcherBase;
import com.tehmou.rxbookapp.data.stores.GitHubRepositoryStore;
import com.tehmou.rxbookapp.network.NetworkApi;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxandroidarchitecture.pojo.NetworkRequestStatus;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import rx.Observable;
import rx.Subscription;
import com.tehmou.rxandroidarchitecture.utils.Preconditions;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by ttuo on 16/04/15.
 */
public class GitHubRepositoryFetcher extends AppFetcherBase {
    private static final String TAG = GitHubRepositoryFetcher.class.getSimpleName();

    @NonNull
    private final GitHubRepositoryStore gitHubRepositoryStore;

    public GitHubRepositoryFetcher(@NonNull NetworkApi networkApi,
                                   @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                                   @NonNull GitHubRepositoryStore gitHubRepositoryStore) {
        super(networkApi, updateNetworkRequestStatus);

        Preconditions.checkNotNull(gitHubRepositoryStore, "GitHub Repository Store cannot be null.");

        this.gitHubRepositoryStore = gitHubRepositoryStore;
    }

    @Override
    public void fetch(@NonNull Intent intent) {
        Preconditions.checkNotNull(intent, "Fetch Intent cannot be null.");

        final int repositoryId = intent.getIntExtra("id", -1);
        if (repositoryId != -1) {
            fetchGitHubRepository(repositoryId);
        } else {
            Log.e(TAG, "No repositoryId provided in the intent extras");
        }
    }

    private void fetchGitHubRepository(final int repositoryId) {
        Log.d(TAG, "fetchGitHubRepository(" + repositoryId + ")");
        if (requestMap.containsKey(repositoryId) &&
                !requestMap.get(repositoryId).isUnsubscribed()) {
            Log.d(TAG, "Found an ongoing request for repository " + repositoryId);
            return;
        }
        final String uri = gitHubRepositoryStore.getUriForKey(repositoryId).toString();
        Subscription subscription = createNetworkObservable(repositoryId)
                .subscribeOn(Schedulers.computation())
                .doOnError(doOnError(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .subscribe(gitHubRepositoryStore::put,
                        e -> Log.e(TAG, "Error fetching GitHub repository " + repositoryId, e));
        requestMap.put(repositoryId, subscription);
        startRequest(uri);
    }

    @NonNull
    private Observable<GitHubRepository> createNetworkObservable(int repositoryId) {
        return networkApi.getRepository(repositoryId);
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return gitHubRepositoryStore.getContentUri();
    }
}
