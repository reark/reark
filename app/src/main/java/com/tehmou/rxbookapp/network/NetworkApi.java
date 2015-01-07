package com.tehmou.rxbookapp.network;

import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.GitHubResults;

import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;

/**
 * Created by ttuo on 06/01/15.
 */
public class NetworkApi {
    private GitHubService gitHubService;

    public NetworkApi() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.github.com")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        gitHubService = restAdapter.create(GitHubService.class);
    }

    public List<GitHubRepository> search(Map<String, String> search) {
        GitHubResults results = gitHubService.search(search);
        return results.getItems();
    }
}
