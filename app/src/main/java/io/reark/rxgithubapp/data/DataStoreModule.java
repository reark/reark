package io.reark.rxgithubapp.data;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reark.reark.network.fetchers.UriFetcherManager;
import io.reark.rxgithubapp.data.stores.GitHubRepositorySearchStore;
import io.reark.rxgithubapp.data.stores.GitHubRepositoryStore;
import io.reark.rxgithubapp.data.stores.NetworkRequestStatusStore;
import io.reark.rxgithubapp.data.stores.StoreModule;
import io.reark.rxgithubapp.data.stores.UserSettingsStore;
import io.reark.rxgithubapp.injections.ForApplication;
import io.reark.rxgithubapp.network.ServiceDataLayer;
import io.reark.rxgithubapp.network.fetchers.FetcherModule;

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
    public ServiceDataLayer provideServiceDataLayer(UriFetcherManager fetcherManager,
                                                    NetworkRequestStatusStore networkRequestStatusStore,
                                                    GitHubRepositoryStore gitHubRepositoryStore,
                                                    GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        return new ServiceDataLayer(fetcherManager,
                                    networkRequestStatusStore,
                                    gitHubRepositoryStore,
                                    gitHubRepositorySearchStore);
    }

}
