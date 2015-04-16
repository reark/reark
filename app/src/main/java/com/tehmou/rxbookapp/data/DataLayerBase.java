package com.tehmou.rxbookapp.data;

import android.content.ContentResolver;

/**
 * Created by ttuo on 16/04/15.
 */
abstract public class DataLayerBase {
    protected final GitHubRepositoryStore gitHubRepositoryStore;
    protected final GitHubRepositorySearchStore gitHubRepositorySearchStore;

    public DataLayerBase(ContentResolver contentResolver) {
        gitHubRepositoryStore = new GitHubRepositoryStore(contentResolver);
        gitHubRepositorySearchStore = new GitHubRepositorySearchStore(contentResolver);
    }
}
