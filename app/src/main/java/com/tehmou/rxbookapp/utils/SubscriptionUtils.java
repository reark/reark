package com.tehmou.rxbookapp.utils;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.TextView;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.internal.Preconditions;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ttuo on 13/06/14.
 */
public class SubscriptionUtils {
    private SubscriptionUtils() { }

    static public Subscription subscribeTextViewText(@NonNull final Observable<String> observable,
                                                     @NonNull final TextView textView) {
        return subscribeTextViewText(observable, textView, AndroidSchedulers.mainThread());
    }
    static public Subscription subscribeTextViewText(@NonNull final Observable<String> observable,
                                                     @NonNull final TextView textView,
                                                     @NonNull Scheduler scheduler) {
        Preconditions.checkNotNull(observable, "Observable cannot be null.");
        Preconditions.checkNotNull(textView, "TextView cannot be null.");
        Preconditions.checkNotNull(scheduler, "Scheduler cannot be null.");

        return observable
                .observeOn(scheduler)
                .subscribe(textView::setText,
                        error -> {
                            textView.setText(error.toString());
                            textView.setBackgroundColor(Color.RED);
                        }
                );
    }

}
