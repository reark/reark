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

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import retrofit2.HttpException;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public abstract class FetcherBase<T> implements Fetcher<T> {
    private static final String TAG = FetcherBase.class.getSimpleName();

    private static final int NO_ERROR_CODE = -1;

    @NonNull
    private final Consumer<NetworkRequestStatus> updateNetworkRequestStatus;

    @NonNull
    private final Map<Integer, Disposable> requestMap = new ConcurrentHashMap<>();

    protected FetcherBase(@NonNull final Consumer<NetworkRequestStatus> updateNetworkRequestStatus) {
        this.updateNetworkRequestStatus = get(updateNetworkRequestStatus);
    }

    protected void startRequest(@NonNull final String uri) {
        checkNotNull(uri);

        Log.v(TAG, "startRequest(" + uri + ")");
        try {
            updateNetworkRequestStatus.accept(NetworkRequestStatus.ongoing(uri));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    protected void errorRequest(@NonNull final String uri, int errorCode, @Nullable final String errorMessage) {
        checkNotNull(uri);

        Log.v(TAG, "errorRequest(" + uri + ", " + errorCode + ", " + errorMessage + ")");
        try {
            updateNetworkRequestStatus.accept(NetworkRequestStatus.error(uri, errorCode, errorMessage));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    protected void completeRequest(@NonNull final String uri) {
        checkNotNull(uri);

        Log.v(TAG, "completeRequest(" + uri + ")");
        try {
            updateNetworkRequestStatus.accept(NetworkRequestStatus.completed(uri));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    protected boolean isOngoingRequest(int requestId) {
        Log.v(TAG, "isOngoingRequest(" + requestId + ")");

        return requestMap.containsKey(requestId)
                && !requestMap.get(requestId).isDisposed();
    }

    protected void addRequest(int requestId, @NonNull final Disposable disposable) {
        Log.v(TAG, "addRequest(" + requestId + ")");

        requestMap.put(requestId, get(disposable));
    }

    @NonNull
    public Consumer<Throwable> doOnError(@NonNull final String uri) {
        checkNotNull(uri);

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
