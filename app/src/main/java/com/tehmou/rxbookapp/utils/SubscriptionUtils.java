package com.tehmou.rxbookapp.utils;

import android.graphics.Color;
import android.widget.TextView;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ttuo on 13/06/14.
 */
public class SubscriptionUtils {
    private SubscriptionUtils() { }

    static public Subscription subscribeTextViewText(final Observable<String> observable,
                                                     final TextView textView) {
        return subscribeTextViewText(observable, textView, AndroidSchedulers.mainThread());
    }
    static public Subscription subscribeTextViewText(final Observable<String> observable,
                                                     final TextView textView,
                                                     Scheduler scheduler) {
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
