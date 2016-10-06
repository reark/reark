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
package io.reark.rxgithubapp.shared.viewmodels;

import android.support.annotation.NonNull;

import io.reark.reark.viewmodels.AbstractViewModel;
import io.reark.rxgithubapp.shared.data.DataFunctions.FetchAndGetGitHubRepository;
import io.reark.rxgithubapp.shared.data.DataFunctions.GetUserSettings;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.UserSettings;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class RepositoryViewModel extends AbstractViewModel {

    @NonNull
    private final GetUserSettings getUserSettings;

    @NonNull
    private final FetchAndGetGitHubRepository fetchAndGetGitHubRepository;

    @NonNull
    private final BehaviorSubject<GitHubRepository> repository = BehaviorSubject.create();

    public RepositoryViewModel(@NonNull final GetUserSettings getUserSettings,
                               @NonNull final FetchAndGetGitHubRepository fetchAndGetGitHubRepository) {
        this.getUserSettings = get(getUserSettings);
        this.fetchAndGetGitHubRepository = get(fetchAndGetGitHubRepository);
    }

    @Override
    public void subscribeToDataStoreInternal(@NonNull final CompositeSubscription compositeSubscription) {
        checkNotNull(compositeSubscription);

        compositeSubscription.add(
                getUserSettings.call()
                        .map(UserSettings::getSelectedRepositoryId)
                        .switchMap(fetchAndGetGitHubRepository::call)
                        .subscribe(repository)
        );
    }

    @NonNull
    public Observable<GitHubRepository> getRepository() {
        return repository.asObservable();
    }
}
