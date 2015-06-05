package com.tehmou.rxbookapp.viewmodels;

import com.tehmou.rxbookapp.data.DataLayer;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.UserSettings;

import android.util.Log;

import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ttuo on 06/04/15.
 */
public class RepositoryViewModel extends AbstractViewModel {
    private static final String TAG = RepositoryViewModel.class.getSimpleName();

    private final DataLayer.GetUserSettings getUserSettings;
    private final DataLayer.FetchAndGetGitHubRepository fetchAndGetGitHubRepository;

    final private BehaviorSubject<GitHubRepository> repository = BehaviorSubject.create();

    public RepositoryViewModel(DataLayer.GetUserSettings getUserSettings,
                               DataLayer.FetchAndGetGitHubRepository fetchAndGetGitHubRepository) {
        this.getUserSettings = getUserSettings;
        this.fetchAndGetGitHubRepository = fetchAndGetGitHubRepository;
        Log.v(TAG, "RepositoryViewModel");
    }

    @Override
    protected void subscribeToDataStoreInternal(CompositeSubscription compositeSubscription) {
        compositeSubscription.add(
                getUserSettings.call()
                        .map(UserSettings::getSelectedRepositoryId)
                        .switchMap(fetchAndGetGitHubRepository::call)
                        .subscribe(repository)
        );
    }

    public BehaviorSubject<GitHubRepository> getRepository() {
        return repository;
    }
}
