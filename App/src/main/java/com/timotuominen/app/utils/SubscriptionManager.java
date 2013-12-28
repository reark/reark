package com.timotuominen.app.utils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by tehmou on 12/25/13.
 */
public class SubscriptionManager {
    final private List<Subscription> subscriptions = new ArrayList<Subscription>();

    public SubscriptionManager() {

    }

    public void add(Subscription subscription) {
        if (!subscriptions.contains(subscription)) {
            subscriptions.add(subscription);
        }
    }

    public void unsubscribe() {
        for (Subscription subscription : subscriptions) {
            subscription.unsubscribe();
        }
        subscriptions.clear();
    }
}
