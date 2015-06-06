package com.tehmou.rxbookapp.viewmodels;

import com.tehmou.rxbookapp.data.DataLayer.GetGitHubRepositorySearch;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static com.tehmou.rxbookapp.data.DataStreamNotification.fetchingError;
import static com.tehmou.rxbookapp.data.DataStreamNotification.fetchingStart;
import static com.tehmou.rxbookapp.data.DataStreamNotification.onNext;
import static com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel.ProgressStatus.ERROR;
import static com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel.ProgressStatus.IDLE;
import static com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel.ProgressStatus.LOADING;
import static com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel.toProgressStatus;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by Pawel Polanski on 5/31/15.
 */
public class RepositoriesViewModelTest {

    @Test
    public void testStartFetchingReportedAsLoading() {
        assertEquals(LOADING, toProgressStatus().call(fetchingStart()));
    }

    @Test
    public void testFetchingErrorReportedAsError() {
        assertEquals(ERROR, toProgressStatus().call(fetchingError()));
    }

    @Test
    public void testAnyValueReportedAsIdle() {
        GitHubRepositorySearch value = new GitHubRepositorySearch("", null);

        assertEquals(IDLE, toProgressStatus().call(onNext(value)));
    }

    @Test
    public void testTooManyRepositoriesAreCappedToFive() {
        RepositoriesViewModel repositoriesViewModel = new RepositoriesViewModel(
                mock(GetGitHubRepositorySearch.class),
                repositoryId -> Observable.just(mock(GitHubRepository.class)));
        TestSubscriber<List<GitHubRepository>> observer = new TestSubscriber<>();

        repositoriesViewModel.toGitHubRepositoryList()
                             .call(Arrays.asList(1, 2, 3, 4, 5, 6))
                             .subscribe(observer);

        observer.awaitTerminalEvent();
        assertEquals("Invalid number of repositories",
                     5,
                     observer.getOnNextEvents().get(0).size());
    }

    @Test
    public void testTooLittleRepositoriesReturnThoseRepositories() {
        RepositoriesViewModel repositoriesViewModel = new RepositoriesViewModel(
                mock(GetGitHubRepositorySearch.class),
                repositoryId -> Observable.just(mock(GitHubRepository.class)));
        TestSubscriber<List<GitHubRepository>> observer = new TestSubscriber<>();

        repositoriesViewModel.toGitHubRepositoryList()
                             .call(Arrays.asList(1, 2, 3))
                             .subscribe(observer);

        observer.awaitTerminalEvent();
        assertEquals("Invalid number of repositories",
                     3,
                     observer.getOnNextEvents().get(0).size());
    }

}
