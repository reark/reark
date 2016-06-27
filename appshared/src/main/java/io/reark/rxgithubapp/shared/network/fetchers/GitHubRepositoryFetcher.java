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

import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.shared.data.StorePutInterface;
import io.reark.rxgithubapp.shared.network.GitHubService;
import io.reark.rxgithubapp.shared.network.NetworkApi;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.view.RepositoryView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class GitHubRepositoryFetcher extends AppFetcherBase {
    private static final String TAG = GitHubRepositoryFetcher.class.getSimpleName();

    @NonNull
    private final StorePutInterface<GitHubRepository, Integer> gitHubRepositoryStore;

    public GitHubRepositoryFetcher(@NonNull NetworkApi networkApi,
                                   @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                                   @NonNull StorePutInterface<GitHubRepository, Integer> gitHubRepositoryStore) {
        super(networkApi, updateNetworkRequestStatus);

        Preconditions.checkNotNull(gitHubRepositoryStore, "GitHub Repository Store cannot be null.");

        this.gitHubRepositoryStore = gitHubRepositoryStore;
    }

    @Override
    public void fetch(@NonNull Intent intent) {
        Preconditions.checkNotNull(intent, "Fetch Intent cannot be null.");

        final int repositoryId = intent.getIntExtra("id", -1);
        if (repositoryId != -1) {
            fetchGitHubRepository(repositoryId);
        } else {
            Log.e(TAG, "No repositoryId provided in the intent extras");
        }
    }

    private void fetchGitHubRepository(final int repositoryId) {
        Log.d(TAG, "fetchGitHubRepository(" + repositoryId + ")");
        if (requestMap.containsKey(repositoryId) &&
                !requestMap.get(repositoryId).isUnsubscribed()) {
            Log.d(TAG, "Found an ongoing request for repository " + repositoryId);
            return;
        }
        final String uri = getUniqueId(repositoryId);
        Subscription subscription = createNetworkObservable(repositoryId)
                .subscribeOn(Schedulers.computation())
                .doOnError(doOnError(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .subscribe(gitHubRepositoryStore::put,
                        e -> Log.e(TAG, "Error fetching GitHub repository " + repositoryId, e));
        requestMap.put(repositoryId, subscription);
        startRequest(uri);
    }

    @NonNull
    private Observable<GitHubRepository> createNetworkObservable(int repositoryId) {
        return networkApi.getRepository(repositoryId);
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
