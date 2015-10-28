package io.reark.rxgithubapp.data.stores;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import io.reark.reark.data.store.SingleItemContentProviderStore;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.data.schematicProvider.GitHubProvider;
import io.reark.rxgithubapp.data.schematicProvider.GitHubRepositorySearchColumns;
import io.reark.rxgithubapp.pojo.GitHubRepositorySearch;

/**
 * Created by ttuo on 07/01/15.
 */
public class GitHubRepositorySearchStore extends SingleItemContentProviderStore<GitHubRepositorySearch, String> {
    private static final String TAG = GitHubRepositorySearchStore.class.getSimpleName();

    public GitHubRepositorySearchStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
    }

    @NonNull
    @Override
    protected String getIdFor(@NonNull GitHubRepositorySearch item) {
        Preconditions.checkNotNull(item, "GitHub Repository Search cannot be null.");

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

    @NonNull
    @Override
    protected ContentValues readRaw(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(GitHubRepositorySearchColumns.JSON));
        ContentValues contentValues = new ContentValues();
        contentValues.put(GitHubRepositorySearchColumns.JSON, json);
        return contentValues;
    }

    @NonNull
    @Override
    public Uri getUriForKey(@NonNull String id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return GitHubProvider.GitHubRepositorySearches.withSearch(id);
    }

    @Override
    protected boolean contentValuesEqual(ContentValues v1, ContentValues v2) {
        return v1.getAsString(GitHubRepositorySearchColumns.JSON).equals(v2.getAsString(GitHubRepositorySearchColumns.JSON));
    }
}
