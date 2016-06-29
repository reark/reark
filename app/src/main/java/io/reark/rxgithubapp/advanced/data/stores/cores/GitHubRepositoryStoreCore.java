/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.rxgithubapp.advanced.data.stores.cores;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.advanced.data.schematicProvider.GitHubProvider;
import io.reark.rxgithubapp.advanced.data.schematicProvider.JsonIdColumns;
import io.reark.rxgithubapp.advanced.data.schematicProvider.UserSettingsColumns;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;

public class GitHubRepositoryStoreCore extends GsonStoreCoreBase<Integer, GitHubRepository> {
    private static final String TAG = GitHubRepositoryStoreCore.class.getSimpleName();

    public GitHubRepositoryStoreCore(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver, gson);
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
        contentValues.put(JsonIdColumns.JSON, getGson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected GitHubRepository read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON));
        final GitHubRepository value = getGson().fromJson(json, GitHubRepository.class);
        return value;
    }

    @NonNull
    @Override
    protected GitHubRepository mergeValues(@NonNull GitHubRepository v1, @NonNull GitHubRepository v2) {
        // Creating a new object to avoid overwriting the passed argument
        GitHubRepository newValue = new GitHubRepository(v1);

        return newValue.overwrite(v2);
    }

    @NonNull
    @Override
    protected Uri getUriForId(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return GitHubProvider.GitHubRepositories.withId(id);
    }

    @NonNull
    @Override
    protected Integer getIdForUri(@NonNull Uri uri) {
        Preconditions.checkNotNull(uri, "Uri cannot be null.");

        return (int) GitHubProvider.GitHubRepositories.fromUri(uri);
    }
}
