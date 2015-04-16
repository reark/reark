package com.tehmou.rxbookapp.network;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

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
public class NetworkService extends Service {
    private static final String TAG = NetworkService.class.getSimpleName();

    private NetworkApi networkApi;
    private ServiceDataLayer serviceDataLayer;

    @Override
    public void onCreate() {
        super.onCreate();
        networkApi = new NetworkApi();
        serviceDataLayer = new ServiceDataLayer(getContentResolver());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent != null) {
            processIntent(intent);
        } else {
            Log.d(TAG, "Intent was null, no action taken");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void processIntent(Intent intent) {
        final String contentUriString = intent.getStringExtra("contentUriString");
        if (contentUriString != null) {
            final Uri uri = Uri.parse(contentUriString);
            if (uri.equals(serviceDataLayer.getGitHubRepositoryStore().getContentUri())) {
                final int repositoryId = intent.getIntExtra("id", -1);
                if (repositoryId != -1) {
                    fetchGitHubRepository(repositoryId);
                } else {
                    Log.e(TAG, "No repositoryId provided in the intent extras");
                }
            } else if (uri.equals(serviceDataLayer.getGitHubRepositorySearchStore().getContentUri())) {
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
        .subscribe(serviceDataLayer.getGitHubRepositoryStore()::put,
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
                serviceDataLayer.getGitHubRepositoryStore().put(repository);
                repositoryIds.add(repository.getId());
            }
            return new GitHubRepositorySearch(searchString, repositoryIds);
        })
        .subscribe(serviceDataLayer.getGitHubRepositorySearchStore()::put,
                e -> Log.e(TAG, "Error fetching GitHub repository search for '" + searchString + "'", e));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
