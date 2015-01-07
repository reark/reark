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
public class DataStore {
    final private NetworkApi networkApi;

    static private DataStore instance;

    static public DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public DataStore() {
        networkApi = new NetworkApi();
    }

    public Observable<List<GitHubRepository>> getGitHub(final String search) {
        return Observable
                .create(new Observable.OnSubscribe<List<GitHubRepository>>() {
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
                .subscribeOn(Schedulers.computation());
    }
}
