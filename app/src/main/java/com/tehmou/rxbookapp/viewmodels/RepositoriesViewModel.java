package com.tehmou.rxbookapp.viewmodels;

import com.tehmou.rxbookapp.data.DataLayer;
import com.tehmou.rxbookapp.data.DataStreamNotification;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;
import com.tehmou.rxbookapp.utils.RxUtils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by ttuo on 19/03/14.
 */
public class RepositoriesViewModel extends AbstractViewModel {
    private static final String TAG = RepositoriesViewModel.class.getSimpleName();

    public enum ProgressStatus {
        LOADING, ERROR, IDLE
    }

    private static final int MAX_REPOSITORIES_DISPLAYED = 5;

    private final DataLayer.GetGitHubRepositorySearch getGitHubRepositorySearch;
    private final DataLayer.GetGitHubRepository getGitHubRepository;

    private final PublishSubject<Observable<String>> searchString = PublishSubject.create();
    private final PublishSubject<GitHubRepository> selectRepository = PublishSubject.create();

    private final BehaviorSubject<List<GitHubRepository>> repositories
            = BehaviorSubject.create();
    private final BehaviorSubject<ProgressStatus> networkRequestStatusText = BehaviorSubject.create();

    public RepositoriesViewModel(DataLayer.GetGitHubRepositorySearch getGitHubRepositorySearch,
                                 DataLayer.GetGitHubRepository getGitHubRepository) {
        this.getGitHubRepositorySearch = getGitHubRepositorySearch;
        this.getGitHubRepository = getGitHubRepository;
        Log.v(TAG, "RepositoriesViewModel");
    }

    @Override
    protected void subscribeToDataStoreInternal(CompositeSubscription compositeSubscription) {
        Log.v(TAG, "subscribeToDataStoreInternal");

        ConnectableObservable<DataStreamNotification<GitHubRepositorySearch>> repositorySearchSource =
                Observable.switchOnNext(searchString)
                          .filter((string) -> string.length() > 2)
                          .throttleLast(500, TimeUnit.MILLISECONDS)
                          .switchMap(getGitHubRepositorySearch::call)
                          .publish();

        compositeSubscription.add(repositorySearchSource
                                          .map(toProgressStatus())
                                          .subscribe(this::setNetworkStatusText));
        compositeSubscription.add(
                repositorySearchSource
                        .filter(DataStreamNotification::isOnNext)
                        .map(DataStreamNotification::getValue)
                        .map(GitHubRepositorySearch::getItems)
                        .flatMap(toGitHubRepositoryList())
                        .doOnNext(list -> Log.d(TAG, "Publishing " + list.size()
                                                              + " repositories from the ViewModel"))
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
    private Observable<GitHubRepository> getGitHubRepositoryObservable(Integer repositoryId) {
        return getGitHubRepository.call(repositoryId)
                                  .doOnNext((repository) -> Log.v(TAG, "Received repository "
                                                                       + repository.getId()));
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

    private void setNetworkStatusText(ProgressStatus status) {
        networkRequestStatusText.onNext(status);
    }

    public Observable<List<GitHubRepository>> getRepositories() {
        return repositories;
    }

    public Observable<ProgressStatus> getNetworkRequestStatusText() {
        return networkRequestStatusText;
    }

    public void setSearchStringObservable(Observable<String> searchStringObservable) {
        this.searchString.onNext(searchStringObservable);
    }

    public void selectRepository(GitHubRepository repository) {
        this.selectRepository.onNext(repository);
    }

    public Observable<GitHubRepository> getSelectRepository() {
        return selectRepository;
    }
}
