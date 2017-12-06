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
package io.reark.rxgithubapp.basic.data;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.reark.reark.network.fetchers.Fetcher;
import io.reark.reark.network.fetchers.UriFetcherManager;
import io.reark.reark.utils.Log;
import io.reark.rxgithubapp.basic.data.stores.GitHubRepositorySearchStore;
import io.reark.rxgithubapp.basic.data.stores.GitHubRepositoryStore;
import io.reark.rxgithubapp.basic.data.stores.NetworkRequestStatusStore;
import io.reark.rxgithubapp.basic.data.stores.UserSettingsStore;
import io.reark.rxgithubapp.shared.data.ClientDataLayerBase;
import io.reark.rxgithubapp.shared.network.GitHubService;

import static io.reark.reark.utils.Preconditions.get;

public class DataLayer extends ClientDataLayerBase {
    private static final String TAG = DataLayer.class.getSimpleName();

    private final UriFetcherManager fetcherManager;

    public DataLayer(@NonNull final UriFetcherManager fetcherManager,
                     @NonNull final NetworkRequestStatusStore networkRequestStatusStore,
                     @NonNull final GitHubRepositoryStore gitHubRepositoryStore,
                     @NonNull final GitHubRepositorySearchStore gitHubRepositorySearchStore,
                     @NonNull final UserSettingsStore userSettingsStore) {
        super(networkRequestStatusStore,
                gitHubRepositoryStore,
                gitHubRepositorySearchStore,
                userSettingsStore);

        this.fetcherManager = get(fetcherManager);
    }

    @Override
    protected int fetchGitHubRepository(@NonNull final Integer repositoryId) {
        Log.d(TAG, "fetchGitHubRepository(" + get(repositoryId) + ")");

        int listenerId = createListenerId();

        Intent intent = new Intent();
        intent.putExtra("serviceUriString", GitHubService.REPOSITORY.toString());
        intent.putExtra("repositoryId", repositoryId);

        Fetcher<Uri> fetcher = fetcherManager.findFetcher(GitHubService.REPOSITORY);

        if (fetcher != null) {
            fetcher.fetch(intent, listenerId);
        }

        return listenerId;
    }

    @Override
    protected int fetchGitHubRepositorySearch(@NonNull final String searchString) {
        Log.d(TAG, "fetchGitHubRepositorySearch(" + get(searchString) + ")");

        int listenerId = createListenerId();

        Intent intent = new Intent();
        intent.putExtra("serviceUriString", GitHubService.REPOSITORY_SEARCH.toString());
        intent.putExtra("searchString", searchString);

        Fetcher<Uri> fetcher = fetcherManager.findFetcher(GitHubService.REPOSITORY_SEARCH);

        if (fetcher != null) {
            fetcher.fetch(intent, listenerId);
        }

        return listenerId;
    }
}
