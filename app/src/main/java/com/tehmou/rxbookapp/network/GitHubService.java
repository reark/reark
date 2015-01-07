package com.tehmou.rxbookapp.network;

import com.tehmou.rxbookapp.pojo.GitHubResults;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by ttuo on 06/01/15.
 */
public interface GitHubService {
    @GET("/search/repositories")
    public GitHubResults search(
            @QueryMap Map<String, String> search
    );
}
