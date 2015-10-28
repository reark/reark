package com.tehmou.rxbookapp.network;

import com.tehmou.rxbookapp.data.DataLayerBase;
import com.tehmou.rxbookapp.data.stores.GitHubRepositorySearchStore;
import com.tehmou.rxbookapp.data.stores.GitHubRepositoryStore;
import com.tehmou.rxbookapp.data.stores.NetworkRequestStatusStore;
import com.tehmou.rxandroidarchitecture.network.fetchers.Fetcher;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;
import java.util.Collection;

import com.tehmou.rxandroidarchitecture.utils.Preconditions;

/**
 * Created by ttuo on 16/04/15.
 */
public class ServiceDataLayer extends DataLayerBase {
    private static final String TAG = ServiceDataLayer.class.getSimpleName();

    @NonNull
    final private Collection<Fetcher> fetchers;

    public ServiceDataLayer(@NonNull Fetcher gitHubRepositoryFetcher,
                            @NonNull Fetcher gitHubRepositorySearchFetcher,
                            @NonNull NetworkRequestStatusStore networkRequestStatusStore,
                            @NonNull GitHubRepositoryStore gitHubRepositoryStore,
                            @NonNull GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        super(networkRequestStatusStore, gitHubRepositoryStore, gitHubRepositorySearchStore);

        Preconditions.checkNotNull(gitHubRepositoryFetcher,
                                   "GitHub Repository Fetcher cannot be null.");
        Preconditions.checkNotNull(gitHubRepositorySearchFetcher,
                                   "GitHub Repository Search Fetcher cannot be null.");

        fetchers = Arrays.asList(
                gitHubRepositoryFetcher,
                gitHubRepositorySearchFetcher
        );
    }

    public void processIntent(@NonNull Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null.");

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

    @Nullable
    private Fetcher findFetcher(@NonNull Uri contentUri) {
        Preconditions.checkNotNull(contentUri, "Content URL cannot be null.");

        for (Fetcher fetcher : fetchers) {
            if (fetcher.getContentUri().equals(contentUri)) {
                return fetcher;
            }
        }
        return null;
    }
}
