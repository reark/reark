package io.reark.rxbookapp.network;

import io.reark.rxbookapp.pojo.GitHubRepository;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by ttuo on 06/01/15.
 */
public interface GitHubService {
    @GET("/search/repositories")
    Observable<GitHubRepositorySearchResults> search(@QueryMap Map<String, String> search);

    @GET("/repositories/{id}")
    Observable<GitHubRepository> getRepository(@Path("id") Integer id);
}
