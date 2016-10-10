/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.rxgithubapp.advanced.network;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.reark.reark.network.fetchers.Fetcher;
import io.reark.reark.network.fetchers.UriFetcherManager;
import io.reark.reark.utils.Log;
import io.reark.rxgithubapp.advanced.data.stores.GitHubRepositorySearchStore;
import io.reark.rxgithubapp.advanced.data.stores.GitHubRepositoryStore;
import io.reark.rxgithubapp.advanced.data.stores.NetworkRequestStatusStore;
import io.reark.rxgithubapp.shared.data.DataLayerBase;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class ServiceDataLayer extends DataLayerBase {
    private static final String TAG = ServiceDataLayer.class.getSimpleName();

    @NonNull
    private final UriFetcherManager fetcherManager;

    public ServiceDataLayer(@NonNull final UriFetcherManager fetcherManager,
                            @NonNull final NetworkRequestStatusStore networkRequestStatusStore,
                            @NonNull final GitHubRepositoryStore gitHubRepositoryStore,
                            @NonNull final GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        super(networkRequestStatusStore, gitHubRepositoryStore, gitHubRepositorySearchStore);

        this.fetcherManager = get(fetcherManager);
    }

    public void processIntent(@NonNull final Intent intent) {
        checkNotNull(intent);

        final String serviceUriString = intent.getStringExtra("serviceUriString");

        if (serviceUriString == null) {
            Log.e(TAG, "No Uri defined");
            return;
        }

        final Uri serviceUri = Uri.parse(serviceUriString);
        final Fetcher<Uri> matchingFetcher = fetcherManager.findFetcher(serviceUri);

        if (matchingFetcher != null) {
            Log.v(TAG, "Fetcher found for " + serviceUri);
            matchingFetcher.fetch(intent);
        } else {
            Log.e(TAG, "Unknown Uri " + serviceUri);
        }
    }
}
