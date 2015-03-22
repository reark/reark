package com.tehmou.rxbookapp.utils;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ttuo on 02/08/14.
 */
public class RxBinderUtil {
    final static private String TAG = RxBinderUtil.class.getCanonicalName();

    final private String tag;
    final private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public RxBinderUtil(Object target) {
        this.tag = target.getClass().getCanonicalName();
    }

    public void clear() {
        compositeSubscription.clear();
    }

    public <U> void bindProperty(final Observable<U> observable,
                                 final Action1<U> setter) {
        compositeSubscription.add(
                subscribeSetter(observable, setter, tag));
    }

    static private <U> Subscription subscribeSetter(final Observable<U> observable,
                                                    final Action1<U> setter,
                                                    final String tag) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SetterSubscriber<U>(setter, tag));
    }

    static private class SetterSubscriber<U> extends Subscriber<U> {
        final static private String TAG = SetterSubscriber.class.getCanonicalName();

        final private Action1<U> setter;
        final private String tag;

        public SetterSubscriber(final Action1<U> setter,
                                    final String tag) {
            this.setter = setter;
            this.tag = tag;
        }

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
}
