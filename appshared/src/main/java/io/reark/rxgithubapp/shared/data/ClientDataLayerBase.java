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
package io.reark.rxgithubapp.shared.data;

import android.support.annotation.NonNull;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.data.stores.StoreInterface;
import io.reark.reark.data.utils.DataLayerUtils;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.shared.network.fetchers.GitHubRepositorySearchFetcher;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;
import io.reark.rxgithubapp.shared.pojo.UserSettings;
import rx.Observable;

public abstract class ClientDataLayerBase extends DataLayerBase {
    private static final String TAG = ClientDataLayerBase.class.getSimpleName();
    public static final int DEFAULT_USER_ID = 0;

    protected final StoreInterface<Integer, UserSettings> userSettingsStore;

    public ClientDataLayerBase(@NonNull StoreInterface<Integer, NetworkRequestStatus> networkRequestStatusStore,
                               @NonNull StoreInterface<Integer, GitHubRepository> gitHubRepositoryStore,
                               @NonNull StoreInterface<String, GitHubRepositorySearch> gitHubRepositorySearchStore,
                               @NonNull StoreInterface<Integer, UserSettings> userSettingsStore) {
        super(networkRequestStatusStore, gitHubRepositoryStore, gitHubRepositorySearchStore);
        Preconditions.checkNotNull(userSettingsStore,
                "User Settings Store cannot be null.");

        this.userSettingsStore = userSettingsStore;
    }

    @NonNull
    public Observable<DataStreamNotification<GitHubRepositorySearch>> getGitHubRepositorySearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string Store cannot be null.");

        Log.d(TAG, "getGitHubRepositorySearch(" + searchString + ")");
        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getOnceAndStream(
                        GitHubRepositorySearchFetcher.getUniqueId(searchString).hashCode());
        final Observable<GitHubRepositorySearch> gitHubRepositorySearchObservable =
                gitHubRepositorySearchStore.getOnceAndStream(searchString)
                        .filter(value -> value != null);
        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, gitHubRepositorySearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<GitHubRepositorySearch>> fetchAndGetGitHubRepositorySearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string Store cannot be null.");

        Log.d(TAG, "fetchAndGetGitHubRepositorySearch(" + searchString + ")");
        fetchGitHubRepositorySearch(searchString);
        final Observable<DataStreamNotification<GitHubRepositorySearch>> gitHubRepositoryStream =
                getGitHubRepositorySearch(searchString);
        return gitHubRepositoryStream;
    }

    protected abstract void fetchGitHubRepositorySearch(@NonNull String searchString);

    @NonNull
    public Observable<GitHubRepository> getGitHubRepository(@NonNull Integer repositoryId) {
        Preconditions.checkNotNull(repositoryId, "Repository Id cannot be null.");

        return gitHubRepositoryStore.getOnceAndStream(repositoryId)
                .filter(value -> value != null);
    }

    @NonNull
    public Observable<GitHubRepository> fetchAndGetGitHubRepository(@NonNull Integer repositoryId) {
        Preconditions.checkNotNull(repositoryId, "Repository Id cannot be null.");

        fetchGitHubRepository(repositoryId);
        return getGitHubRepository(repositoryId);
    }

    protected abstract void fetchGitHubRepository(@NonNull Integer repositoryId);

    @NonNull
    public Observable<UserSettings> getUserSettings() {
        return userSettingsStore.getOnceAndStream(DEFAULT_USER_ID)
                .filter(value -> value != null);
    }

    public void setUserSettings(@NonNull UserSettings userSettings) {
        Preconditions.checkNotNull(userSettings, "User Settings cannot be null.");

        userSettingsStore.put(userSettings);
    }
}
