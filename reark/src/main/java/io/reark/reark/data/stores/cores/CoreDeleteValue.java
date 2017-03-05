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
 * A class used to represent a deletion from the database.
 */
final class CoreDeleteValue<U> implements CoreValue<U> {

    private final int id;

    @NonNull
    private final Uri uri;

    private CoreDeleteValue(int id, @NonNull Uri uri) {
        this.id = id;
        this.uri = uri;
    }

    @NonNull
    static <U> CoreDeleteValue<U> create(int id, @NonNull Uri uri) {
        return new CoreDeleteValue<>(id, uri);
    }

    @NonNull
    CoreOperation toOperation() {
        return new CoreOperation(id, uri, ContentProviderOperation.newDelete(uri).build());
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
    @Override
    public Type type() {
        return Type.DELETE;
    }

}
