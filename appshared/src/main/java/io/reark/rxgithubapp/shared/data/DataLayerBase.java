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
package io.reark.rxgithubapp.shared.data;

import android.support.annotation.NonNull;

import io.reark.reark.data.stores.interfaces.StoreInterface;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;

import static io.reark.reark.utils.Preconditions.get;

public abstract class DataLayerBase {

    @NonNull
    protected final StoreInterface<Integer, NetworkRequestStatus, NetworkRequestStatus> networkRequestStatusStore;

    @NonNull
    protected final StoreInterface<Integer, GitHubRepository, GitHubRepository> gitHubRepositoryStore;

    @NonNull
    protected final StoreInterface<String, GitHubRepositorySearch, GitHubRepositorySearch> gitHubRepositorySearchStore;

    protected DataLayerBase(@NonNull final StoreInterface<Integer, NetworkRequestStatus, NetworkRequestStatus> networkRequestStatusStore,
                            @NonNull final StoreInterface<Integer, GitHubRepository, GitHubRepository> gitHubRepositoryStore,
                            @NonNull final StoreInterface<String, GitHubRepositorySearch, GitHubRepositorySearch> gitHubRepositorySearchStore) {
        this.networkRequestStatusStore = get(networkRequestStatusStore);
        this.gitHubRepositoryStore = get(gitHubRepositoryStore);
        this.gitHubRepositorySearchStore = get(gitHubRepositorySearchStore);
    }
}
