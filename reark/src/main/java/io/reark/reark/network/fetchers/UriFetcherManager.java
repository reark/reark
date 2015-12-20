package io.reark.reark.network.fetchers;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by ttuo on 13/12/15.
 */
public class UriFetcherManager extends FetcherManagerBase<Uri> {
    private UriFetcherManager(Collection<Fetcher<Uri>> fetchers) {
        super(fetchers);
    }

    public static class Builder {
        private Collection<Fetcher<Uri>> fetchers = new ArrayList<>();

        public Builder() {

        }

        public Builder fetchers(@NonNull Collection<Fetcher<Uri>> fetchers) {
            this.fetchers = fetchers;
            return this;
        }

        public UriFetcherManager build() {
            return new UriFetcherManager(fetchers);
        }
    }
}
