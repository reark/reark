package com.tehmou.rxbookapp.data.stores;

import com.tehmou.rxbookapp.data.base.store.ContentProviderStoreBase;
import com.tehmou.rxbookapp.data.provider.GitHubRepositoryContract;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by ttuo on 07/01/15.
 */
public class GitHubRepositoryStore extends ContentProviderStoreBase<GitHubRepository, Integer> {
    private static final String TAG = GitHubRepositoryStore.class.getSimpleName();

    public GitHubRepositoryStore(ContentResolver contentResolver) {
        super(contentResolver, new GitHubRepositoryContract());
    }

    @Override
    protected Integer getIdFor(GitHubRepository item) {
        return item.getId();
    }

    @Override
    public Uri getContentUri() {
        return GitHubRepositoryContract.CONTENT_URI;
    }
}
