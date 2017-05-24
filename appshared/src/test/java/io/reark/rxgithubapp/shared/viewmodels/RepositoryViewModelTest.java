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
package io.reark.rxgithubapp.shared.viewmodels;

import org.assertj.core.util.Compatibility;
import org.junit.Test;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import io.reark.reark.utils.Log;
import io.reark.rxgithubapp.shared.pojo.GitHubOwner;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.UserSettings;

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
                () -> Flowable.just(new UserSettings(1)),
                repositoryId -> Flowable.just(gitHubRepository));
        TestSubscriber<GitHubRepository> observer = new TestSubscriber<>();
        repositoryViewModel.getRepository().subscribe(observer);

        repositoryViewModel.subscribeToDataStore();

        assertEquals("Invalid number of repositories",
                     1,
                     observer.getEvents().get(0).size());
        assertEquals("Provided GitHubRepository does not match",
                     gitHubRepository,
                     observer.getEvents().get(0).get(0));
    }

}
