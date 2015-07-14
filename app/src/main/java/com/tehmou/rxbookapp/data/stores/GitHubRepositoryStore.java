package com.tehmou.rxbookapp.data.stores;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.tehmou.rxbookapp.data.base.store.ContentProviderStoreBase;
import com.tehmou.rxbookapp.data.schematicProvider.GitHubProvider;
import com.tehmou.rxbookapp.data.schematicProvider.JsonIdColumns;
import com.tehmou.rxbookapp.data.schematicProvider.UserSettingsColumns;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 07/01/15.
 */
public class GitHubRepositoryStore extends ContentProviderStoreBase<GitHubRepository, Integer> {
    private static final String TAG = GitHubRepositoryStore.class.getSimpleName();

    public GitHubRepositoryStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
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
        return GitHubProvider.GitHubRepositories.GITHUB_REPOSITORIES;
    }


    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { UserSettingsColumns.ID, UserSettingsColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(GitHubRepository item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(JsonIdColumns.ID, item.getId());
        contentValues.put(JsonIdColumns.JSON, new Gson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected GitHubRepository read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON));
        final GitHubRepository value = new Gson().fromJson(json, GitHubRepository.class);
        return value;
    }

    @NonNull
    @Override
    public Uri getUriForKey(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return GitHubProvider.GitHubRepositories.withId(id);
    }
}
