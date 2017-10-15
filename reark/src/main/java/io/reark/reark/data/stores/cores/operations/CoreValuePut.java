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

import io.reactivex.subjects.Subject;

/**
 * A class used to represent a change to the database.
 */
public final class CoreValuePut<U> implements CoreValue<U> {

    @NonNull
    private final Uri uri;

    @NonNull
    private final U item;

    @NonNull
    private final Subject<Boolean> completionNotifier;

    private CoreValuePut(@NonNull Uri uri, @NonNull U item, @NonNull Subject<Boolean> completionNotifier) {
        this.uri = uri;
        this.item = item;
        this.completionNotifier = completionNotifier;
    }

    @NonNull
    public static <U> CoreValuePut<U> create(@NonNull Subject<Boolean> completionNotifier, @NonNull Uri uri, @NonNull U item) {
        return new CoreValuePut<>(uri, item, completionNotifier);
    }

    @NonNull
    public CoreOperation toInsertOperation(@NonNull ContentValues values) {
        return new CoreOperation(uri, completionNotifier, ContentProviderOperation
                .newInsert(uri)
                .withValues(values)
                .build());
    }

    @NonNull
    public CoreOperation toUpdateOperation(@NonNull ContentValues values) {
        return new CoreOperation(uri, completionNotifier, ContentProviderOperation
                .newUpdate(uri)
                .withValues(values)
                .build());
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
        return Type.PUT;
    }

    @NonNull
    @Override
    public Subject<Boolean> completionNotifier() {
        return completionNotifier;
    }

    @Override
    @NonNull
    public CoreOperation noOperation() {
        return new CoreOperation(uri, completionNotifier);
    }

}
