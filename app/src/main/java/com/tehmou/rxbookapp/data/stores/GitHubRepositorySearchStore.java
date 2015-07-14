package com.tehmou.rxbookapp.data.stores;

import com.tehmou.rxbookapp.data.base.store.ContentProviderStoreBase;
import com.tehmou.rxbookapp.data.provider.GitHubRepositorySearchContract;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 07/01/15.
 */
public class GitHubRepositorySearchStore extends ContentProviderStoreBase<GitHubRepositorySearch, String> {
    private static final String TAG = GitHubRepositorySearchStore.class.getSimpleName();

    public GitHubRepositorySearchStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver, new GitHubRepositorySearchContract());
    }

    @NonNull
    @Override
    protected String getIdFor(@NonNull GitHubRepositorySearch item) {
        Preconditions.checkNotNull(item, "Github Repository Search cannot be null.");

        return item.getSearch();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return GitHubRepositorySearchContract.CONTENT_URI;
    }
}
