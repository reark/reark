package com.tehmou.rxbookapp.network;

import com.tehmou.rxbookapp.pojo.GitHubRepository;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;
import rx.Observable;
import retrofit.client.Client;
import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 06/01/15.
 */
public class NetworkApi {

    private final GitHubService gitHubService;

    public NetworkApi(@NonNull Client client) {
        Preconditions.checkNotNull(client, "Client cannot be null.");

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint("https://api.github.com")
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .build();
        gitHubService = restAdapter.create(GitHubService.class);
    }

    public Observable<List<GitHubRepository>> search(Map<String, String> search) {
        return gitHubService.search(search)
                            .map(GitHubRepositorySearchResults::getItems);
    }

    public Observable<GitHubRepository> getRepository(int id) {
        return gitHubService.getRepository(id);
    }
}
