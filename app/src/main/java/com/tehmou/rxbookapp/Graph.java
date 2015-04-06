package com.tehmou.rxbookapp;

import com.tehmou.rxbookapp.activities.MainActivity;
import com.tehmou.rxbookapp.data.DataStoreModule;
import com.tehmou.rxbookapp.fragments.RepositoryFragment;
import com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel;
import com.tehmou.rxbookapp.viewmodels.RepositoryViewModel;
import com.tehmou.rxbookapp.widget.WidgetService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by pt2121 on 2/20/15.
 */
@Singleton
@Component(modules = {DataStoreModule.class})
public interface Graph {

    void inject(RepositoriesViewModel repositoriesViewModel);
    void inject(RepositoryViewModel widgetService);
    void inject(WidgetService widgetService);
    void inject(MainActivity mainActivity);

    public final static class Initializer {

        public static Graph init() {
            return DaggerGraph.builder()
                    .build();
        }
    }
}
