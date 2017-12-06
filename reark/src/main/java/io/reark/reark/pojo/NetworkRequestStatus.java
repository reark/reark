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
package io.reark.reark.pojo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reark.reark.utils.Log;

import static io.reark.reark.pojo.NetworkRequestStatus.Status.COMPLETED_WITHOUT_VALUE;
import static io.reark.reark.pojo.NetworkRequestStatus.Status.COMPLETED_WITH_ERROR;
import static io.reark.reark.pojo.NetworkRequestStatus.Status.COMPLETED_WITH_VALUE;
import static io.reark.reark.pojo.NetworkRequestStatus.Status.NONE;
import static io.reark.reark.pojo.NetworkRequestStatus.Status.ONGOING;
import static io.reark.reark.utils.Preconditions.get;

/**
 * Class representing the status of a network request.
 */
public final class NetworkRequestStatus {

    private static final String TAG = NetworkRequestStatus.class.getSimpleName();

    private static final NetworkRequestStatus NO_STATUS =
            new NetworkRequestStatus(Collections.emptySet(), "", NONE, 0, null);

    @NonNull
    private final String uri;

    @NonNull
    private final Status status;

    @NonNull
    private final List<Integer> listeners = new ArrayList<>(1);

    private final int errorCode;

    @Nullable
    private final String errorMessage;

    public enum Status {
        NONE("none"),
        ONGOING("ongoing"),
        COMPLETED_WITH_VALUE("completedWithValue"),
        COMPLETED_WITHOUT_VALUE("completedWithoutValue"),
        COMPLETED_WITH_ERROR("completedWithError");

        private final String status;

        Status(@NonNull String value) {
            status = value;
        }

        String getStatus() {
            return status;
        }
    }

    private NetworkRequestStatus(@NonNull Set<Integer> listeners,
                                 @NonNull String uri,
                                 @NonNull Status status,
                                 int errorCode,
                                 @Nullable String errorMessage) {
        this.listeners.addAll(get(listeners));
        this.uri = uri;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @NonNull
    public static NetworkRequestStatus none() {
        return NO_STATUS;
    }

    @NonNull
    public String getUri() {
        return uri;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean forListener(@Nullable Integer listenerId) {
        return listenerId == null || listeners.contains(listenerId);
    }

    public boolean isSome() {
        return status != NONE;
    }

    public boolean isNone() {
        return status == NONE;
    }

    public boolean isOngoing() {
        return status == ONGOING;
    }

    public boolean isCompletedWithValue() {
        return status == COMPLETED_WITH_VALUE;
    }

    public boolean isCompletedWithoutValue() {
        return status == COMPLETED_WITHOUT_VALUE;
    }

    public boolean isCompletedWithError() {
        return status == COMPLETED_WITH_ERROR;
    }

    @Override
    public String toString() {
        return "NetworkRequestStatus(" + uri + ", " + status + ")";
    }

    /**
     * Builder class for the status.
     */
    public static class Builder {

        private String uri;

        private Status status;

        private final Set<Integer> listeners = new HashSet<>(1);

        private int errorCode;

        private String errorMessage;

        @NonNull
        public Builder ongoing() {
            this.status = ONGOING;
            return this;
        }

        @NonNull
        public Builder error() {
            this.status = COMPLETED_WITH_ERROR;
            return this;
        }

        @NonNull
        public Builder completed(boolean withValue) {
            this.status = withValue ? COMPLETED_WITH_VALUE : COMPLETED_WITHOUT_VALUE;
            return this;
        }

        @NonNull
        public Builder uri(@NonNull String uri) {
            this.uri = get(uri);
            return this;
        }

        @NonNull
        public Builder listeners(@NonNull Set<Integer> listeners) {
            this.listeners.addAll(get(listeners));
            return this;
        }

        @NonNull
        public Builder errorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        @NonNull
        public Builder errorMessage(@Nullable String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        @NonNull
        public NetworkRequestStatus build() {
            verify();

            return new NetworkRequestStatus(listeners, uri, status, errorCode, errorMessage);
        }

        private void verify() {
            if (uri.isEmpty()) {
                Log.w(TAG, "Uri missing!");
            }
            if (listeners.isEmpty()) {
                Log.w(TAG, "Listeners missing!");
            }
        }
    }
}
