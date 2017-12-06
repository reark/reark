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

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.RxUtils;
import io.reark.reark.viewmodels.AbstractViewModel;
import io.reark.rxgithubapp.shared.data.DataFunctions.GetGitHubRepository;
import io.reark.rxgithubapp.shared.data.DataFunctions.GetGitHubRepositorySearch;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class RepositoriesViewModel extends AbstractViewModel {
    private static final String TAG = RepositoriesViewModel.class.getSimpleName();

    public enum ProgressStatus {
        LOADING, ERROR, IDLE
    }

    private static final int MAX_REPOSITORIES_DISPLAYED = 5;
    private static final int SEARCH_INPUT_DELAY = 500;

    @NonNull
    private final GetGitHubRepositorySearch getGitHubRepositorySearch;

    @NonNull
    private final GetGitHubRepository getGitHubRepository;

    @NonNull
    private final PublishSubject<String> searchString = PublishSubject.create();

    @NonNull
    private final PublishSubject<GitHubRepository> selectRepository = PublishSubject.create();

    @NonNull
    private final BehaviorSubject<List<GitHubRepository>> repositories = BehaviorSubject.create();

    @NonNull
    private final BehaviorSubject<ProgressStatus> networkRequestStatusText = BehaviorSubject.create();

    public RepositoriesViewModel(@NonNull final GetGitHubRepositorySearch getGitHubRepositorySearch,
                                 @NonNull final GetGitHubRepository getGitHubRepository) {
        this.getGitHubRepositorySearch = get(getGitHubRepositorySearch);
        this.getGitHubRepository = get(getGitHubRepository);
    }

    @NonNull
    public Observable<GitHubRepository> getSelectRepository() {
        return selectRepository.hide();
    }

    @NonNull
    public Observable<List<GitHubRepository>> getRepositories() {
        return repositories.hide();
    }

    @NonNull
    public Observable<ProgressStatus> getNetworkRequestStatusText() {
        return networkRequestStatusText.hide();
    }

    public void setSearchString(@NonNull final String searchString) {
        checkNotNull(searchString);

        this.searchString.onNext(searchString);
    }

    public void selectRepository(@NonNull final GitHubRepository repository) {
        checkNotNull(repository);

        this.selectRepository.onNext(repository);
    }

    @NonNull
    static Function<DataStreamNotification<GitHubRepositorySearch>, ProgressStatus> toProgressStatus() {
        return notification -> {
            if (notification.isOngoing()) {
                return ProgressStatus.LOADING;
            } else if (notification.isCompletedWithError()) {
                return ProgressStatus.ERROR;
            } else {
                return ProgressStatus.IDLE;
            }
        };
    }

    @Override
    public void subscribeToDataStoreInternal(@NonNull final CompositeDisposable compositeDisposable) {
        checkNotNull(compositeDisposable);
        Log.v(TAG, "subscribeToDataStoreInternal");

        ConnectableObservable<DataStreamNotification<GitHubRepositorySearch>> repositorySearchSource =
                searchString
                        .debounce(SEARCH_INPUT_DELAY, TimeUnit.MILLISECONDS)
                        .distinctUntilChanged()
                        .filter(value -> value.length() > 2)
                        .doOnNext(value -> Log.d(TAG, "Searching with: " + value))
                        .switchMap(getGitHubRepositorySearch::call)
                        .publish();

        compositeDisposable.add(repositorySearchSource
                .map(toProgressStatus())
                .doOnNext(progressStatus -> Log.d(TAG, "Progress status: " + progressStatus.name()))
                .subscribe(this::setNetworkStatusText));

        compositeDisposable.add(repositorySearchSource
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .map(GitHubRepositorySearch::getItems)
                .flatMap(toGitHubRepositoryList())
                .doOnNext(list -> Log.d(TAG, "Publishing " + list.size() + " repositories from the ViewModel"))
                .subscribe(repositories::onNext));

        compositeDisposable.add(repositorySearchSource.connect());
    }

    @NonNull
    Function<List<Integer>, Observable<List<GitHubRepository>>> toGitHubRepositoryList() {
        return repositoryIds -> Observable.fromIterable(repositoryIds)
                .take(MAX_REPOSITORIES_DISPLAYED)
                .map(this::getGitHubRepositoryObservable)
                .toList()
                .toObservable()
                .flatMap(RxUtils::toObservableList);
    }

    @NonNull
    Observable<GitHubRepository> getGitHubRepositoryObservable(@NonNull final Integer repositoryId) {
        checkNotNull(repositoryId);

        return getGitHubRepository
                .call(repositoryId)
                .doOnNext((repository) -> Log.v(TAG, "Received repository " + repository.getId()));
    }

    void setNetworkStatusText(@NonNull final ProgressStatus status) {
        checkNotNull(status);

        networkRequestStatusText.onNext(status);
    }
}
