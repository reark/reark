package com.tehmou.rxbookapp.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.tehmou.rxbookapp.data.stores.GitHubRepositoryStore;
import com.tehmou.rxbookapp.network.NetworkApi;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by ttuo on 16/04/15.
 */
public class GitHubRepositoryFetcher extends FetcherBase {
    private static final String TAG = GitHubRepositoryFetcher.class.getSimpleName();

    private final GitHubRepositoryStore gitHubRepositoryStore;

    public GitHubRepositoryFetcher(NetworkApi networkApi,
                                   GitHubRepositoryStore gitHubRepositoryStore) {
        super(networkApi);
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
        Observable.<GitHubRepository>create(subscriber -> {
            try {
                GitHubRepository repository = networkApi.getRepository(repositoryId);
                subscriber.onNext(repository);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        })
                .subscribeOn(Schedulers.computation())
                .subscribe(gitHubRepositoryStore::put,
                        e -> Log.e(TAG, "Error fetching GitHub repository " + repositoryId, e));
    }

    @Override
    public Uri getContentUri() {
        return gitHubRepositoryStore.getContentUri();
    }
}
