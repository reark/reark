/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.rxgithubapp.basic.network;

import java.util.Arrays;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reark.reark.network.fetchers.Fetcher;
import io.reark.reark.network.fetchers.UriFetcherManager;
import io.reark.rxgithubapp.basic.data.stores.GitHubRepositorySearchStore;
import io.reark.rxgithubapp.basic.data.stores.GitHubRepositoryStore;
import io.reark.rxgithubapp.basic.data.stores.NetworkRequestStatusStore;
import io.reark.rxgithubapp.shared.network.NetworkApi;
import io.reark.rxgithubapp.shared.network.NetworkModule;
import io.reark.rxgithubapp.shared.network.fetchers.GitHubRepositoryFetcher;
import io.reark.rxgithubapp.shared.network.fetchers.GitHubRepositorySearchFetcher;

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

    @Provides
    public UriFetcherManager provideUriFetcherManager(@Named("gitHubRepository")Fetcher gitHubRepositoryFetcher,
                                                      @Named("gitHubRepositorySearch") Fetcher gitHubRepositorySearchFetcher) {
        return new UriFetcherManager.Builder()
                .fetchers(Arrays.asList(gitHubRepositoryFetcher, gitHubRepositorySearchFetcher))
                .build();
    }
}
