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
import io.reark.rxgithubapp.data.schematicProvider.JsonIdColumns;
import io.reark.rxgithubapp.data.schematicProvider.UserSettingsColumns;
import io.reark.rxgithubapp.pojo.GitHubRepository;

/**
 * Created by ttuo on 07/01/15.
 */
public class GitHubRepositoryStore extends SingleItemContentProviderStore<GitHubRepository, Integer> {
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
