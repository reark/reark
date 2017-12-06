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

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reark.reark.data.stores.cores.ContentProviderStoreCore;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.advanced.data.schematicProvider.GitHubProvider;
import io.reark.rxgithubapp.advanced.data.schematicProvider.GitHubProvider.NetworkRequestStatuses;
import io.reark.rxgithubapp.advanced.data.schematicProvider.JsonIdColumns;
import io.reark.rxgithubapp.advanced.data.schematicProvider.NetworkRequestStatusColumns;

import static io.reark.reark.utils.Preconditions.checkNotNull;

public class NetworkRequestStatusStoreCore extends ContentProviderStoreCore<Integer, NetworkRequestStatus> {

    private final Gson gson;

    public NetworkRequestStatusStoreCore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(contentResolver);

        this.gson = Preconditions.get(gson);
    }

    @NonNull
    @Override
    protected <R> Observable<List<R>> groupOperations(@NonNull Observable<R> source) {
        // NetworkRequestStatus updates should not be grouped to ensure fast processing.
        return source.map(Collections::singletonList);
    }

    @NonNull
    @Override
    protected String getAuthority() {
        return GitHubProvider.AUTHORITY;
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return NetworkRequestStatuses.NETWORK_REQUEST_STATUSES;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { NetworkRequestStatusColumns.ID, NetworkRequestStatusColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull final NetworkRequestStatus item) {
        checkNotNull(item);

        ContentValues contentValues = new ContentValues();
        contentValues.put(JsonIdColumns.ID, item.getUri().hashCode());
        contentValues.put(JsonIdColumns.JSON, gson.toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected NetworkRequestStatus read(@NonNull final Cursor cursor) {
        checkNotNull(cursor);

        final String json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON));
        return gson.fromJson(json, NetworkRequestStatus.class);
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull final Integer id) {
        checkNotNull(id);

        return NetworkRequestStatuses.withId(id);
    }

    @NonNull
    @Override
    protected Integer getIdForUri(@NonNull final Uri uri) {
        checkNotNull(uri);

        return (int) NetworkRequestStatuses.fromUri(uri);
    }
}
