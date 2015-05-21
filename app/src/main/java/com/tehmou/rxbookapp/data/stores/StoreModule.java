package com.tehmou.rxbookapp.data.stores;

import android.content.ContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pawel Polanski on 5/16/15.
 */
@Module
public final class StoreModule {

    @Provides
    @Singleton
    public UserSettingsStore provideUserSettingsStore(ContentResolver contentResolver) {
        return new UserSettingsStore(contentResolver);
    }

    @Provides
    @Singleton
    public NetworkRequestStatusStore provideNetworkRequestStatusStore(ContentResolver contentResolver) {
        return new NetworkRequestStatusStore(contentResolver);
    }

    @Provides
    @Singleton
    public GitHubRepositoryStore provideGitHubRepositoryStore(ContentResolver contentResolver) {
        return new GitHubRepositoryStore(contentResolver);
    }

    @Provides
    @Singleton
    public GitHubRepositorySearchStore provideGitHubRepositorySearchStore(ContentResolver contentResolver) {
        return new GitHubRepositorySearchStore(contentResolver);
    }

}
