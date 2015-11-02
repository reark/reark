package io.reark.rxgithubapp.network;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;
import java.util.Collection;

import io.reark.reark.network.fetchers.Fetcher;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.data.DataLayerBase;
import io.reark.rxgithubapp.data.stores.GitHubRepositorySearchStore;
import io.reark.rxgithubapp.data.stores.GitHubRepositoryStore;
import io.reark.rxgithubapp.data.stores.NetworkRequestStatusStore;

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

        final String fetcherIdentifier = intent.getStringExtra("fetcherIdentifier");
        final String contentUriString = intent.getStringExtra("contentUriString");

        if (fetcherIdentifier != null) {
            Fetcher matchingFetcher = findFetcherByIdentifier(fetcherIdentifier);
            if (matchingFetcher != null) {
                Log.v(TAG, "Fetcher found for " + fetcherIdentifier);
                matchingFetcher.fetch(intent);
            } else {
                Log.e(TAG, "Unknown identifier " + fetcherIdentifier);
            }
        } else if (contentUriString != null) {
            final Uri contentUri = Uri.parse(contentUriString);
            Fetcher matchingFetcher = findFetcherByContentUri(contentUri);
            if (matchingFetcher != null) {
                Log.v(TAG, "Fetcher found for " + contentUri);
                matchingFetcher.fetch(intent);
            } else {
                Log.e(TAG, "Unknown Uri " + contentUri);
            }
        } else {
            Log.e(TAG, "No fetcher found");
        }
    }

    @Nullable
    private Fetcher findFetcherByIdentifier(@NonNull String identifier) {
        Preconditions.checkNotNull(identifier, "Identifier cannot be null.");

        for (Fetcher fetcher : fetchers) {
            if (fetcher.getIdentifier().equals(identifier)) {
                return fetcher;
            }
        }
        return null;
    }

    @Nullable
    private Fetcher findFetcherByContentUri(@NonNull Uri contentUri) {
        Preconditions.checkNotNull(contentUri, "Content URL cannot be null.");

        for (Fetcher fetcher : fetchers) {
            if (fetcher.getContentUri().equals(contentUri)) {
                return fetcher;
            }
        }
        return null;
    }
}
