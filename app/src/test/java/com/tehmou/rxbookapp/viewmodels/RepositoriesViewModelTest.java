package com.tehmou.rxbookapp.viewmodels;

import com.tehmou.rxbookapp.data.DataLayer;
import com.tehmou.rxbookapp.data.DataLayer.GetGitHubRepositorySearch;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;
import com.tehmou.rxbookapp.utils.RxBinderUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private RepositoriesViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new RepositoriesViewModel(
                mock(GetGitHubRepositorySearch.class),
                repositoryId -> Observable.just(mock(GitHubRepository.class)));
    }

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
        TestSubscriber<List<GitHubRepository>> observer = new TestSubscriber<>();

        viewModel.toGitHubRepositoryList()
                 .call(Arrays.asList(1, 2, 3, 4, 5, 6))
                 .subscribe(observer);

        observer.awaitTerminalEvent();
        assertEquals("Invalid number of repositories",
                     5,
                     observer.getOnNextEvents().get(0).size());
    }

    @Test
    public void testTooLittleRepositoriesReturnThoseRepositories() {
        TestSubscriber<List<GitHubRepository>> observer = new TestSubscriber<>();

        viewModel.toGitHubRepositoryList()
                 .call(Arrays.asList(1, 2, 3))
                 .subscribe(observer);

        observer.awaitTerminalEvent();
        assertEquals("Invalid number of repositories",
                     3,
                     observer.getOnNextEvents().get(0).size());
    }

    @Test
    public void testThrowsNullPointerExceptionWhenRepositoryIdIsNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Repository Id cannot be null.");

        viewModel.getGitHubRepositoryObservable(null);
    }

    @Test
    public void testThrowsNullPointerExceptionWhenNetworkStatusIsNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("ProgressStatus cannot be null.");

        viewModel.setNetworkStatusText(null);
    }

    @Test
    public void testThrowsNullPointerExceptionWhenSearchStringIsNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Search Observable cannot be null.");

        viewModel.setSearchStringObservable(null);
    }

    @Test
    public void testThrowsNullPointerExceptionWhenSelectedRepositoryIsNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Selected repository cannot be null.");

        viewModel.selectRepository(null);
    }

    @Test
    public void testThrowsNullPointerExceptionConstructedWithNullRepositorySearch() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("GetGitHubRepositorySearch cannot be null.");

        new RepositoriesViewModel(null, mock(DataLayer.GetGitHubRepository.class));
    }

    @Test
    public void testThrowsNullPointerExceptionConstructedWithNullRepository() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("GetGitHubRepository cannot be null.");

        new RepositoriesViewModel(mock(GetGitHubRepositorySearch.class), null);
    }

}
