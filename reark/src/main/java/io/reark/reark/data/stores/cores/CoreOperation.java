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
package io.reark.reark.data.stores.cores;

import android.content.ContentProviderOperation;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * A class wrapping ContentProviderOperation, the operation Uri, and operation identifier.
 */
public final class CoreOperation {

    @NonNull
    private static final ContentProviderOperation NO_OP =
            ContentProviderOperation.newInsert(Uri.EMPTY).build();

    private final int id;

    @NonNull
    private final Uri uri;

    @NonNull
    private final ContentProviderOperation operation;

    CoreOperation(int id, @NonNull Uri uri) {
        this(id, uri, NO_OP);
    }

    CoreOperation(int id, @NonNull Uri uri, @NonNull ContentProviderOperation operation) {
        this.id = id;
        this.uri = uri;
        this.operation = operation;
    }

    public int id() {
        return id;
    }

    @NonNull
    public Uri uri() {
        return uri;
    }

    @NonNull
    ContentProviderOperation contentOperation() {
        return operation;
    }

    boolean isValid() {
        return !NO_OP.equals(operation);
    }

}
