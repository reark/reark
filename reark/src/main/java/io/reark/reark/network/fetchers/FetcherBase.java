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
package io.reark.reark.network.fetchers;

import android.support.annotation.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.functions.Action1;

abstract public class FetcherBase implements Fetcher {
    private static final String TAG = FetcherBase.class.getSimpleName();

    public static final int NO_ERROR_CODE = -1;

    private final Action1<NetworkRequestStatus> updateNetworkRequestStatus;
    protected final Map<Integer, Subscription> requestMap = new ConcurrentHashMap<>();

    public FetcherBase(@NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus) {
        Preconditions.checkNotNull(updateNetworkRequestStatus, "Update Network Status cannot be null.");

        this.updateNetworkRequestStatus = updateNetworkRequestStatus;
    }

    protected void startRequest(@NonNull String uri) {
        Preconditions.checkNotNull(uri, "URI cannot be null.");

        Log.v(TAG, "startRequest(" + uri + ")");
        updateNetworkRequestStatus.call(NetworkRequestStatus.ongoing(uri));
    }

    protected void errorRequest(@NonNull String uri, int errorCode, String errorMessage) {
        Preconditions.checkNotNull(uri, "URI cannot be null.");

        Log.v(TAG, "errorRequest(" + uri + ", " + errorCode + ", " + errorMessage + ")");
        updateNetworkRequestStatus.call(NetworkRequestStatus.error(uri, errorCode, errorMessage));
    }

    protected void completeRequest(@NonNull String uri) {
        Preconditions.checkNotNull(uri, "URI cannot be null.");

        Log.v(TAG, "completeRequest(" + uri + ")");
        updateNetworkRequestStatus.call(NetworkRequestStatus.completed(uri));
    }

    @NonNull
    public Action1<Throwable> doOnError(@NonNull final String uri) {
        Preconditions.checkNotNull(uri, "URI cannot be null.");

        return throwable -> {
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                int statusCode = httpException.code();
                errorRequest(uri, statusCode, httpException.getMessage());
            } else {
                Log.e(TAG, "The error was not a RetroFitError");
                errorRequest(uri, NO_ERROR_CODE, null);
            }
        };
    }
}
