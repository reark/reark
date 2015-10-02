package com.tehmou.rxbookapp.network.fetchers;

import com.tehmou.rxbookapp.data.stores.GitHubRepositorySearchStore;
import com.tehmou.rxbookapp.data.stores.GitHubRepositoryStore;
import com.tehmou.rxbookapp.data.stores.NetworkRequestStatusStore;
import com.tehmou.rxbookapp.network.NetworkApi;
import com.tehmou.rxbookapp.network.NetworkModule;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pawel Polanski on 5/16/15.
 */
@Module(includes = NetworkModule.class)
public final class FetcherModule {

    @Provides
    @Named("gitHubRepository")
    public Fetcher provideGitHubRepositoryFetcher(NetworkApi networkApi,
                                                  NetworkRequestStatusStore networkRequestStatusStore,
                                                  GitHubRepositoryStore gitHubRepositoryStore) {
        return new GitHubRepositoryFetcher(networkApi,
                                           networkRequestStatusStore::put,
                                           gitHubRepositoryStore);
    }

    @Provides
    @Named("gitHubRepositorySearch")
    public Fetcher provideGitHubRepositorySearchFetcher(NetworkApi networkApi,
                                                        NetworkRequestStatusStore networkRequestStatusStore,
                                                        GitHubRepositoryStore gitHubRepositoryStore,
                                                        GitHubRepositorySearchStore gitHubRepositorySearchStore) {
        return new GitHubRepositorySearchFetcher(networkApi,
                                                 networkRequestStatusStore::put,
                                                 gitHubRepositoryStore,
                                                 gitHubRepositorySearchStore);
    }

}
