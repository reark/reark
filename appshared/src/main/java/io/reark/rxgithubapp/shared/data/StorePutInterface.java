package io.reark.rxgithubapp.shared.data;

import android.net.Uri;

import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;

/**
 * Created by ttuo on 26/06/16.
 */
public interface StorePutInterface<T, U> {
    Uri getUriForId(U id);
    void put(T item);
}
