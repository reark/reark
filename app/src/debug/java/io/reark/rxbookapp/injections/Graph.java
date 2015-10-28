package io.reark.rxbookapp.injections;

import android.app.Application;

import io.reark.rxbookapp.RxBookApp;
import io.reark.rxbookapp.activities.MainActivity;
import io.reark.rxbookapp.data.DataStoreModule;
import io.reark.rxbookapp.fragments.RepositoriesFragment;
import io.reark.rxbookapp.fragments.RepositoryFragment;
import io.reark.rxbookapp.network.NetworkService;
import io.reark.rxbookapp.viewmodels.RepositoriesViewModel;
import io.reark.rxbookapp.viewmodels.RepositoryViewModel;
import io.reark.rxbookapp.viewmodels.ViewModelModule;
import io.reark.rxbookapp.widget.WidgetService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by pt2121 on 2/20/15.
 */
@Singleton
@Component(modules = {ApplicationModule.class, DataStoreModule.class, ViewModelModule.class,
                      DebugInstrumentationModule.class})
public interface Graph {

    void inject(RepositoriesViewModel repositoriesViewModel);
    void inject(RepositoryViewModel widgetService);
    void inject(WidgetService widgetService);
    void inject(MainActivity mainActivity);
    void inject(RepositoriesFragment repositoriesFragment);
    void inject(RepositoryFragment repositoryFragment);
    void inject(NetworkService networkService);
    void inject(RxBookApp rxBookApp);

    final class Initializer {

        public static Graph init(Application application) {
            return DaggerGraph.builder()
                    .applicationModule(new ApplicationModule(application))
                    .build();
        }

    }
}
