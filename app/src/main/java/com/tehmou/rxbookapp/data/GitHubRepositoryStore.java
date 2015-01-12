package com.tehmou.rxbookapp.data;

import android.content.ContentResolver;

import com.google.gson.reflect.TypeToken;
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
}
