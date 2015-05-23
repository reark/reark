package com.tehmou.rxbookapp.network;

import com.tehmou.rxbookapp.data.DataLayerBase;
import com.tehmou.rxbookapp.data.stores.GitHubRepositorySearchStore;
import com.tehmou.rxbookapp.data.stores.GitHubRepositoryStore;
import com.tehmou.rxbookapp.data.stores.NetworkRequestStatusStore;
import com.tehmou.rxbookapp.network.fetchers.Fetcher;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by ttuo on 16/04/15.
 */
public class ServiceDataLayer extends DataLayerBase {
    private static final String TAG = ServiceDataLayer.class.getSimpleName();
    final private Collection<Fetcher> fetchers;

    public ServiceDataLayer(Fetcher gitHubRepositoryFetcher,
                            Fetcher gitHubRepositorySearchFetcher,
                            NetworkRequestStatusStore networkRequestStatusStore,
                            GitHubRepositoryStore gitHubRepositoryStore,
                            GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        super(networkRequestStatusStore, gitHubRepositoryStore, gitHubRepositorySearchStore);
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
