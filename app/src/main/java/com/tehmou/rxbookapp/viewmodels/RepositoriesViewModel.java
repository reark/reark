package com.tehmou.rxbookapp.viewmodels;

import com.tehmou.rxbookapp.RxBookApp;
import com.tehmou.rxbookapp.data.DataLayer;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by ttuo on 19/03/14.
 */
public class RepositoriesViewModel {
    private static final String TAG = RepositoriesViewModel.class.getSimpleName();

    private static final int MAX_REPOSITORIES_DISPLAYED = 5;

    private CompositeSubscription compositeSubscription;

    @Inject
    DataLayer dataLayer;

    private final PublishSubject<Observable<String>> searchString = PublishSubject.create();

    final private Subject<List<GitHubRepository>, List<GitHubRepository>> repositories
            = BehaviorSubject.create();

    public Observable<List<GitHubRepository>> getRepositories() {
        return repositories;
    }

    public RepositoriesViewModel() {
        RxBookApp.getInstance().getGraph().inject(this);
        Log.v(TAG, "RepositoriesViewModel");
    }

    public void subscribeToDataStore() {
        Log.v(TAG, "subscribeToDataStore");

        if (compositeSubscription == null) {
            compositeSubscription = new CompositeSubscription();
        }

        compositeSubscription.add(
                Observable.switchOnNext(
                        Observable.switchOnNext(searchString)
                                .filter((string) -> string.length() > 2)
                                .throttleLast(500, TimeUnit.MILLISECONDS)
                                .map(dataLayer::getGitHubRepositorySearch)
                )
                .flatMap((repositorySearch) -> {
                    Log.d(TAG, "Found " + repositorySearch.getItems().size() +
                            " repositories with search " + repositorySearch.getSearch());
                    final List<Observable<GitHubRepository>> observables = new ArrayList<>();
                    for (int repositoryId : repositorySearch.getItems()) {
                        Log.v(TAG, "Process repositoryId: " + repositoryId);
                        final Observable<GitHubRepository> observable =
                                dataLayer.getGitHubRepository(repositoryId)
                                        .doOnNext((repository) ->
                                                Log.v(TAG, "Received repository " + repository.getId()));
                        observables.add(observable);
                        if (observables.size() >= MAX_REPOSITORIES_DISPLAYED) {
                            break;
                        }
                    }
                    return Observable.combineLatest(
                            observables,
                            (args) -> {
                                Log.v(TAG, "Combine items into a list");
                                final List<GitHubRepository> list = new ArrayList<>();
                                for (Object repository : args) {
                                    list.add((GitHubRepository) repository);
                                }
                                return list;
                            }
                    );
                })
                .subscribe((repositories) -> {
                    Log.d(TAG, "Publishing " + repositories.size() + " repositories from the ViewModel");
                    RepositoriesViewModel.this.repositories.onNext(repositories);
                }));
    }

    public void unsubscribeFromDataStore() {
        Log.v(TAG, "unsubscribeToDataStore");
        compositeSubscription.clear();
        compositeSubscription = null;
    }

    public void setSearchStringObservable(Observable<String> searchStringObservable) {
        this.searchString.onNext(searchStringObservable);
    }
}
