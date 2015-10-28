package io.reark.rxgithubapp.viewmodels;

import org.junit.Test;

import io.reark.rxgithubapp.pojo.GitHubOwner;
import io.reark.rxgithubapp.pojo.GitHubRepository;
import io.reark.rxgithubapp.pojo.UserSettings;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RepositoryViewModelTest {

    @Test(timeout = 1000)
    public void testRepositoryViewModelFetchesValidGitHubRepository() throws Exception {
        GitHubRepository gitHubRepository = new GitHubRepository(2,
                                                                 "repo",
                                                                 3,
                                                                 4,
                                                                 mock(GitHubOwner.class));
        RepositoryViewModel repositoryViewModel = new RepositoryViewModel(
                () -> Observable.just(new UserSettings(1)),
                repositoryId -> Observable.just(gitHubRepository));
        TestSubscriber<GitHubRepository> observer = new TestSubscriber<>();
        repositoryViewModel.getRepository().subscribe(observer);

        repositoryViewModel.subscribeToDataStore();

        observer.awaitTerminalEvent();
        assertEquals("Invalid number of repositories",
                     1,
                     observer.getOnNextEvents().size());
        assertEquals("Provided GitHubRepository does not match",
                     gitHubRepository,
                     observer.getOnNextEvents().get(0));
    }

}
