package com.tehmou.rxbookapp.network;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.tehmou.rxbookapp.data.DataLayerBase;
import com.tehmou.rxbookapp.data.stores.GitHubRepositorySearchStore;
import com.tehmou.rxbookapp.data.stores.GitHubRepositoryStore;
import com.tehmou.rxbookapp.data.stores.NetworkRequestStatusStore;
import com.tehmou.rxbookapp.network.fetchers.Fetcher;
import com.tehmou.rxbookapp.network.fetchers.GitHubRepositoryFetcher;
import com.tehmou.rxbookapp.network.fetchers.GitHubRepositorySearchFetcher;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by ttuo on 16/04/15.
 */
public class ServiceDataLayer extends DataLayerBase {
    private static final String TAG = ServiceDataLayer.class.getSimpleName();
    final private Collection<Fetcher> fetchers;

    public ServiceDataLayer(NetworkApi networkApi,
                            NetworkRequestStatusStore networkRequestStatusStore,
                            GitHubRepositoryStore gitHubRepositoryStore,
                            GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        super(networkRequestStatusStore, gitHubRepositoryStore,
              gitHubRepositorySearchStore);
        final Fetcher gitHubRepositoryFetcher = new GitHubRepositoryFetcher(
                networkApi, networkRequestStatusStore::insertOrUpdate,
                gitHubRepositoryStore);
        final Fetcher gitHubRepositorySearchFetcher = new GitHubRepositorySearchFetcher(
                networkApi, networkRequestStatusStore::insertOrUpdate,
                gitHubRepositoryStore, gitHubRepositorySearchStore);
        fetchers = Arrays.asList(
                gitHubRepositoryFetcher,
                gitHubRepositorySearchFetcher
        );
    }

    public void processIntent(Intent intent) {
        final String contentUriString = intent.getStringExtra("contentUriString");
        if (contentUriString != null) {
            final Uri contentUri = Uri.parse(contentUriString);
            Fetcher matchingFetcher = findFetcher(contentUri);
            if (matchingFetcher != null) {
                Log.v(TAG, "Fetcher found for " + contentUri);
                matchingFetcher.fetch(intent);
            } else {
                Log.e(TAG, "Unknown Uri " + contentUri);
            }
        } else {
            Log.e(TAG, "No Uri defined");
        }
    }

    private Fetcher findFetcher(Uri contentUri) {
        for (Fetcher fetcher : fetchers) {
            if (fetcher.getContentUri().equals(contentUri)) {
                return fetcher;
            }
        }
        return null;
    }
}
