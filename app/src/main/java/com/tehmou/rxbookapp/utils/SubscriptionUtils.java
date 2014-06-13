package com.tehmou.rxbookapp.utils;

import android.widget.TextView;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by ttuo on 13/06/14.
 */
public class SubscriptionUtils {
    private SubscriptionUtils() { }

    static public Subscription subscribeTextViewText(final Observable<String> observable,
                                                     final TextView textView) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        textView.setText(s);
                    }
                });
    }
}
