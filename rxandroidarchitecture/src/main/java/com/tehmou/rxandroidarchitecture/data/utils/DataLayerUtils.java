package com.tehmou.rxandroidarchitecture.data.utils;

import com.tehmou.rxandroidarchitecture.data.DataStreamNotification;
import com.tehmou.rxandroidarchitecture.pojo.NetworkRequestStatus;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by ttuo on 06/05/15.
 */
public class DataLayerUtils {
    private DataLayerUtils() {

    }

    @NonNull
    public static<T> Observable<DataStreamNotification<T>> createDataStreamNotificationObservable(
            @NonNull Observable<NetworkRequestStatus> networkRequestStatusObservable,
            @NonNull Observable<T> valueObservable) {
        final Observable<DataStreamNotification<T>> networkStatusStream =
                networkRequestStatusObservable
                        .filter(networkRequestStatus ->
                                !networkRequestStatus.isCompleted())
                        .map(new Func1<NetworkRequestStatus, DataStreamNotification<T>>() {
                            @Override
                            public DataStreamNotification<T> call(NetworkRequestStatus networkRequestStatus) {
                                if (networkRequestStatus.isError()) {
                                    return DataStreamNotification.fetchingError();
                                } else if (networkRequestStatus.isOngoing()) {
                                    return DataStreamNotification.fetchingStart();
                                } else {
                                    return null;
                                }
                            }
                        })
                        .filter(dataStreamNotification -> dataStreamNotification != null);
        final Observable<DataStreamNotification<T>> valueStream =
                valueObservable.map(DataStreamNotification::onNext);
        return Observable.merge(networkStatusStream, valueStream);
    }
}
