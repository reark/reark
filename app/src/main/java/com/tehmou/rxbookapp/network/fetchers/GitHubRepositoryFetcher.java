package com.tehmou.rxbookapp.network.fetchers;

import com.tehmou.rxbookapp.data.stores.GitHubRepositoryStore;
import com.tehmou.rxbookapp.network.NetworkApi;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.NetworkRequestStatus;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by ttuo on 16/04/15.
 */
public class GitHubRepositoryFetcher extends FetcherBase {
    private static final String TAG = GitHubRepositoryFetcher.class.getSimpleName();

    private final GitHubRepositoryStore gitHubRepositoryStore;

    public GitHubRepositoryFetcher(NetworkApi networkApi,
                                   Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                                   GitHubRepositoryStore gitHubRepositoryStore) {
        super(networkApi, updateNetworkRequestStatus);
        this.gitHubRepositoryStore = gitHubRepositoryStore;
    }

    @Override
    public void fetch(Intent intent) {
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

    private Observable<GitHubRepository> createNetworkObservable(int repositoryId) {
        return Observable.<GitHubRepository>create(subscriber -> {
            try {
                GitHubRepository repository = networkApi.getRepository(repositoryId);
                subscriber.onNext(repository);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Uri getContentUri() {
        return gitHubRepositoryStore.getContentUri();
    }
}
