package com.tehmou.rxbookapp.pojo;

import com.tehmou.rxbookapp.network.GitHubService;

import java.util.List;

/**
 * Created by ttuo on 06/01/15.
 */
public class GitHubResults {
    final private List<GitHubRepository> items;

    public GitHubResults(List<GitHubRepository> items) {
        this.items = items;
    }

    public List<GitHubRepository> getItems() {
        return items;
    }
}
