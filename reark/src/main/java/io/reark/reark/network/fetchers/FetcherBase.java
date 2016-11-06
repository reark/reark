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
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import retrofit.RetrofitError;
import rx.Subscription;
import rx.functions.Action1;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public abstract class FetcherBase<T> implements Fetcher<T> {
    private static final String TAG = FetcherBase.class.getSimpleName();

    private static final int NO_ERROR_CODE = -1;

    @NonNull
    private final Action1<NetworkRequestStatus> updateNetworkRequestStatus;

    @NonNull
    private final Map<Integer, Subscription> requestMap = new ConcurrentHashMap<>();

    protected FetcherBase(@NonNull final Action1<NetworkRequestStatus> updateNetworkRequestStatus) {
        this.updateNetworkRequestStatus = get(updateNetworkRequestStatus);
    }

    protected void startRequest(@NonNull final String uri) {
        checkNotNull(uri);

        Log.v(TAG, "startRequest(" + uri + ")");
        updateNetworkRequestStatus.call(NetworkRequestStatus.ongoing(uri));
    }

    protected void errorRequest(@NonNull final String uri, int errorCode, @Nullable final String errorMessage) {
        checkNotNull(uri);

        Log.v(TAG, "errorRequest(" + uri + ", " + errorCode + ", " + errorMessage + ")");
        updateNetworkRequestStatus.call(NetworkRequestStatus.error(uri, errorCode, errorMessage));
    }

    protected void completeRequest(@NonNull final String uri) {
        checkNotNull(uri);

        Log.v(TAG, "completeRequest(" + uri + ")");
        updateNetworkRequestStatus.call(NetworkRequestStatus.completed(uri));
    }

    protected boolean isOngoingRequest(int requestId) {
        Log.v(TAG, "isOngoingRequest(" + requestId + ")");

        return requestMap.containsKey(requestId)
                && !requestMap.get(requestId).isUnsubscribed();
    }

    protected void addRequest(int requestId, @NonNull final Subscription subscription) {
        Log.v(TAG, "addRequest(" + requestId + ")");

        requestMap.put(requestId, get(subscription));
    }

    @NonNull
    public Action1<Throwable> doOnError(@NonNull final String uri) {
        checkNotNull(uri);

        return throwable -> {
            if (throwable instanceof RetrofitError) {
                RetrofitError retrofitError = (RetrofitError) throwable;
                errorRequest(uri, getStatusCode(retrofitError), retrofitError.getMessage());
            } else {
                Log.e(TAG, "The error was not a RetroFitError");
                errorRequest(uri, NO_ERROR_CODE, null);
            }
        };
    }

    private static int getStatusCode(@NonNull final RetrofitError retrofitError) {
        return retrofitError.getResponse() != null
                ? retrofitError.getResponse().getStatus()
                : NO_ERROR_CODE;
    }
}
