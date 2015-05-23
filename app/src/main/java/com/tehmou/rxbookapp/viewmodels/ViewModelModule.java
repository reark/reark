package com.tehmou.rxbookapp.viewmodels;

import com.tehmou.rxbookapp.data.DataLayer.FetchAndGetGitHubRepository;
import com.tehmou.rxbookapp.data.DataLayer.GetGitHubRepository;
import com.tehmou.rxbookapp.data.DataLayer.GetGitHubRepositorySearch;
import com.tehmou.rxbookapp.data.DataLayer.GetUserSettings;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pawel Polanski on 5/16/15.
 */
@Module
public class ViewModelModule {

    @Provides
    public RepositoriesViewModel provideRepositoriesViewModel(GetGitHubRepositorySearch repositorySearch,
                                                              GetGitHubRepository repositoryRepository) {
        return new RepositoriesViewModel(repositorySearch, repositoryRepository);
    }

    @Provides
    public RepositoryViewModel provideRepositoryViewModel(GetUserSettings getUserSettings,
                                                          FetchAndGetGitHubRepository fetchAndGetGitHubRepository) {
        return new RepositoryViewModel(getUserSettings, fetchAndGetGitHubRepository);
    }

}
