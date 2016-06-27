package io.reark.rxgithubapp.shared.data;

import android.net.Uri;

import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;

/**
 * Created by ttuo on 26/06/16.
 */
public interface StorePutInterface<T> {
    void put(T item);
}
