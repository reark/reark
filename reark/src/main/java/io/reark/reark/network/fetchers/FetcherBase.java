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
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.pojo.NetworkRequestStatus.Builder;
import io.reark.reark.utils.Log;
import retrofit2.adapter.rxjava.HttpException;
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
    private final Map<Integer, Pair<Subscription, List<Integer>>> requestMap = new ConcurrentHashMap<>();

    protected FetcherBase(@NonNull final Action1<NetworkRequestStatus> updateNetworkRequestStatus) {
        this.updateNetworkRequestStatus = get(updateNetworkRequestStatus);
    }

    protected void startRequest(int requestId, int listenerId, @NonNull final String uri) {
        checkNotNull(uri);
        Log.v(TAG, "startRequest(" + uri + ")");

        updateNetworkRequestStatus.call(new Builder()
                .uri(uri)
                .listeners(createListener(listenerId))
                .ongoing()
                .build());
    }

    protected void errorRequest(int requestId, @NonNull final String uri, int errorCode, @Nullable final String errorMessage) {
        checkNotNull(uri);
        Log.v(TAG, String.format("errorRequest(%s, %s, %s)", uri, errorCode, errorMessage));

        updateNetworkRequestStatus.call(new Builder()
                .uri(uri)
                .listeners(getListeners(requestId))
                .error()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build());
    }

    protected void completeRequest(int requestId, @NonNull final String uri) {
        checkNotNull(uri);
        Log.v(TAG, "completeRequest(" + uri + ")");

        updateNetworkRequestStatus.call(new Builder()
                .uri(uri)
                .listeners(getListeners(requestId))
                .completed()
                .build());
    }

    protected boolean isOngoingRequest(int requestId) {
        Log.v(TAG, "isOngoingRequest(" + requestId + ")");

        return requestMap.containsKey(requestId)
                && !requestMap.get(requestId).first.isUnsubscribed();
    }

    protected void addListener(int requestId, int listenerId) {
        Log.v(TAG, String.format("addListener(%s, %s)", requestId, listenerId));

        requestMap.get(requestId).second.add(listenerId);
    }

    protected void addRequest(int requestId, int listenerId, @NonNull final Subscription subscription) {
        checkNotNull(subscription);
        Log.v(TAG, String.format("addRequest(%s, %s)", requestId, listenerId));

        requestMap.put(requestId, new Pair<>(subscription, createListener(listenerId)));
    }

    @NonNull
    private List<Integer> getListeners(int requestId) {
        return requestMap.get(requestId).second;
    }

    @NonNull
    private static ArrayList<Integer> createListener(int listenerId) {
        return new ArrayList<Integer>() {{ add(listenerId); }};
    }

    @NonNull
    public Action1<Throwable> doOnError(int requestId, @NonNull final String uri) {
        checkNotNull(uri);

        return throwable -> {
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                int statusCode = httpException.code();
                errorRequest(requestId, uri, statusCode, httpException.getMessage());
            } else {
                Log.e(TAG, "The error was not a RetroFitError");
                errorRequest(requestId, uri, NO_ERROR_CODE, null);
            }
        };
    }

}
