package com.tehmou.rxbookapp.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.internal.Preconditions;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ttuo on 02/08/14.
 */
public class RxBinderUtil {
    final static private String TAG = RxBinderUtil.class.getCanonicalName();

    @NonNull
    final private String tag;
    final private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public RxBinderUtil(@NonNull Object target) {
        Preconditions.checkNotNull(target, "Target cannot be null.");

        this.tag = target.getClass().getCanonicalName();
    }

    public void clear() {
        compositeSubscription.clear();
    }

    public <U> void bindProperty(@NonNull final Observable<U> observable,
                                 @NonNull final Action1<U> setter) {
        compositeSubscription.add(
                subscribeSetter(observable, setter, tag));
    }

    static private <U> Subscription subscribeSetter(@NonNull final Observable<U> observable,
                                                    @NonNull final Action1<U> setter,
                                                    @NonNull final String tag) {
        Preconditions.checkNotNull(observable, "Observable cannot be null.");

        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SetterSubscriber<>(setter, tag));
    }

    static private class SetterSubscriber<U> extends Subscriber<U> {
        final static private String TAG = SetterSubscriber.class.getCanonicalName();

        @NonNull
        final private Action1<U> setter;

        @NonNull
        final private String tag;

        public SetterSubscriber(@NonNull final Action1<U> setter, @NonNull final String tag) {
            Preconditions.checkNotNull(setter, "Setter cannot be null.");
            Preconditions.checkNotNull(tag, "Tag cannot be null.");

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
