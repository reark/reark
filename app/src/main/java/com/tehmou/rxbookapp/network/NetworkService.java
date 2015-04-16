package com.tehmou.rxbookapp.network;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.tehmou.rxbookapp.data.GitHubRepositoryStore;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by ttuo on 16/04/15.
 */
public class NetworkService extends Service {
    private static final String TAG = NetworkService.class.getSimpleName();

    private NetworkApi networkApi;
    private GitHubRepositoryStore gitHubRepositoryStore;

    @Override
    public void onCreate() {
        super.onCreate();
        networkApi = new NetworkApi();
        gitHubRepositoryStore = new GitHubRepositoryStore(getContentResolver());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent != null) {
            final String contentUriString = intent.getStringExtra("contentUriString");
            if (contentUriString != null) {
                final Uri uri = Uri.parse(contentUriString);
                if (uri.equals(gitHubRepositoryStore.getContentUri())) {
                    final int repositoryId = intent.getIntExtra("id", -1);
                    if (repositoryId == -1) {
                        Log.e(TAG, "No repositoryId provided in the intent extras");
                    } else {
                        getGitHubRepository(repositoryId);
                    }
                } else {
                    Log.e(TAG, "Unknown Uri " + uri);
                }
            } else {
                Log.e(TAG, "No Uri defined");
            }
        } else {
            Log.d(TAG, "Intent was null, no action taken");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void getGitHubRepository(final int repositoryId) {
        Log.d(TAG, "getGitHubRepository(" + repositoryId + ")");
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
                e -> Log.d(TAG, "Error fetching GitHub repository " + repositoryId, e));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
