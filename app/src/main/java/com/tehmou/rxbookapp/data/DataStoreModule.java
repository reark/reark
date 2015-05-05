package com.tehmou.rxbookapp.data;

import com.tehmou.rxbookapp.RxBookApp;

import android.content.ContentResolver;
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
    public DataLayer.GetNetworkRequestStatus provideGetNetworkRequestStatus(DataLayer dataLayer) {
        return dataLayer::getNetworkRequestStatus;
    }

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
    public ContentResolver contentResolver() {
        return RxBookApp.getInstance().getContentResolver();
    }

    @Provides
    public Context applicationContext() {
        return RxBookApp.getInstance().getApplicationContext();
    }

    @Provides
    @Singleton
    public DataLayer provideDataStoreModule(ContentResolver contentResolver,
                                            Context applicationContext) {
        return new DataLayer(contentResolver, applicationContext);
    }

}
