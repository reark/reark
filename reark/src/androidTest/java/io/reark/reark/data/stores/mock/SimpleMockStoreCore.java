/*
 * The MIT License
 *
 * Copyright (c) 2013-2017 reark project contributors
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
package io.reark.reark.data.stores.mock;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Single;
import io.reark.reark.data.stores.cores.ContentProviderStoreCore;

/**
 * A simple store core implementing the methods content provider requires.
 */
public class SimpleMockStoreCore extends ContentProviderStoreCore<Integer, String> {

    public static final String AUTHORITY = "test.authority";

    private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    private static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "veggies");

    private static final String[] PROJECTION = {
            SimpleMockContentProvider.DataColumns.KEY,
            SimpleMockContentProvider.DataColumns.VALUE
    };

    public SimpleMockStoreCore(@NonNull final ContentResolver contentResolver) {
        super(contentResolver);
    }

    @NonNull
    @Override
    public Single<List<String>> getCached() {
        return getAllOnce(getUriForId(0));
    }

    @NonNull
    @Override
    protected String getAuthority() {
        return AUTHORITY;
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return CONTENT_URI;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return PROJECTION;
    }

    @NonNull
    @Override
    protected String read(@NonNull final Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(SimpleMockContentProvider.DataColumns.VALUE));
    }

    @NonNull
    @Override
    public ContentValues getContentValuesForItem(@NonNull final String item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SimpleMockContentProvider.DataColumns.KEY, item.hashCode());
        contentValues.put(SimpleMockContentProvider.DataColumns.VALUE, item);
        return contentValues;
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull final Integer id) {
        return Uri.withAppendedPath(getContentUri(), String.valueOf(id));
    }

    @NonNull
    @Override
    protected Integer getIdForUri(@NonNull final Uri uri) {
        return Integer.valueOf(uri.getLastPathSegment());
    }
}
