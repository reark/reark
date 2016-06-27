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
package io.reark.rxgithubapp.advanced.data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.data.utils.DataLayerUtils;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.advanced.data.stores.GitHubRepositorySearchStore;
import io.reark.rxgithubapp.advanced.data.stores.GitHubRepositoryStore;
import io.reark.rxgithubapp.advanced.data.stores.NetworkRequestStatusStore;
import io.reark.rxgithubapp.advanced.data.stores.UserSettingsStore;
import io.reark.rxgithubapp.advanced.network.NetworkService;
import io.reark.rxgithubapp.shared.network.GitHubService;
import io.reark.rxgithubapp.shared.network.fetchers.GitHubRepositorySearchFetcher;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;
import io.reark.rxgithubapp.shared.pojo.UserSettings;
import rx.Observable;

public class DataLayer extends DataLayerBase {
    private static final String TAG = DataLayer.class.getSimpleName();
    private final Context context;
    protected final UserSettingsStore userSettingsStore;
    public static final int DEFAULT_USER_ID = 0;

    public DataLayer(@NonNull Context context,
                     @NonNull UserSettingsStore userSettingsStore,
                     @NonNull NetworkRequestStatusStore networkRequestStatusStore,
                     @NonNull GitHubRepositoryStore gitHubRepositoryStore,
                     @NonNull GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        super(networkRequestStatusStore, gitHubRepositoryStore, gitHubRepositorySearchStore);

        Preconditions.checkNotNull(context, "Context cannot be null.");
        Preconditions.checkNotNull(userSettingsStore, "User Settings Store cannot be null.");

        this.context = context;
        this.userSettingsStore = userSettingsStore;
    }

    @NonNull
    public Observable<DataStreamNotification<GitHubRepositorySearch>> getGitHubRepositorySearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string Store cannot be null.");

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(
                        GitHubRepositorySearchFetcher.getUniqueId(searchString).hashCode());
        final Observable<GitHubRepositorySearch> gitHubRepositorySearchObservable =
                gitHubRepositorySearchStore.getStream(searchString);
        return DataLayerUtils.createDataStreamNotificationObservable(
                        networkRequestStatusObservable, gitHubRepositorySearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<GitHubRepositorySearch>> fetchAndGetGitHubRepositorySearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string Store cannot be null.");

        final Observable<DataStreamNotification<GitHubRepositorySearch>> gitHubRepositoryStream =
                getGitHubRepositorySearch(searchString);
        fetchGitHubRepositorySearch(searchString);
        return gitHubRepositoryStream;
    }

    private void fetchGitHubRepositorySearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string Store cannot be null.");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", GitHubService.REPOSITORY_SEARCH.toString());
        intent.putExtra("searchString", searchString);
        context.startService(intent);
    }

    @NonNull
    public Observable<GitHubRepository> getGitHubRepository(@NonNull Integer repositoryId) {
        Preconditions.checkNotNull(repositoryId, "Repository Id cannot be null.");

        return gitHubRepositoryStore.getStream(repositoryId);
    }

    @NonNull
    public Observable<GitHubRepository> fetchAndGetGitHubRepository(@NonNull Integer repositoryId) {
        Preconditions.checkNotNull(repositoryId, "Repository Id cannot be null.");

        fetchGitHubRepository(repositoryId);
        return getGitHubRepository(repositoryId);
    }

    private void fetchGitHubRepository(@NonNull Integer repositoryId) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", GitHubService.REPOSITORY.toString());
        intent.putExtra("id", repositoryId);
        context.startService(intent);
    }

    @NonNull
    public Observable<UserSettings> getUserSettings() {
        return userSettingsStore.getStream(DEFAULT_USER_ID);
    }

    public void setUserSettings(@NonNull UserSettings userSettings) {
        Preconditions.checkNotNull(userSettings, "User Settings cannot be null.");

        userSettingsStore.put(userSettings);
    }
}
