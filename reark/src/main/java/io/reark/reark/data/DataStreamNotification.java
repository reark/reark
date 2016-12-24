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

    private static final int DEFAULT_CODE = 0;

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
    private final String errorBody;

    private int httpCode = DEFAULT_CODE;

    @Nullable
    private String errorMessage = "";

    private DataStreamNotification(@NonNull Type type, @Nullable T value, @Nullable String error) {

        this.type = get(type);
        this.value = value;
        this.errorBody = error;
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
    public String getErrorBody() {
        return errorBody;
    }

    public int getHttpCode() {
        return httpCode;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    @NonNull
    public static <T> DataStreamNotification<T> fetchingStart() {
        return new DataStreamNotification<>(Type.FETCHING_START, null, null);
    }

    @NonNull
    public static <T> DataStreamNotification<T> onNext(T value) {
        DataStreamNotification<T> data = new DataStreamNotification<>(Type.ON_NEXT, value, null);
        data.httpCode = 200;
        return data;
    }

    @NonNull
    public static <T> DataStreamNotification<T> fetchingCompleted() {
        return new DataStreamNotification<>(Type.FETCHING_COMPLETED, null, null);
    }

    @NonNull
    public static <T> DataStreamNotification<T> fetchingError(int errorCode, String errorMessage, @Nullable String error) {
        DataStreamNotification<T> data = new DataStreamNotification<>(Type.FETCHING_ERROR, null, error);
        data.httpCode = errorCode;
        data.errorMessage = errorMessage;
        return data;
    }

    public boolean isFetchingStart() {
        return type == Type.FETCHING_START;
    }

    public boolean isOnNext() {
        return type == Type.ON_NEXT;
    }

    public boolean isFetchingCompleted() {
        return type == Type.FETCHING_COMPLETED;
    }

    public boolean isFetchingError() {
        return type == Type.FETCHING_ERROR;
    }

    @Override
    public String toString() {
        return "DataStreamNotification(" + type + ", " + value + ")";
    }
}
