package com.tehmou.rxbookapp.data;

import android.content.ContentResolver;
import android.net.Uri;

import com.google.gson.reflect.TypeToken;
import com.tehmou.rxbookapp.data.provider.GitHubRepositoryContract;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

/**
 * Created by ttuo on 07/01/15.
 */
public class GitHubRepositoryStore extends ContentProviderStoreBase<GitHubRepository, Integer> {
    private static final String TAG = GitHubRepositoryStore.class.getSimpleName();

    public GitHubRepositoryStore(ContentResolver contentResolver) {
        super(contentResolver, new TypeToken<GitHubRepository>() {}.getType());
    }

    @Override
    protected Integer getIdFor(GitHubRepository item) {
        return item.getId();
    }

    @Override
    protected Uri getContentUri() {
        return GitHubRepositoryContract.CONTENT_URI;
    }
}
