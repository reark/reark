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
import rx.android.internal.Preconditions;
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

    @NonNull
    private final DataLayer.GetGitHubRepositorySearch getGitHubRepositorySearch;

    @NonNull
    private final DataLayer.GetGitHubRepository getGitHubRepository;

    private final PublishSubject<Observable<String>> searchString = PublishSubject.create();
    private final PublishSubject<GitHubRepository> selectRepository = PublishSubject.create();

    private final BehaviorSubject<List<GitHubRepository>> repositories = BehaviorSubject.create();
    private final BehaviorSubject<ProgressStatus> networkRequestStatusText = BehaviorSubject.create();

    public RepositoriesViewModel(@NonNull DataLayer.GetGitHubRepositorySearch getGitHubRepositorySearch,
                                 @NonNull DataLayer.GetGitHubRepository getGitHubRepository) {
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

    public void setSearchStringObservable(@NonNull Observable<String> searchStringObservable) {
        Preconditions.checkNotNull(searchStringObservable, "Search Observable cannot be null.");

        this.searchString.onNext(searchStringObservable);
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
    protected void subscribeToDataStoreInternal(@NonNull CompositeSubscription compositeSubscription) {
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
    Observable<GitHubRepository> getGitHubRepositoryObservable(@NonNull Integer repositoryId) {
        Preconditions.checkNotNull(repositoryId, "Repository Id cannot be null.");

        return getGitHubRepository.call(repositoryId)
                                  .doOnNext((repository) -> Log.v(TAG, "Received repository "
                                                                       + repository.getId()));
    }

    void setNetworkStatusText(@NonNull ProgressStatus status) {
        Preconditions.checkNotNull(status, "ProgressStatus cannot be null.");

        networkRequestStatusText.onNext(status);
    }

}
