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

import static io.reark.reark.pojo.NetworkRequestStatus.Status.NETWORK_STATUS_COMPLETED;
import static io.reark.reark.pojo.NetworkRequestStatus.Status.NETWORK_STATUS_ERROR;
import static io.reark.reark.pojo.NetworkRequestStatus.Status.NETWORK_STATUS_ONGOING;

public final class NetworkRequestStatus {

    private final String uri;
    private final Status status;
    private final int errorCode;
    private final String errorMessage;

    public enum Status {
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

    private NetworkRequestStatus(String uri,
                                 Status status,
                                 int errorCode,
                                 String errorMessage) {
        this.uri = uri;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static NetworkRequestStatus ongoing(String uri) {
        return new NetworkRequestStatus(uri, NETWORK_STATUS_ONGOING, 0, null);
    }

    public static NetworkRequestStatus error(String uri, int errorCode, String errorMessage) {
        return new NetworkRequestStatus(uri, NETWORK_STATUS_ERROR, errorCode, errorMessage);
    }

    public static NetworkRequestStatus completed(String uri) {
        return new NetworkRequestStatus(uri, NETWORK_STATUS_COMPLETED, 0, null);
    }

    public String getUri() {
        return uri;
    }

    public Status getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
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
