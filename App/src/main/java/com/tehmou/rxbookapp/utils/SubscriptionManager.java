package com.tehmou.rxbookapp.utils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by ttuo on 19/03/14.
 */
public class SubscriptionManager {
    final private List<Subscription> subscriptions = new ArrayList<Subscription>();

    public void add(Subscription subscription) {
        subscriptions.add(subscription);
    }

    public void unsubscribeAll() {
        for (Subscription subscription : subscriptions) {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
        subscriptions.clear();
    }
}
