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

import static io.reark.reark.utils.Preconditions.get;

public final class DataStreamNotification<T> {

    public enum Type {
        ONGOING,
        ON_NEXT,
        COMPLETED_WITH_VALUE,
        COMPLETED_WITHOUT_VALUE,
        COMPLETED_WITH_ERROR
    }

    @NonNull
    private final Type type;

    @Nullable
    private final T value;

    @Nullable
    private final String error;

    private DataStreamNotification(@NonNull Type type,
                                   @Nullable T value,
                                   @Nullable String error) {
        this.type = get(type);
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
    public String getError() {
        return error;
    }

    @NonNull
    public static<T> DataStreamNotification<T> ongoing() {
        return new DataStreamNotification<>(Type.ONGOING, null, null);
    }

    @NonNull
    public static<T> DataStreamNotification<T> onNext(T value) {
        return new DataStreamNotification<>(Type.ON_NEXT, value, null);
    }

    @NonNull
    public static<T> DataStreamNotification<T> completedWithValue() {
        return new DataStreamNotification<>(Type.COMPLETED_WITH_VALUE, null, null);
    }

    @NonNull
    public static<T> DataStreamNotification<T> completedWithoutValue() {
        return new DataStreamNotification<>(Type.COMPLETED_WITHOUT_VALUE, null, null);
    }

    @NonNull
    public static<T> DataStreamNotification<T> completedWithError(@Nullable String error) {
        return new DataStreamNotification<>(Type.COMPLETED_WITH_ERROR, null, error);
    }

    public boolean isOngoing() {
        return type == Type.ONGOING;
    }

    public boolean isOnNext() {
        return type == Type.ON_NEXT;
    }

    public boolean isCompletedWithValue() {
        return type == Type.COMPLETED_WITH_VALUE;
    }

    public boolean isCompletedWithoutValue() {
        return type == Type.COMPLETED_WITHOUT_VALUE;
    }

    public boolean isCompletedWithError() {
        return type == Type.COMPLETED_WITH_ERROR;
    }

    public boolean isCompletedWithSuccess() {
        return isCompletedWithValue() || isCompletedWithoutValue();
    }

    public boolean isCompleted() {
        return isCompletedWithSuccess() || isCompletedWithError();
    }

    @Override
    public String toString() {
        return "DataStreamNotification(" + type + ", " + value + ")";
    }
}
