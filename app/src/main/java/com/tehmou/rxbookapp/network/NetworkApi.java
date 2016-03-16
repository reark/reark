
package com.tehmou.rxbookapp.network;


//import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 06/01/15.
 */
public class NetworkApi {

    private final GitHubService gitHubService;

    public NetworkApi(@NonNull OkHttpClient client) {
        Preconditions.checkNotNull(client, "Client cannot be null.");

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.github.com")
                .client(client)
                .build();
        gitHubService = retrofit.create(GitHubService.class);
    }

    public Observable<List<GitHubRepository>> search(Map<String, String> search) {
        return gitHubService.search(search)
                .map(GitHubRepositorySearchResults::getItems);
    }

    public Observable<GitHubRepository> getRepository(int id) {
        return gitHubService.getRepository(id);
    }
}