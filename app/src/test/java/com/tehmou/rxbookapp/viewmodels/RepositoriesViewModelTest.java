package com.tehmou.rxbookapp.viewmodels;

import com.tehmou.rxbookapp.data.DataStreamNotification;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;

import org.junit.Test;

import static com.tehmou.rxbookapp.data.DataStreamNotification.fetchingError;
import static com.tehmou.rxbookapp.data.DataStreamNotification.fetchingStart;
import static com.tehmou.rxbookapp.data.DataStreamNotification.onNext;
import static com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel.ProgressStatus.ERROR;
import static com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel.ProgressStatus.IDLE;
import static com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel.ProgressStatus.LOADING;
import static com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel.toProgressStatus;
import static org.junit.Assert.assertEquals;

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

}