package com.tehmou.rxbookapp.network;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.tehmou.rxbookapp.data.DataLayerBase;
import com.tehmou.rxbookapp.data.GitHubRepositorySearchStore;
import com.tehmou.rxbookapp.data.GitHubRepositoryStore;
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
public class ServiceDataLayer extends DataLayerBase {
    private static final String TAG = ServiceDataLayer.class.getSimpleName();
    final private NetworkApi networkApi;

    public ServiceDataLayer(ContentResolver contentResolver) {
        super(contentResolver);
        this.networkApi = new NetworkApi();
    }


    public void processIntent(Intent intent) {
        final String contentUriString = intent.getStringExtra("contentUriString");
        if (contentUriString != null) {
            final Uri uri = Uri.parse(contentUriString);
            if (uri.equals(gitHubRepositoryStore.getContentUri())) {
                final int repositoryId = intent.getIntExtra("id", -1);
                if (repositoryId != -1) {
                    fetchGitHubRepository(repositoryId);
                } else {
                    Log.e(TAG, "No repositoryId provided in the intent extras");
                }
            } else if (uri.equals(gitHubRepositorySearchStore.getContentUri())) {
                final String searchString = intent.getStringExtra("searchString");
                if (searchString != null) {
                    fetchGitHubSearch(searchString);
                } else {
                    Log.e(TAG, "No searchString provided in the intent extras");
                }
            } else {
                Log.e(TAG, "Unknown Uri " + uri);
            }
        } else {
            Log.e(TAG, "No Uri defined");
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
}
