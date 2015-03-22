package com.tehmou.rxbookapp.data;

import com.tehmou.rxbookapp.network.NetworkApi;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;

import android.content.ContentResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;


/**
 * Created by ttuo on 19/03/14.
 */
public class DataLayer {
    final private NetworkApi networkApi;
    final private GitHubRepositoryStore gitHubRepositoryStore;
    final private GitHubRepositorySearchStore gitHubRepositorySearchStore;

    public DataLayer(ContentResolver contentResolver) {
        networkApi = new NetworkApi();
        gitHubRepositoryStore = new GitHubRepositoryStore(contentResolver);
        gitHubRepositorySearchStore = new GitHubRepositorySearchStore(contentResolver);
    }

    public Observable<GitHubRepositorySearch> getGitHubRepositorySearch(final String search) {
        Observable.<List<GitHubRepository>>create((subscriber) -> {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("q", search);
                        List<GitHubRepository> results = networkApi.search(params);
                        subscriber.onNext(results);
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .map((repositories) -> {
                    final List<Integer> repositoryIds = new ArrayList<>();
                    for (GitHubRepository repository : repositories) {
                        gitHubRepositoryStore.put(repository);
                        repositoryIds.add(repository.getId());
                    }
                    return new GitHubRepositorySearch(search, repositoryIds);
                })
                .subscribe(gitHubRepositorySearchStore::put);
        return gitHubRepositorySearchStore.getStream(search);
    }

    public Observable<GitHubRepository> getGitHubRepository(Integer repositoryId) {
        return gitHubRepositoryStore.getStream(repositoryId);
    }
}
