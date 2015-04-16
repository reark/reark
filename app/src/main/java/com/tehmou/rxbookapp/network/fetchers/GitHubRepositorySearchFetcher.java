package com.tehmou.rxbookapp.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.tehmou.rxbookapp.data.stores.GitHubRepositorySearchStore;
import com.tehmou.rxbookapp.data.stores.GitHubRepositoryStore;
import com.tehmou.rxbookapp.network.NetworkApi;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by ttuo on 16/04/15.
 */
public class GitHubRepositorySearchFetcher extends FetcherBase {
    private static final String TAG = GitHubRepositorySearchFetcher.class.getSimpleName();

    private final GitHubRepositoryStore gitHubRepositoryStore;
    private final GitHubRepositorySearchStore gitHubRepositorySearchStore;

    public GitHubRepositorySearchFetcher(NetworkApi networkApi,
                                         GitHubRepositoryStore gitHubRepositoryStore,
                                         GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        super(networkApi);
        this.gitHubRepositoryStore = gitHubRepositoryStore;
        this.gitHubRepositorySearchStore = gitHubRepositorySearchStore;
    }

    @Override
    public void fetch(Intent intent) {
        final String searchString = intent.getStringExtra("searchString");
        if (searchString != null) {
            fetchGitHubSearch(searchString);
        } else {
            Log.e(TAG, "No searchString provided in the intent extras");
        }
    }

    private void fetchGitHubSearch(final String searchString) {
        Log.d(TAG, "fetchGitHubSearch(" + searchString + ")");
        Observable.<List<GitHubRepository>>create((subscriber) -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("q", searchString);
                List<GitHubRepository> results = networkApi.search(params);
                subscriber.onNext(results);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        })
                .subscribeOn(Schedulers.computation())
                .map((repositories) -> {
                    final List<Integer> repositoryIds = new ArrayList<>();
                    for (GitHubRepository repository : repositories) {
                        gitHubRepositoryStore.put(repository);
                        repositoryIds.add(repository.getId());
                    }
                    return new GitHubRepositorySearch(searchString, repositoryIds);
                })
                .subscribe(gitHubRepositorySearchStore::put,
                        e -> Log.e(TAG, "Error fetching GitHub repository search for '" + searchString + "'", e));
    }

    @Override
    public Uri getContentUri() {
        return gitHubRepositorySearchStore.getContentUri();
    }
}
