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
import io.reark.rxgithubapp.advanced.data.schematicProvider.GitHubProvider.GitHubRepositorySearches;
import io.reark.rxgithubapp.advanced.data.schematicProvider.GitHubRepositorySearchColumns;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;

import static io.reark.reark.utils.Preconditions.checkNotNull;

public class GitHubRepositorySearchStoreCore extends ContentProviderStoreCore<String, GitHubRepositorySearch> {

    private final Gson gson;

    public GitHubRepositorySearchStoreCore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
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
        return GitHubRepositorySearches.GITHUB_REPOSITORY_SEARCHES;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { GitHubRepositorySearchColumns.SEARCH, GitHubRepositorySearchColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull final GitHubRepositorySearch item) {
        checkNotNull(item);

        ContentValues contentValues = new ContentValues();
        contentValues.put(GitHubRepositorySearchColumns.SEARCH, item.getSearch());
        contentValues.put(GitHubRepositorySearchColumns.JSON, gson.toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected GitHubRepositorySearch read(@NonNull final Cursor cursor) {
        checkNotNull(cursor);

        final String json = cursor.getString(cursor.getColumnIndex(GitHubRepositorySearchColumns.JSON));
        return gson.fromJson(json, GitHubRepositorySearch.class);
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull final String id) {
        checkNotNull(id);

        return GitHubRepositorySearches.withSearch(id);
    }

    @NonNull
    @Override
    protected String getIdForUri(@NonNull final Uri uri) {
        checkNotNull(uri);

        return GitHubRepositorySearches.fromUri(uri);
    }
}
