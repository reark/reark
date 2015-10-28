package io.reark.rxbookapp.data;

import android.content.Context;

import io.reark.reark.network.fetchers.Fetcher;
import io.reark.rxbookapp.data.stores.GitHubRepositorySearchStore;
import io.reark.rxbookapp.data.stores.GitHubRepositoryStore;
import io.reark.rxbookapp.data.stores.NetworkRequestStatusStore;
import io.reark.rxbookapp.data.stores.StoreModule;
import io.reark.rxbookapp.data.stores.UserSettingsStore;
import io.reark.rxbookapp.injections.ForApplication;
import io.reark.rxbookapp.network.ServiceDataLayer;
import io.reark.rxbookapp.network.fetchers.FetcherModule;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pt2121 on 2/20/15.
 */
@Module(includes = { FetcherModule.class, StoreModule.class })
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
    public ServiceDataLayer provideServiceDataLayer(@Named("gitHubRepository")Fetcher gitHubRepositoryFetcher,
                                                    @Named("gitHubRepositorySearch") Fetcher gitHubRepositorySearchFetcher,
                                                    NetworkRequestStatusStore networkRequestStatusStore,
                                                    GitHubRepositoryStore gitHubRepositoryStore,
                                                    GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        return new ServiceDataLayer(gitHubRepositoryFetcher,
                                    gitHubRepositorySearchFetcher,
                                    networkRequestStatusStore,
                                    gitHubRepositoryStore,
                                    gitHubRepositorySearchStore);
    }

}
