package com.tehmou.rxbookapp.network;

import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by ttuo on 06/01/15.
 */
public interface GitHubService {
    @GET("/search/repositories")
    public GitHubRepositorySearchResults search(
            @QueryMap Map<String, String> search
    );
}
