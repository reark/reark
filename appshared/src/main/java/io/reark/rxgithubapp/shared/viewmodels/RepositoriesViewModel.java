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

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import io.reark.reark.utils.RxUtils;
import io.reark.reark.viewmodels.AbstractViewModel;
import io.reark.rxgithubapp.shared.data.DataFunctions;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class RepositoriesViewModel extends AbstractViewModel {
    private static final String TAG = RepositoriesViewModel.class.getSimpleName();

    public enum ProgressStatus {
        LOADING, ERROR, IDLE
    }

    private static final int MAX_REPOSITORIES_DISPLAYED = 5;
    private static final int SEARCH_INPUT_DELAY = 500;

    @NonNull
    private final DataFunctions.GetGitHubRepositorySearch getGitHubRepositorySearch;

    @NonNull
    private final DataFunctions.GetGitHubRepository getGitHubRepository;

    private final PublishSubject<String> searchString = PublishSubject.create();
    private final PublishSubject<GitHubRepository> selectRepository = PublishSubject.create();

    private final BehaviorSubject<List<GitHubRepository>> repositories = BehaviorSubject.create();
    private final BehaviorSubject<ProgressStatus> networkRequestStatusText = BehaviorSubject.create();

    public RepositoriesViewModel(@NonNull DataFunctions.GetGitHubRepositorySearch getGitHubRepositorySearch,
                                 @NonNull DataFunctions.GetGitHubRepository getGitHubRepository) {
        Preconditions.checkNotNull(getGitHubRepositorySearch,
                                   "GetGitHubRepositorySearch cannot be null.");
        Preconditions.checkNotNull(getGitHubRepository,
                                   "GetGitHubRepository cannot be null.");

        this.getGitHubRepositorySearch = getGitHubRepositorySearch;
        this.getGitHubRepository = getGitHubRepository;
        Log.v(TAG, "RepositoriesViewModel");
    }

    @NonNull
    public Observable<GitHubRepository> getSelectRepository() {
        return selectRepository.asObservable();
    }

    @NonNull
    public Observable<List<GitHubRepository>> getRepositories() {
        return repositories.asObservable();
    }

    @NonNull
    public Observable<ProgressStatus> getNetworkRequestStatusText() {
        return networkRequestStatusText.asObservable();
    }

    public void setSearchString(@NonNull String searchString) {
        Preconditions.checkNotNull(searchString, "SearchString cannot be null.");

        this.searchString.onNext(searchString);
    }

    public void selectRepository(@NonNull GitHubRepository repository) {
        Preconditions.checkNotNull(repository, "Repository cannot be null.");

        this.selectRepository.onNext(repository);
    }

    @NonNull
    static Func1<DataStreamNotification<GitHubRepositorySearch>, ProgressStatus> toProgressStatus() {
        return notification -> {
            if (notification.isFetchingStart()) {
                return ProgressStatus.LOADING;
            } else if (notification.isFetchingError()) {
                return ProgressStatus.ERROR;
            } else {
                return ProgressStatus.IDLE;
            }
        };
    }

    @Override
    public void subscribeToDataStoreInternal(@NonNull CompositeSubscription compositeSubscription) {
        Log.v(TAG, "subscribeToDataStoreInternal");

        ConnectableObservable<DataStreamNotification<GitHubRepositorySearch>> repositorySearchSource =
                searchString
                        .filter((string) -> string.length() > 2)
                        .debounce(SEARCH_INPUT_DELAY, TimeUnit.MILLISECONDS)
                        .distinctUntilChanged()
                        .switchMap(getGitHubRepositorySearch::call)
                        .publish();

        compositeSubscription.add(repositorySearchSource
                .map(toProgressStatus())
                .subscribe(this::setNetworkStatusText));

        compositeSubscription.add(repositorySearchSource
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .map(GitHubRepositorySearch::getItems)
                .flatMap(toGitHubRepositoryList())
                .doOnNext(list -> Log.d(TAG, "Publishing " + list.size() + " repositories from the ViewModel"))
                .subscribe(RepositoriesViewModel.this.repositories::onNext));

        compositeSubscription.add(repositorySearchSource.connect());
    }

    @NonNull
    Func1<List<Integer>, Observable<List<GitHubRepository>>> toGitHubRepositoryList() {
        return repositoryIds -> Observable.from(repositoryIds)
                .take(MAX_REPOSITORIES_DISPLAYED)
                .map(this::getGitHubRepositoryObservable)
                .toList()
                .flatMap(RxUtils::toObservableList);
    }

    @NonNull
    Observable<GitHubRepository> getGitHubRepositoryObservable(@NonNull Integer repositoryId) {
        Preconditions.checkNotNull(repositoryId, "Repository Id cannot be null.");

        return getGitHubRepository
                .call(repositoryId)
                .doOnNext((repository) -> Log.v(TAG, "Received repository " + repository.getId()));
    }

    void setNetworkStatusText(@NonNull ProgressStatus status) {
        Preconditions.checkNotNull(status, "ProgressStatus cannot be null.");

        networkRequestStatusText.onNext(status);
    }
}
