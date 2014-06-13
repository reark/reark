import junit.framework.TestCase;

import java.lang.Exception;
import java.lang.Override;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static org.mockito.Mockito.*;


/**
 * Created by tehmou on 12/25/13.
 */
public class SubscriptionManagerTest extends TestCase {
    private CompositeSubscription subscriptionManager;

    @Override
    protected void setUp() throws Exception {
        subscriptionManager = new CompositeSubscription();
    }

    public void testUnsubscribe() {
        Subscription subscriptionA = mock(Subscription.class);
        Subscription subscriptionB = mock(Subscription.class);
        subscriptionManager.add(subscriptionA);
        subscriptionManager.add(subscriptionB);
        subscriptionManager.unsubscribe();
        verify(subscriptionA).unsubscribe();
        verify(subscriptionB).unsubscribe();
    }

    public void testUnsubscribeOnlyOnce() {
        Subscription subscriptionA = new TestSubscription();
        subscriptionManager.add(subscriptionA);
        subscriptionManager.add(subscriptionA);
        subscriptionManager.unsubscribe();
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
