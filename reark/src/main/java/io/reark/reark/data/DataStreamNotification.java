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
package io.reark.reark.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reark.reark.utils.Preconditions;

public class DataStreamNotification<T> {

    public enum Type {
        FETCHING_START,
        FETCHING_COMPLETED,
        FETCHING_ERROR,
        ON_NEXT
    }

    @NonNull
    private final Type type;

    @Nullable
    private final T value;

    @Nullable
    private final Throwable error;

    private DataStreamNotification(@NonNull Type type, @Nullable T value, @Nullable Throwable error) {
        Preconditions.checkNotNull(type, "Type cannot be null.");

        this.type = type;
        this.value = value;
        this.error = error;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    @Nullable
    public T getValue() {
        return value;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }

    @NonNull
    public static<T> DataStreamNotification<T> fetchingStart() {
        return new DataStreamNotification<>(Type.FETCHING_START, null, null);
    }

    @NonNull
    public static<T> DataStreamNotification<T> onNext(T value) {
        return new DataStreamNotification<>(Type.ON_NEXT, value, null);
    }

    @NonNull
    public static<T> DataStreamNotification<T> fetchingCompleted() {
        return new DataStreamNotification<>(Type.FETCHING_COMPLETED, null, null);
    }

    @NonNull
    public static<T> DataStreamNotification<T> fetchingError() {
        return new DataStreamNotification<>(Type.FETCHING_ERROR, null, null);
    }

    public boolean isFetchingStart() {
        return type.equals(Type.FETCHING_START);
    }

    public boolean isOnNext() {
        return type.equals(Type.ON_NEXT);
    }

    public boolean isFetchingCompleted() {
        return type.equals(Type.FETCHING_COMPLETED);
    }

    public boolean isFetchingError() {
        return type.equals(Type.FETCHING_ERROR);
    }

    @Override
    public String toString() {
        return "DataStreamNotification(" + type + ", " + value + ")";
    }
}
