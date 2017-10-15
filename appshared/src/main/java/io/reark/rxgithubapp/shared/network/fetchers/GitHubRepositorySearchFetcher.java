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
package io.reark.rxgithubapp.shared.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import io.reark.reark.data.stores.interfaces.StorePutInterface;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.rxgithubapp.shared.network.GitHubService;
import io.reark.rxgithubapp.shared.network.NetworkApi;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class GitHubRepositorySearchFetcher extends AppFetcherBase<Uri> {
    private static final String TAG = GitHubRepositorySearchFetcher.class.getSimpleName();

    @NonNull
    private final StorePutInterface<GitHubRepository> gitHubRepositoryStore;

    @NonNull
    private final StorePutInterface<GitHubRepositorySearch> gitHubRepositorySearchStore;

    public GitHubRepositorySearchFetcher(@NonNull final NetworkApi networkApi,
                                         @NonNull final Consumer<NetworkRequestStatus> updateNetworkRequestStatus,
                                         @NonNull final StorePutInterface<GitHubRepository> gitHubRepositoryStore,
                                         @NonNull final StorePutInterface<GitHubRepositorySearch> gitHubRepositorySearchStore) {
        super(networkApi, updateNetworkRequestStatus);

        this.gitHubRepositoryStore = get(gitHubRepositoryStore);
        this.gitHubRepositorySearchStore = get(gitHubRepositorySearchStore);
    }

    @Override
    public synchronized void fetch(@NonNull final Intent intent, int listenerId) {
        checkNotNull(intent);

        if (!intent.hasExtra("searchString")) {
            Log.e(TAG, "Missing required fetch parameters!");
            return;
        }

        String searchString = intent.getStringExtra("searchString");
        String uri = getUniqueId(searchString);
        int requestId = searchString.hashCode();

        addListener(requestId, listenerId);

        if (isOngoingRequest(requestId)) {
            Log.d(TAG, "Found an ongoing request for repository " + searchString);
            return;
        }

        Log.d(TAG, "fetch(" + searchString + ")");

        Disposable disposable = createNetworkObservable(searchString)
                .subscribeOn(Schedulers.computation())
                .flatMapObservable(Observable::fromIterable)
                .doOnNext(gitHubRepositoryStore::put)
                .map(GitHubRepository::getId)
                .toList()
                .map(idList -> new GitHubRepositorySearch(searchString, idList))
                .flatMap(gitHubRepositorySearchStore::put)
                .doOnSubscribe(__ -> startRequest(requestId, uri))
                .doOnSuccess(updated -> completeRequest(requestId, uri, updated))
                .doOnError(doOnError(requestId, uri))
                .subscribe(Functions.emptyConsumer(),
                        Log.onError(TAG, "Error fetching GitHub repository search for '" + searchString + "'"));

        addRequest(requestId, disposable);
    }

    @NonNull
    private Single<List<GitHubRepository>> createNetworkObservable(@NonNull final String searchString) {
        return getNetworkApi().search(Collections.singletonMap("q", searchString));
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return GitHubService.REPOSITORY_SEARCH;
    }

    @NonNull
    public static String getUniqueId(String search) {
        return GitHubRepository.class + "/" + search;
    }
}
