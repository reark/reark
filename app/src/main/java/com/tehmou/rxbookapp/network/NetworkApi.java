package com.tehmou.rxbookapp.network;

import com.tehmou.rxbookapp.pojo.GitHubRepository;

import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;

/**
 * Created by ttuo on 06/01/15.
 */
public class NetworkApi {
    private final GitHubService gitHubService;

    public NetworkApi() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.github.com")
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .build();
        gitHubService = restAdapter.create(GitHubService.class);
    }

    public List<GitHubRepository> search(Map<String, String> search) {
        GitHubRepositorySearchResults results = gitHubService.search(search);
        return results.getItems();
    }
}
