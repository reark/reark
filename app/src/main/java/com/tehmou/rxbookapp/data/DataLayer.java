package com.tehmou.rxbookapp.data;

import com.tehmou.rxbookapp.network.NetworkApi;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


/**
 * Created by ttuo on 19/03/14.
 */
public class DataLayer {
    final private NetworkApi networkApi;
    final private RepositoryStore repositoryStore;

    static private DataLayer instance;

    static public DataLayer getInstance() {
        if (instance == null) {
            instance = new DataLayer();
        }
        return instance;
    }

    public DataLayer() {
        networkApi = new NetworkApi();
        repositoryStore = new RepositoryStore();
    }

    public Observable<List<GitHubRepository>> getGitHub(final String search) {
        Observable.create(new Observable.OnSubscribe<List<GitHubRepository>>() {
                    @Override
                    public void call(Subscriber<? super List<GitHubRepository>> subscriber) {
                        try {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("q", search);
                            List<GitHubRepository> results = networkApi.search(params);
                            subscriber.onNext(results);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.computation())
                .subscribe((repositories) -> {
                    repositoryStore.put(search, repositories);
                });
        return repositoryStore.getRepositoriesForSearch(search);
    }
}
