package io.reark.rxgithubapp.basic.data.stores;

import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;

/**
 * Created by ttuo on 27/06/16.
 */
public class GitHubRepositorySearchStore extends MemoryStore<GitHubRepositorySearch, String> {
    public GitHubRepositorySearchStore() {
        super(GitHubRepositorySearch::getSearch);
    }
}
