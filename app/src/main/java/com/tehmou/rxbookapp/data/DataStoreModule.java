package com.tehmou.rxbookapp.data;

import com.tehmou.rxbookapp.RxBookApp;

import android.content.ContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pt2121 on 2/20/15.
 */
@Module
public final class DataStoreModule {

    @Provides
    public ContentResolver contentResolver() {
        return RxBookApp.getInstance().getContentResolver();
    }

    @Provides
    @Singleton
    public DataLayer provideDataStoreModule(ContentResolver contentResolver) {
        return new DataLayer(contentResolver);
    }

}
