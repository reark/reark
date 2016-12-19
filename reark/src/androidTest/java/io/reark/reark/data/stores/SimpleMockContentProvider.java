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
package io.reark.reark.data.stores;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.mock.MockContentProvider;

import java.util.AbstractMap;
import java.util.LinkedHashMap;

/**
 * Mock content provider to act as the underlying provider for stores in unit tests.
 */
public class SimpleMockContentProvider extends MockContentProvider {

    // LinkedHashMap keeps the insertion order for more straightforward testing
    private final AbstractMap<Uri, String> values = new LinkedHashMap<>(5);

    public interface DataColumns {
        String KEY = "key";
        String VALUE = "value";
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        values.put(uri, contentValues.getAsString(DataColumns.VALUE));
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return getCursor(uri, projection);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull final Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder, CancellationSignal cancellationSignal) {
        return getCursor(uri, projection);
    }

    // Crude imitation of the behavior of SQL content provider
    private Cursor getCursor(Uri uri, String... projection) {
        MatrixCursor cursor = new MatrixCursor(projection);

        if (values.containsKey(uri)) {
            String[] result = { uri.getLastPathSegment(), values.get(uri) };
            cursor.addRow(result);
        } else if ("0".equals(uri.getLastPathSegment())) {
            for (Uri key : values.keySet()) {
                String[] result = { key.getLastPathSegment(), values.get(key) };
                cursor.addRow(result);
            }
        }

        return cursor;
    }
}
