package com.tehmou.rxbookapp.utils;

import android.util.Log;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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
                subscribeSetter(observable, new WeakReference<Action1<U>>(setter), tag));
    }

    static private <U> Subscription subscribeSetter(final Observable<U> observable,
                                                    final WeakReference<Action1<U>> weakSetter,
                                                    final String tag) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new WeakSetterSubscriber<U>(weakSetter, tag));
    }

    static private class WeakSetterSubscriber<U> extends Subscriber<U> {
        final static private String TAG = WeakSetterSubscriber.class.getCanonicalName();

        final private WeakReference<Action1<U>> weakSetter;
        final private String tag;

        public WeakSetterSubscriber(final WeakReference<Action1<U>> weakSetter,
                                    final String tag) {
            this.weakSetter = weakSetter;
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
            if (weakSetter.get() != null) {
                weakSetter.get().call(u);
            }
        }
    }
}
