package com.tehmou.rxbookapp.viewmodels;

import com.tehmou.rxbookapp.RxBookApp;
import android.util.Log;

import com.tehmou.rxbookapp.data.DataLayer;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;
import com.tehmou.rxbookapp.view.RepositoriesView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.FuncN;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by ttuo on 19/03/14.
 */
public class RepositoriesViewModel {
    private static final String TAG = RepositoriesViewModel.class.getSimpleName();
    final private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    DataLayer dataLayer;

    private PublishSubject<Observable<String>> searchString = PublishSubject.create();

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

        compositeSubscription.add(
                Observable.switchOnNext(
                        Observable.switchOnNext(searchString)
                                .filter((string) -> string.length() > 2)
                                .throttleLast(500, TimeUnit.MILLISECONDS)
                                .map(dataLayer::getGitHubRepositorySearch)
                )
                .flatMap((repositorySearch) -> {
                    final List<Observable<GitHubRepository>> observables = new ArrayList<>();
                    for (int repositoryId : repositorySearch.getItems()) {
                        Log.d(TAG, "Process repositoryId: " + repositoryId);
                        final Observable<GitHubRepository> observable =
                                dataLayer.getGitHubRepository(repositoryId);
                        observables.add(observable);
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
    }

    public void setSearchStringObservable(Observable<String> searchStringObservable) {
        this.searchString.onNext(searchStringObservable);
    }
}
