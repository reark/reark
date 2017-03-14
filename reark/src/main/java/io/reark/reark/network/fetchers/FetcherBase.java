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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.ObjectLockHandler;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.functions.Action1;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

/**
 * Base class for Fetchers. The class implements tracking of request listeners and duplicate
 * requests.
 *
 * @param <T> Type of the Service Uri used by the application.
 */
public abstract class FetcherBase<T> implements Fetcher<T> {

    private static final String TAG = FetcherBase.class.getSimpleName();

    private static final int NO_ERROR_CODE = -1;

    @NonNull
    private final Action1<NetworkRequestStatus> updateNetworkRequestStatus;

    @NonNull
    private final Map<Integer, Pair<Subscription, Set<Integer>>> requestMap = new ConcurrentHashMap<>(20, 0.75f, 2);

    @NonNull
    private final ObjectLockHandler<Integer> locker = new ObjectLockHandler<>();

    protected FetcherBase(@NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus) {
        this.updateNetworkRequestStatus = get(updateNetworkRequestStatus);
    }

    protected void startRequest(int requestId, int listenerId, @NonNull String uri) {
        Log.v(TAG, String.format("startRequest(%s, %s, %s)", requestId, listenerId, get(uri)));

        lock(requestId);

        // Fetcher calls startRequest before addRequest, so at this point the listenerId is not yet
        // in the list of listeners for the request Subscription and we must use `createListeners`
        // instead of `getListeners`. A newly created request can in any case only have one active
        // listener (the one being created), so the outcome isn't affected.
        updateNetworkRequestStatus.call(new NetworkRequestStatus.Builder()
                .uri(uri)
                .listeners(createListener(listenerId))
                .ongoing()
                .build());

        release(requestId);
    }

    protected void completeRequest(int requestId, @NonNull String uri) {
        Log.v(TAG, String.format("completeRequest(%s, %s)", requestId, get(uri)));

        lock(requestId);

        updateNetworkRequestStatus.call(new NetworkRequestStatus.Builder()
                .uri(uri)
                .listeners(getListeners(requestId))
                .completed()
                .build());

        release(requestId);
    }

    protected void errorRequest(int requestId, @NonNull String uri, int errorCode, @Nullable String errorMessage) {
        Log.v(TAG, String.format("errorRequest(%s, %s, %s, %s)", requestId, get(uri), errorCode, errorMessage));

        lock(requestId);

        updateNetworkRequestStatus.call(new NetworkRequestStatus.Builder()
                .uri(uri)
                .listeners(getListeners(requestId))
                .error()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build());

        release(requestId);
    }

    protected void addRequest(int requestId, int listenerId, @NonNull Subscription subscription) {
        Log.v(TAG, String.format("addRequest(%s, %s)", requestId, listenerId));

        lock(requestId);

        Set<Integer> listeners = createListener(listenerId);

        if (requestMap.containsKey(requestId)) {
            Pair<Subscription, Set<Integer>> oldRequest = requestMap.remove(requestId);

            if (!oldRequest.first.isUnsubscribed()) {
                Log.w(TAG, "Unexpected subscribed ");
                oldRequest.first.unsubscribe();
            }

            // Old listeners are still potentially interested in new fetches
            listeners.addAll(oldRequest.second);
        }

        requestMap.put(requestId, new Pair<>(get(subscription), listeners));

        release(requestId);
    }

    protected void addListener(int requestId, int listenerId) {
        Log.v(TAG, String.format("addListener(%s, %s)", requestId, listenerId));

        lock(requestId);

        requestMap.get(requestId).second.add(listenerId);

        release(requestId);
    }

    protected boolean isOngoingRequest(int requestId) {
        Log.v(TAG, String.format("isOngoingRequest(%s)", requestId));

        lock(requestId);

        boolean isOngoing = requestMap.containsKey(requestId)
                && !requestMap.get(requestId).first.isUnsubscribed();

        release(requestId);

        return isOngoing;
    }

    @NonNull
    private Set<Integer> getListeners(int requestId) {
        return requestMap.get(requestId).second;
    }

    @NonNull
    private static Set<Integer> createListener(int listenerId) {
        Set<Integer> set = new HashSet<>(1);
        set.add(listenerId);
        return set;
    }

    @NonNull
    protected Action1<Throwable> doOnError(int requestId, @NonNull String uri) {
        checkNotNull(uri);

        return throwable -> {
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                int statusCode = httpException.code();
                errorRequest(requestId, uri, statusCode, httpException.getMessage());
            } else {
                Log.e(TAG, "The error was not a RetrofitError");
                errorRequest(requestId, uri, NO_ERROR_CODE, null);
            }
        };
    }

    private void lock(int id) {
        try {
            locker.acquire(id);
        } catch (InterruptedException e) {
            Log.e(TAG, "Lock acquisition failed!", e);
        }
    }

    private void release(int id) {
        locker.release(id);
    }

}
