package com.tehmou.rxbookapp.network;

import com.tehmou.rxbookapp.pojo.GitHubRepository;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by ttuo on 06/01/15.
 */
public interface GitHubService {
    @GET("/search/repositories")
    GitHubRepositorySearchResults search(
            @QueryMap Map<String, String> search
    );

    @GET("/repositories/{id}")
    GitHubRepository getRepository(@Path("id") Integer id);
}
