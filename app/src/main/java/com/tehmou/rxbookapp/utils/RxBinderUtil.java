package com.tehmou.rxbookapp.utils;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by ttuo on 02/08/14.
 */
public class RxBinderUtil<T> {
    final static private String TAG = RxBinderUtil.class.getCanonicalName();

    final private String tag;

    public RxBinderUtil(String tag) {
        this.tag = tag;
    }

    public <U> void bindProperty(Observable<U> observable,
                                 final Func1<U, T> setter,
                                 final String key) {
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Subscriber<U>() {
                            @Override
                            public void onCompleted() {
                                Log.v(TAG, tag + "." + key + "." + "onCompleted");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, tag + "." + key + "." + "onError", e);
                            }

                            @Override
                            public void onNext(U u) {
                                Log.v(TAG, tag + "." + key + "." + "onNext");
                                setter.call(u);
                            }
                        }
                );
    }
}
