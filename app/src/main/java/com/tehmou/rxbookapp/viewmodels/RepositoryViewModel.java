package com.tehmou.rxbookapp.viewmodels;

import com.tehmou.rxandroidarchitecture.viewmodels.AbstractViewModel;
import com.tehmou.rxbookapp.data.DataLayer;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.UserSettings;

import android.support.annotation.NonNull;
import android.util.Log;

import rx.Observable;
import rx.android.internal.Preconditions;
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

    public RepositoryViewModel(@NonNull DataLayer.GetUserSettings getUserSettings,
                               @NonNull DataLayer.FetchAndGetGitHubRepository fetchAndGetGitHubRepository) {
        Preconditions.checkNotNull(getUserSettings, "Gey User Settings cannot be null.");
        Preconditions.checkNotNull(fetchAndGetGitHubRepository,
                                   "Fetch And Get GitHub Repository cannot be null.");

        this.getUserSettings = getUserSettings;
        this.fetchAndGetGitHubRepository = fetchAndGetGitHubRepository;
        Log.v(TAG, "RepositoryViewModel");
    }

    @Override
    public void subscribeToDataStoreInternal(@NonNull CompositeSubscription compositeSubscription) {
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
