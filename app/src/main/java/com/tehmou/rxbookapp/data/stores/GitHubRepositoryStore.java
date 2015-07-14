package com.tehmou.rxbookapp.data.stores;

import com.tehmou.rxbookapp.data.base.store.ContentProviderStoreBase;
import com.tehmou.rxbookapp.data.provider.GitHubRepositoryContract;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 07/01/15.
 */
public class GitHubRepositoryStore extends ContentProviderStoreBase<GitHubRepository, Integer> {
    private static final String TAG = GitHubRepositoryStore.class.getSimpleName();

    public GitHubRepositoryStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver, new GitHubRepositoryContract());
    }

    @NonNull
    @Override
    protected Integer getIdFor(@NonNull GitHubRepository item) {
        Preconditions.checkNotNull(item, "Github Repository cannot be null.");

        return item.getId();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return GitHubRepositoryContract.CONTENT_URI;
    }
}
