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

import io.reark.reark.data.stores.interfaces.StorePutInterface;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.rxgithubapp.shared.network.GitHubService;
import io.reark.rxgithubapp.shared.network.NetworkApi;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static io.reark.reark.utils.Preconditions.checkNotNull;

public class GitHubRepositoryFetcher extends AppFetcherBase<Uri> {
    private static final String TAG = GitHubRepositoryFetcher.class.getSimpleName();

    @NonNull
    private final StorePutInterface<GitHubRepository> gitHubRepositoryStore;

    public GitHubRepositoryFetcher(@NonNull final NetworkApi networkApi,
                                   @NonNull final Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                                   @NonNull final StorePutInterface<GitHubRepository> gitHubRepositoryStore) {
        super(networkApi, updateNetworkRequestStatus);

        checkNotNull(gitHubRepositoryStore);

        this.gitHubRepositoryStore = gitHubRepositoryStore;
    }

    @Override
    public void fetch(@NonNull final Intent intent) {
        checkNotNull(intent);

        final int repositoryId = intent.getIntExtra("id", -1);

        if (repositoryId >= 0) {
            fetchGitHubRepository(repositoryId);
        } else {
            Log.e(TAG, "No repositoryId provided in the intent extras");
        }
    }

    private void fetchGitHubRepository(final int repositoryId) {
        Log.d(TAG, "fetchGitHubRepository(" + repositoryId + ")");

        if (isOngoingRequest(repositoryId)) {
            Log.d(TAG, "Found an ongoing request for repository " + repositoryId);
            return;
        }

        final String uri = getUniqueId(repositoryId);

        Subscription subscription = createNetworkObservable(repositoryId)
                .subscribeOn(Schedulers.computation())
                .doOnSubscribe(() -> startRequest(uri))
                .doOnError(doOnError(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .subscribe(gitHubRepositoryStore::put,
                        e -> Log.e(TAG, "Error fetching GitHub repository " + repositoryId, e));

        addRequest(repositoryId, subscription);
    }

    @NonNull
    private Observable<GitHubRepository> createNetworkObservable(int repositoryId) {
        return getNetworkApi().getRepository(repositoryId);
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return GitHubService.REPOSITORY;
    }

    @NonNull
    public static String getUniqueId(int repositoryId) {
        return GitHubRepository.class + "/" + repositoryId;
    }
}
