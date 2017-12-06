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

import io.reark.reark.data.stores.cores.ContentProviderStoreCore;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.advanced.data.schematicProvider.GitHubProvider;
import io.reark.rxgithubapp.advanced.data.schematicProvider.GitHubProvider.GitHubRepositories;
import io.reark.rxgithubapp.advanced.data.schematicProvider.JsonIdColumns;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;

import static io.reark.reark.utils.Preconditions.checkNotNull;

public class GitHubRepositoryStoreCore extends ContentProviderStoreCore<Integer, GitHubRepository> {

    private final Gson gson;

    public GitHubRepositoryStoreCore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(contentResolver);

        this.gson = Preconditions.get(gson);
    }

    @NonNull
    @Override
    protected String getAuthority() {
        return GitHubProvider.AUTHORITY;
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return GitHubRepositories.GITHUB_REPOSITORIES;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { JsonIdColumns.ID, JsonIdColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull final GitHubRepository item) {
        checkNotNull(item);

        ContentValues contentValues = new ContentValues();
        contentValues.put(JsonIdColumns.ID, item.getId());
        contentValues.put(JsonIdColumns.JSON, gson.toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected GitHubRepository read(@NonNull final Cursor cursor) {
        checkNotNull(cursor);

        final String json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON));
        return gson.fromJson(json, GitHubRepository.class);
    }

    @NonNull
    @Override
    protected GitHubRepository mergeValues(@NonNull final GitHubRepository v1, @NonNull final GitHubRepository v2) {
        checkNotNull(v1);
        checkNotNull(v2);

        // Creating a new object to avoid overwriting the passed argument
        GitHubRepository newValue = new GitHubRepository(v1);

        return newValue.overwrite(v2);
    }

    @NonNull
    @Override
    protected Uri getUriForId(@NonNull final Integer id) {
        checkNotNull(id);

        return GitHubRepositories.withId(id);
    }

    @NonNull
    @Override
    protected Integer getIdForUri(@NonNull final Uri uri) {
        checkNotNull(uri);

        return (int) GitHubRepositories.fromUri(uri);
    }
}
