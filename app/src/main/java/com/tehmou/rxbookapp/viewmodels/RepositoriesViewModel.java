package com.tehmou.rxbookapp.viewmodels;

import com.tehmou.rxbookapp.RxBookApp;
import android.util.Log;

import com.tehmou.rxbookapp.data.DataLayer;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.FuncN;
import rx.subjects.BehaviorSubject;
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

    final private String search;

    final private Subject<List<GitHubRepository>, List<GitHubRepository>> repositories
            = BehaviorSubject.create();

    public Observable<List<GitHubRepository>> getRepositories() {
        return repositories;
    }

    public RepositoriesViewModel(final String search) {
        RxBookApp.getInstance().getGraph().inject(this);
        this.search = search;
    }

    public void subscribeToDataStore() {
        compositeSubscription.add(
                dataLayer.getGitHubRepositorySearch(search)
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
                       .subscribe(repositories));
    }

    public void unsubscribeFromDataStore() {
        compositeSubscription.clear();
    }
}
