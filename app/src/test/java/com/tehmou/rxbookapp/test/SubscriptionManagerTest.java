package com.tehmou.rxbookapp.test;

import org.junit.Before;
import org.junit.Test;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by tehmou on 12/25/13.
 */
public class SubscriptionManagerTest {
    private CompositeSubscription subscriptionManager;

    @Before
    public void setUp() throws Exception {
        subscriptionManager = new CompositeSubscription();
    }

    @Test
    public void testUnsubscribe() {
        Subscription subscriptionA = mock(Subscription.class);
        Subscription subscriptionB = mock(Subscription.class);
        subscriptionManager.add(subscriptionA);
        subscriptionManager.add(subscriptionB);

        subscriptionManager.unsubscribe();

        verify(subscriptionA).unsubscribe();
        verify(subscriptionB).unsubscribe();
    }

    @Test
    public void testUnsubscribeOnlyOnce() {
        Subscription subscriptionA = new TestSubscription();
        subscriptionManager.add(subscriptionA);
        subscriptionManager.add(subscriptionA);

        subscriptionManager.unsubscribe();

        assertTrue(subscriptionManager.isUnsubscribed());
    }

    static class TestSubscription implements Subscription {
        private boolean isSubscribed = true;

        @Override
        public void unsubscribe() {
            if (!isSubscribed) {
                fail("unsubscribed when not subscribed");
            }
            isSubscribed = false;
        }

        @Override
        public boolean isUnsubscribed() {
            return !isSubscribed;
        }
    }
}
