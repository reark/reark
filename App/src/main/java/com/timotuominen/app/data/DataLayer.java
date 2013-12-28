package com.timotuominen.app.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.util.functions.Action1;

/**
 * Created by tehmou on 12/25/13.
 */
public class DataLayer {
    private static DataLayer instance = null;

    final private List<Long> intervalNumberCache = new ArrayList<Long>();
    final private Subject<Long, Long> intervalNumberSubject = PublishSubject.create();

    private DataLayer () {
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(intervalNumberSubject);
        intervalNumberSubject.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                intervalNumberCache.add(aLong);
            }
        });
    }

    public static DataLayer getInstance() {
        if (instance == null) {
            instance = new DataLayer();
        }
        return instance;
    }

    public Observable<Long> getIntervalNumberStream() {
        if (intervalNumberCache.isEmpty()) {
            return intervalNumberSubject;
        } else {
            return Observable.merge(
                    Observable.from(intervalNumberCache).last(),
                    intervalNumberSubject);
        }
    }
}
