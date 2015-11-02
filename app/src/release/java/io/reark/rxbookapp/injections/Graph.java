package io.reark.rxgithubapp.injections;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import io.reark.rxgithubapp.RxGitHubApp;
import io.reark.rxgithubapp.activities.MainActivity;
import io.reark.rxgithubapp.data.DataStoreModule;
import io.reark.rxgithubapp.fragments.RepositoriesFragment;
import io.reark.rxgithubapp.fragments.RepositoryFragment;
import io.reark.rxgithubapp.network.NetworkService;
import io.reark.rxgithubapp.viewmodels.RepositoriesViewModel;
import io.reark.rxgithubapp.viewmodels.RepositoryViewModel;
import io.reark.rxgithubapp.viewmodels.ViewModelModule;
import io.reark.rxgithubapp.widget.WidgetService;

/**
 * Created by pt2121 on 2/20/15.
 */
@Singleton
@Component(modules = {ApplicationModule.class, DataStoreModule.class, ViewModelModule.class,
                      InstrumentationModule.class})
public interface Graph {

    void inject(RepositoriesViewModel repositoriesViewModel);
    void inject(RepositoryViewModel widgetService);
    void inject(WidgetService widgetService);
    void inject(MainActivity mainActivity);
    void inject(RepositoriesFragment repositoriesFragment);
    void inject(RepositoryFragment repositoryFragment);
    void inject(NetworkService networkService);
    void inject(RxGitHubApp rxGitHubApp);

    final class Initializer {

        public static Graph init(Application application) {
            return DaggerGraph.builder()
                              .applicationModule(new ApplicationModule(application))
                              .build();
        }
    }
}
