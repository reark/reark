package com.tehmou.rxbookapp;

import android.content.ContentResolver;

import com.tehmou.rxbookapp.data.DataStoreModule;
import com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by pt2121 on 2/20/15.
 */
@Singleton
@Component(modules = {DataStoreModule.class})
public interface Graph {

    void inject(RepositoriesViewModel repositoriesViewModel);

    public final static class Initializer {

        public static Graph init() {
            return Dagger_Graph.builder()
                    .build();
        }
    }
}
