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
import android.net.Uri;
import android.support.annotation.NonNull;

import rx.subjects.Subject;

/**
 * A class used to represent a deletion from the database.
 */
public final class CoreValueDelete<U> implements CoreValue<U> {

    @NonNull
    private final Uri uri;

    @NonNull
    private final Subject<Boolean, Boolean> subject;

    private CoreValueDelete(@NonNull Uri uri, @NonNull Subject<Boolean, Boolean> subject) {
        this.uri = uri;
        this.subject = subject;
    }

    @NonNull
    public static <U> CoreValueDelete<U> create(@NonNull Subject<Boolean, Boolean> subject, @NonNull Uri uri) {
        return new CoreValueDelete<>(uri, subject);
    }

    @NonNull
    public CoreOperation toDeleteOperation() {
        return new CoreOperation(uri, subject, ContentProviderOperation.newDelete(uri).build());
    }

    @Override
    @NonNull
    public CoreOperation noOperation() {
        return new CoreOperation(uri, subject);
    }

    @NonNull
    @Override
    public Subject<Boolean, Boolean> subject() {
        return subject;
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
