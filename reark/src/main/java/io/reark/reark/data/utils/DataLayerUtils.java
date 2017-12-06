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
package io.reark.reark.data.utils;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.pojo.NetworkRequestStatus;

import static io.reark.reark.utils.Preconditions.checkNotNull;

public final class DataLayerUtils {

    private DataLayerUtils() {
    }

    @NonNull
    public static<T> Observable<DataStreamNotification<T>> createDataStreamNotificationObservable(
            @NonNull final Observable<NetworkRequestStatus> networkRequestStatusObservable,
            @NonNull final Observable<T> valueObservable) {

        final Observable<DataStreamNotification<T>> networkStatusStream =
                networkRequestStatusObservable
                        .map(DataLayerUtils.<T>fromNetworkRequestStatus());

        final Observable<DataStreamNotification<T>> valueStream =
                valueObservable.map(DataStreamNotification::onNext);

        return Observable.merge(networkStatusStream, valueStream);
    }

    private static<T> Function<NetworkRequestStatus, DataStreamNotification<T>> fromNetworkRequestStatus() {
        return networkRequestStatus -> {
            checkNotNull(networkRequestStatus);

            switch (networkRequestStatus.getStatus()) {
                case ONGOING:
                    return DataStreamNotification.ongoing();
                case COMPLETED_WITH_VALUE:
                    return DataStreamNotification.completedWithValue();
                case COMPLETED_WITHOUT_VALUE:
                    return DataStreamNotification.completedWithoutValue();
                case COMPLETED_WITH_ERROR:
                    return DataStreamNotification.completedWithError(networkRequestStatus.getErrorMessage());
                case NONE:
                default:
                    throw new IllegalStateException("Unexpected network status " + networkRequestStatus);
            }
        };
    }
}
