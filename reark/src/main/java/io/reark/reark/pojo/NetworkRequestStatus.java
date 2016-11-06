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

import io.reark.reark.utils.Preconditions;

import static io.reark.reark.pojo.NetworkRequestStatus.Status.NETWORK_STATUS_COMPLETED;
import static io.reark.reark.pojo.NetworkRequestStatus.Status.NETWORK_STATUS_ERROR;
import static io.reark.reark.pojo.NetworkRequestStatus.Status.NETWORK_STATUS_NONE;
import static io.reark.reark.pojo.NetworkRequestStatus.Status.NETWORK_STATUS_ONGOING;
import static io.reark.reark.utils.Preconditions.get;

public final class NetworkRequestStatus {

    @NonNull
    private final String uri;

    @NonNull
    private final Status status;

    private final int errorCode;

    @Nullable
    private final String errorMessage;

    public enum Status {
        NETWORK_STATUS_NONE("networkStatusNone"),
        NETWORK_STATUS_ONGOING("networkStatusOngoing"),
        NETWORK_STATUS_COMPLETED("networkStatusCompleted"),
        NETWORK_STATUS_ERROR("networkStatusError");

        private final String status;

        Status(@NonNull final String value) {
            status = value;
        }

        String getStatus() {
            return status;
        }
    }

    private NetworkRequestStatus(@NonNull final String uri,
                                 @NonNull final Status status,
                                 int errorCode,
                                 @Nullable final String errorMessage) {
        this.uri = uri;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @NonNull
    public static NetworkRequestStatus none() {
        return new NetworkRequestStatus("", NETWORK_STATUS_NONE, 0, null);
    }

    @NonNull
    public static NetworkRequestStatus ongoing(@NonNull final String uri) {
        return new NetworkRequestStatus(get(uri), NETWORK_STATUS_ONGOING, 0, null);
    }

    @NonNull
    public static NetworkRequestStatus error(@NonNull final String uri, int errorCode, @Nullable final String errorMessage) {
        return new NetworkRequestStatus(get(uri), NETWORK_STATUS_ERROR, errorCode, errorMessage);
    }

    @NonNull
    public static NetworkRequestStatus completed(@NonNull final String uri) {
        return new NetworkRequestStatus(get(uri), NETWORK_STATUS_COMPLETED, 0, null);
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

    public boolean isNone() {
        return status == NETWORK_STATUS_NONE;
    }

    public boolean isOngoing() {
        return status == NETWORK_STATUS_ONGOING;
    }

    public boolean isError() {
        return status == NETWORK_STATUS_ERROR;
    }

    public boolean isCompleted() {
        return status == NETWORK_STATUS_COMPLETED;
    }

    @Override
    public String toString() {
        return "NetworkRequestStatus(" + uri + ", " + status + ")";
    }
}
