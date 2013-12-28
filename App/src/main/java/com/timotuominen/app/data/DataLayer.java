package com.timotuominen.app.data;

import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * Created by tehmou on 12/25/13.
 */
public class DataLayer {
    private static DataLayer instance = null;

    private DataLayer () {

    }

    public static DataLayer getInstance() {
        if (instance == null) {
            instance = new DataLayer();
        }
        return instance;
    }

    public Observable<Long> getIntervalStream() {
        return Observable.interval(1, TimeUnit.SECONDS);
    }
}
