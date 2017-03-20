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
package io.reark.reark.data.stores.cores.operations;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * A class used to represent a change to the database.
 */
public final class CoreValuePut<U> implements CoreValue<U> {

    private final int id;

    @NonNull
    private final Uri uri;

    @NonNull
    private final U item;

    private CoreValuePut(int id, @NonNull Uri uri, @NonNull U item) {
        this.id = id;
        this.uri = uri;
        this.item = item;
    }

    @NonNull
    public static <U> CoreValuePut<U> create(int id, @NonNull Uri uri, @NonNull U item) {
        return new CoreValuePut<>(id, uri, item);
    }

    @NonNull
    public CoreOperation toInsertOperation(@NonNull ContentValues values) {
        return new CoreOperation(id, uri, ContentProviderOperation
                .newInsert(uri)
                .withValues(values)
                .build());
    }

    @NonNull
    public CoreOperation toUpdateOperation(@NonNull ContentValues values) {
        return new CoreOperation(id, uri, ContentProviderOperation
                .newUpdate(uri)
                .withValues(values)
                .build());
    }

    @Override
    @NonNull
    public CoreOperation noOperation() {
        return new CoreOperation(id, uri);
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    @NonNull
    public Uri uri() {
        return uri;
    }

    @NonNull
    public U item() {
        return item;
    }

    @NonNull
    @Override
    public Type type() {
        return Type.UPDATE;
    }

}
