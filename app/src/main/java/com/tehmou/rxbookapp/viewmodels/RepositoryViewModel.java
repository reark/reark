package com.tehmou.rxbookapp.viewmodels;

import android.util.Log;

import com.tehmou.rxbookapp.RxBookApp;
import com.tehmou.rxbookapp.data.DataLayer;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

import javax.inject.Inject;

import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ttuo on 06/04/15.
 */
public class RepositoryViewModel extends AbstractViewModel {
    private static final String TAG = RepositoryViewModel.class.getSimpleName();

    final private BehaviorSubject<Integer> repositoryId = BehaviorSubject.create();

    final private BehaviorSubject<GitHubRepository> repository = BehaviorSubject.create();

    public RepositoryViewModel() {
        RxBookApp.getInstance().getGraph().inject(this);
        Log.v(TAG, "RepositoryViewModel");
    }

    @Override
    protected void subscribeToDataStoreInternal(CompositeSubscription compositeSubscription) {
        compositeSubscription.add(
                repositoryId
                        .switchMap(dataLayer::fetchAndGetGitHubRepository)
                        .subscribe(repository)
        );
    }

    public BehaviorSubject<GitHubRepository> getRepository() {
        return repository;
    }

    public void setRepositoryId(int repositoryId) {
        this.repositoryId.onNext(repositoryId);
    }
}
