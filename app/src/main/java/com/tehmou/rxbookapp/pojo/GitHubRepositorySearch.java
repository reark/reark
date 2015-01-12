package com.tehmou.rxbookapp.pojo;

import com.tehmou.rxbookapp.network.GitHubService;

import java.util.List;

/**
 * Created by ttuo on 06/01/15.
 */
public class GitHubRepositorySearch {
    final private String search;
    final private List<String> items;

    public GitHubRepositorySearch(final String search, final List<String> items) {
        this.search = search;
        this.items = items;
    }

    public String getSearch() {
        return search;
    }

    public List<String> getItems() {
        return items;
    }
}
