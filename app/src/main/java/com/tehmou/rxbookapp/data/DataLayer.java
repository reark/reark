package com.tehmou.rxbookapp.data;

import com.tehmou.rxbookapp.network.NetworkApi;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by ttuo on 19/03/14.
 */
public class DataLayer {
    final private NetworkApi networkApi;
    final private GitHubRepositoryStore gitHubRepositoryStore;
    final private GitHubRepositorySearchStore gitHubRepositorySearchStore;

    static private DataLayer instance;

    static public DataLayer getInstance() {
        if (instance == null) {
            instance = new DataLayer();
        }
        return instance;
    }

    public DataLayer() {
        networkApi = new NetworkApi();
        gitHubRepositoryStore = new GitHubRepositoryStore();
        gitHubRepositorySearchStore = new GitHubRepositorySearchStore();
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
                    final List<String> repositoryIds = new ArrayList<String>();
                    for (GitHubRepository repository : repositories) {
                        repositoryIds.add(gitHubRepositoryStore.put(repository));
                    }
                    GitHubRepositorySearch gitHubRepositorySearch =
                            new GitHubRepositorySearch(search, repositoryIds);
                    return gitHubRepositorySearch;
                })
                .subscribe((repositorySearch) -> {
                    gitHubRepositorySearchStore.put(search, repositorySearch);
                });
        return gitHubRepositorySearchStore.getStream(search);
    }

    public Observable<GitHubRepository> getGitHubRepository(String repositoryId) {
        return gitHubRepositoryStore.getStream(repositoryId);
    }
}
