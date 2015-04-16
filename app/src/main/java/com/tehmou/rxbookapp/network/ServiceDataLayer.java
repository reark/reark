package com.tehmou.rxbookapp.network;

import android.content.ContentResolver;

import com.tehmou.rxbookapp.data.DataLayerBase;
import com.tehmou.rxbookapp.data.GitHubRepositorySearchStore;
import com.tehmou.rxbookapp.data.GitHubRepositoryStore;

/**
 * Created by ttuo on 16/04/15.
 */
public class ServiceDataLayer extends DataLayerBase {
    public ServiceDataLayer(ContentResolver contentResolver) {
        super(contentResolver);
    }

    GitHubRepositoryStore getGitHubRepositoryStore() {
        return gitHubRepositoryStore;
    }

    GitHubRepositorySearchStore getGitHubRepositorySearchStore() {
        return gitHubRepositorySearchStore;
    }
}
