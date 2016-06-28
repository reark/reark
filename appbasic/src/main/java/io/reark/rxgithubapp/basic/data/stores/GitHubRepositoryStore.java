package io.reark.rxgithubapp.basic.data.stores;

import io.reark.reark.data.stores.MemoryStore;
import io.reark.reark.utils.Log;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;

/**
 * Created by ttuo on 27/06/16.
 */
public class GitHubRepositoryStore extends MemoryStore<Integer, GitHubRepository> {
    public GitHubRepositoryStore() {
        super(GitHubRepository::getId);
        Log.d("lolg", "create");
    }
}
