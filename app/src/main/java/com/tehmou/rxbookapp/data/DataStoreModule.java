package com.tehmou.rxbookapp.data;

import com.tehmou.rxbookapp.data.stores.GitHubRepositorySearchStore;
import com.tehmou.rxbookapp.data.stores.GitHubRepositoryStore;
import com.tehmou.rxbookapp.data.stores.NetworkRequestStatusStore;
import com.tehmou.rxbookapp.data.stores.UserSettingsStore;
import com.tehmou.rxbookapp.injections.ForApplication;
import com.tehmou.rxbookapp.network.NetworkApi;
import com.tehmou.rxbookapp.network.ServiceDataLayer;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pt2121 on 2/20/15.
 */
@Module
public final class DataStoreModule {

    @Provides
    public DataLayer.GetUserSettings provideGetUserSettings(DataLayer dataLayer) {
        return dataLayer::getUserSettings;
    }

    @Provides
    public DataLayer.SetUserSettings provideSetUserSettings(DataLayer dataLayer) {
        return dataLayer::setUserSettings;
    }

    @Provides
    public DataLayer.FetchAndGetGitHubRepository provideFetchAndGetGitHubRepository(DataLayer dataLayer) {
        return dataLayer::fetchAndGetGitHubRepository;
    }

    @Provides
    public DataLayer.GetGitHubRepositorySearch provideGetGitHubRepositorySearch(DataLayer dataLayer) {
        return dataLayer::fetchAndGetGitHubRepositorySearch;
    }

    @Provides
    public DataLayer.GetGitHubRepository provideGetGitHubRepository(DataLayer dataLayer) {
        return dataLayer::getGitHubRepository;
    }

    @Provides
    @Singleton
    public DataLayer provideApplicationDataLayer(@ForApplication Context context,
                                                 UserSettingsStore userSettingsStore,
                                                 NetworkRequestStatusStore networkRequestStatusStore,
                                                 GitHubRepositoryStore gitHubRepositoryStore,
                                                 GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        return new DataLayer(context, userSettingsStore, networkRequestStatusStore, gitHubRepositoryStore, gitHubRepositorySearchStore);
    }

    @Provides
    @Singleton
    public ServiceDataLayer provideServiceDataLayer(NetworkApi networkApi,
                                                    NetworkRequestStatusStore networkRequestStatusStore,
                                                    GitHubRepositoryStore gitHubRepositoryStore,
                                                    GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        return new ServiceDataLayer(networkApi, networkRequestStatusStore, gitHubRepositoryStore, gitHubRepositorySearchStore);
    }

}
