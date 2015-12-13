package io.reark.rxgithubapp.network;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;

import io.reark.reark.network.fetchers.Fetcher;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.data.DataLayerBase;
import io.reark.rxgithubapp.data.stores.GitHubRepositorySearchStore;
import io.reark.rxgithubapp.data.stores.GitHubRepositoryStore;
import io.reark.rxgithubapp.data.stores.NetworkRequestStatusStore;
import io.reark.rxgithubapp.network.fetchers.FetcherManager;

/**
 * Created by ttuo on 16/04/15.
 */
public class ServiceDataLayer extends DataLayerBase {
    private static final String TAG = ServiceDataLayer.class.getSimpleName();

    final private FetcherManager fetcherManager;

    public ServiceDataLayer(@NonNull FetcherManager fetcherManager,
                            @NonNull NetworkRequestStatusStore networkRequestStatusStore,
                            @NonNull GitHubRepositoryStore gitHubRepositoryStore,
                            @NonNull GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        super(networkRequestStatusStore, gitHubRepositoryStore, gitHubRepositorySearchStore);

        Preconditions.checkNotNull(fetcherManager,
                "FetcherManager cannot be null.");
        this.fetcherManager = fetcherManager;
    }

    public void processIntent(@NonNull Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null.");

        final String serviceUriString = intent.getStringExtra("serviceUriString");
        if (serviceUriString != null) {
            final Uri serviceUri = Uri.parse(serviceUriString);
            Fetcher matchingFetcher = fetcherManager.findFetcher(serviceUri);
            if (matchingFetcher != null) {
                Log.v(TAG, "Fetcher found for " + serviceUri);
                matchingFetcher.fetch(intent);
            } else {
                Log.e(TAG, "Unknown Uri " + serviceUri);
            }
        } else {
            Log.e(TAG, "No Uri defined");
        }
    }
}
