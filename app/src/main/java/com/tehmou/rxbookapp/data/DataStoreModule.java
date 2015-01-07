package com.tehmou.rxbookapp.data;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pt2121 on 2/20/15.
 */
@Module
public final class DataStoreModule {

    @Provides
    @Singleton
    public DataLayer provideDataStoreModule() {
        return new DataLayer();
    }

}
