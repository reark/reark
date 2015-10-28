package io.reark.rxgithubapp.viewmodels;

import dagger.Module;
import dagger.Provides;
import io.reark.rxgithubapp.data.DataLayer.FetchAndGetGitHubRepository;
import io.reark.rxgithubapp.data.DataLayer.GetGitHubRepository;
import io.reark.rxgithubapp.data.DataLayer.GetGitHubRepositorySearch;
import io.reark.rxgithubapp.data.DataLayer.GetUserSettings;

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
