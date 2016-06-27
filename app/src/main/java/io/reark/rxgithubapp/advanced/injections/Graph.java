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
package io.reark.rxgithubapp.advanced.injections;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import io.reark.rxgithubapp.advanced.RxGitHubApp;
import io.reark.rxgithubapp.advanced.activities.MainActivity;
import io.reark.rxgithubapp.advanced.data.DataStoreModule;
import io.reark.rxgithubapp.advanced.fragments.RepositoriesFragment;
import io.reark.rxgithubapp.advanced.fragments.RepositoryFragment;
import io.reark.rxgithubapp.advanced.network.NetworkService;
import io.reark.rxgithubapp.shared.injections.InstrumentationModule;
import io.reark.rxgithubapp.shared.viewmodels.RepositoriesViewModel;
import io.reark.rxgithubapp.shared.viewmodels.RepositoryViewModel;
import io.reark.rxgithubapp.shared.viewmodels.ViewModelModule;
import io.reark.rxgithubapp.advanced.widget.WidgetService;

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
