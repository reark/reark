package com.tehmou.rxbookapp.data;

import com.google.gson.reflect.TypeToken;

import com.tehmou.rxbookapp.data.provider.GitHubRepositorySearchContract;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by ttuo on 07/01/15.
 */
public class GitHubRepositorySearchStore extends ContentProviderStoreBase<GitHubRepositorySearch, String> {
    private static final String TAG = GitHubRepositorySearchStore.class.getSimpleName();

    public GitHubRepositorySearchStore(ContentResolver contentResolver) {
        super(contentResolver, new TypeToken<GitHubRepositorySearch>() {}.getType());
    }

    @Override
    protected String getIdFor(GitHubRepositorySearch item) {
        return item.getSearch();
    }

    @Override
    public Uri getContentUri() {
        return GitHubRepositorySearchContract.CONTENT_URI;
    }
}
