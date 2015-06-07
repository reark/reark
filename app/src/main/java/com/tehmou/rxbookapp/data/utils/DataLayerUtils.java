package com.tehmou.rxbookapp.data.utils;

import com.tehmou.rxbookapp.data.DataStreamNotification;
import com.tehmou.rxbookapp.pojo.NetworkRequestStatus;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by ttuo on 06/05/15.
 */
public class DataLayerUtils {
    private DataLayerUtils() {

    }

    public static<T> Observable<DataStreamNotification<T>> createDataStreamNotificationObservable(
            Observable<NetworkRequestStatus> networkRequestStatusObservable,
            Observable<T> valueObservable) {
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
