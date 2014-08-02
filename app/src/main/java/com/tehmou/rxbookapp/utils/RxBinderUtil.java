package com.tehmou.rxbookapp.utils;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by ttuo on 02/08/14.
 */
public class RxBinderUtil {
    final static private String TAG = RxBinderUtil.class.getCanonicalName();

    final private String tag;

    public RxBinderUtil(Object target) {
        this.tag = target.getClass().getCanonicalName();
    }

    public <U> void bindProperty(Observable<U> observable,
                                 final Action1<U> setter) {
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Subscriber<U>() {
                            @Override
                            public void onCompleted() {
                                Log.v(TAG, tag + "." + "onCompleted");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, tag + "." + "onError", e);
                            }

                            @Override
                            public void onNext(U u) {
                                setter.call(u);
                            }
                        }
                );
    }
}
