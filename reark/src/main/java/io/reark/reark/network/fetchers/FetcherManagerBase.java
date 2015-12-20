package io.reark.reark.network.fetchers;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;

import io.reark.reark.utils.Preconditions;

/**
 * Created by ttuo on 13/12/15.
 */
public abstract class FetcherManagerBase<T> {
    @NonNull
    final private Collection<Fetcher<T>> fetchers;

    public FetcherManagerBase(@NonNull Collection<Fetcher<T>> fetchers) {
        Preconditions.checkNotNull(fetchers, "fetchers cannot be null.");

        this.fetchers = fetchers;
    }

    @Nullable
    public Fetcher<T> findFetcher(@NonNull T serviceUri) {
        Preconditions.checkNotNull(serviceUri, "Service URI cannot be null.");

        for (Fetcher<T> fetcher : fetchers) {
            if (fetcher.getServiceUri().equals(serviceUri)) {
                return fetcher;
            }
        }
        return null;
    }
}
