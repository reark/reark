package io.reark.rxgithubapp.network;

import android.support.annotation.NonNull;

import java.util.List;

import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.pojo.GitHubRepository;

/**
 * Created by ttuo on 11/01/15.
 */
public class GitHubRepositorySearchResults {

    @NonNull
    final private List<GitHubRepository> items;

    public GitHubRepositorySearchResults(@NonNull final List<GitHubRepository> items) {
        Preconditions.checkNotNull(items, "GitHub Repository Items cannot be null.");

        this.items = items;
    }

    @NonNull
    public List<GitHubRepository> getItems() {
        return items;
    }
}
