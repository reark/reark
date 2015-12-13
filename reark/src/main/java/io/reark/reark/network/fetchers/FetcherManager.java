package io.reark.reark.network.fetchers;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;

import io.reark.reark.utils.Preconditions;

/**
 * Created by ttuo on 13/12/15.
 */
public class FetcherManager {
    @NonNull
    final private Collection<Fetcher> fetchers;

    private FetcherManager(Collection<Fetcher> fetchers) {
        this.fetchers = fetchers;
    }

    @Nullable
    public Fetcher findFetcher(@NonNull Uri serviceUri) {
        Preconditions.checkNotNull(serviceUri, "Service URI cannot be null.");

        for (Fetcher fetcher : fetchers) {
            if (fetcher.getServiceUri().equals(serviceUri)) {
                return fetcher;
            }
        }
        return null;
    }

    public static class Builder {
        private Collection<Fetcher> fetchers;

        public Builder() {

        }

        public Builder fetchers(@NonNull Collection<Fetcher> fetchers) {
            this.fetchers = fetchers;
            return this;
        }

        public FetcherManager build() {
            return new FetcherManager(fetchers);
        }
    }
}
