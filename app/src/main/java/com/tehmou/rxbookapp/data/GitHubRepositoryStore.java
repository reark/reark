package com.tehmou.rxbookapp.data;

import com.tehmou.rxbookapp.pojo.GitHubRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 07/01/15.
 */
public class GitHubRepositoryStore {
    final private Map<String, List<GitHubRepository>> hash = new ConcurrentHashMap<>();
    final private Map<String, Subject<List<GitHubRepository>, List<GitHubRepository>>> subjectsHash = new ConcurrentHashMap<>();

    public void put(String search, List<GitHubRepository> repositories) {
        hash.put(search, repositories);
        if (subjectsHash.containsKey(search)) {
            subjectsHash.get(search).onNext(repositories);
        }
    }

    public Observable<List<GitHubRepository>> getRepositoriesForSearch(String search) {
        if (!subjectsHash.containsKey(search)) {
            subjectsHash.put(search, PublishSubject.create());
        }
        if (hash.containsKey(search)) {
            return Observable.concat(
                    Observable.just(hash.get(search)),
                    subjectsHash.get(search)
            );
        } else {
            return subjectsHash.get(search);
        }
    }
}
