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

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.pojo.NetworkRequestStatus;
import rx.Observable;
import rx.functions.Func1;

public class DataLayerUtils {
    private DataLayerUtils() {
    }

    @NonNull
    public static<T> Observable<DataStreamNotification<T>> createDataStreamNotificationObservable(
            @NonNull Observable<NetworkRequestStatus> networkRequestStatusObservable,
            @NonNull Observable<T> valueObservable) {

        final Observable<DataStreamNotification<T>> networkStatusStream =
                networkRequestStatusObservable
                        .map(DataLayerUtils.<T>fromNetworkRequestStatus())
                        .filter(dataStreamNotification -> dataStreamNotification != null);

        final Observable<DataStreamNotification<T>> valueStream =
                valueObservable.map(DataStreamNotification::onNext);

        return Observable.merge(networkStatusStream, valueStream);
    }

    public static<T> Func1<NetworkRequestStatus, DataStreamNotification<T>> fromNetworkRequestStatus() {
        return networkRequestStatus -> {
            if (networkRequestStatus.isOngoing()) {
                return DataStreamNotification.fetchingStart();
            } else if (networkRequestStatus.isCompleted()) {
                return DataStreamNotification.fetchingCompleted();
            } else if (networkRequestStatus.isError()) {
                return DataStreamNotification.fetchingError();
            } else {
                return null;
            }
        };
    }
}
