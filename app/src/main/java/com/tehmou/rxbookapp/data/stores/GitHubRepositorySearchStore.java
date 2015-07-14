package com.tehmou.rxbookapp.data.stores;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.tehmou.rxbookapp.data.base.store.ContentProviderStoreBase;
import com.tehmou.rxbookapp.data.schematicProvider.GitHubProvider;
import com.tehmou.rxbookapp.data.schematicProvider.GitHubRepositorySearchColumns;
import com.tehmou.rxbookapp.pojo.GitHubRepositorySearch;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 07/01/15.
 */
public class GitHubRepositorySearchStore extends ContentProviderStoreBase<GitHubRepositorySearch, String> {
    private static final String TAG = GitHubRepositorySearchStore.class.getSimpleName();

    public GitHubRepositorySearchStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
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
        return GitHubProvider.GitHubRepositorySearches.GITHUB_REPOSITORY_SEARCHES;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { GitHubRepositorySearchColumns.SEARCH, GitHubRepositorySearchColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(GitHubRepositorySearch item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(GitHubRepositorySearchColumns.SEARCH, item.getSearch());
        contentValues.put(GitHubRepositorySearchColumns.JSON, new Gson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected GitHubRepositorySearch read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(GitHubRepositorySearchColumns.JSON));
        final GitHubRepositorySearch value = new Gson().fromJson(json, GitHubRepositorySearch.class);
        return value;
    }
}
