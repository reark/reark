import com.tehmou.rxbookapp.utils.SubscriptionManager;

import junit.framework.TestCase;

import rx.Subscription;
import static org.mockito.Mockito.*;


/**
 * Created by tehmou on 12/25/13.
 */
public class SubscriptionManagerTest extends TestCase {
    private SubscriptionManager subscriptionManager;

    @Override
    protected void setUp() throws Exception {
        subscriptionManager = new SubscriptionManager();
    }

    public void testUnsubscribe() {
        Subscription subscriptionA = mock(Subscription.class);
        Subscription subscriptionB = mock(Subscription.class);
        subscriptionManager.add(subscriptionA);
        subscriptionManager.add(subscriptionB);
        subscriptionManager.unsubscribeAll();
        verify(subscriptionA).unsubscribe();
        verify(subscriptionB).unsubscribe();
    }

    public void testUnsubscribeOnlyOnce() {
        Subscription subscriptionA = mock(Subscription.class);
        subscriptionManager.add(subscriptionA);
        subscriptionManager.add(subscriptionA);
        subscriptionManager.unsubscribeAll();
        verify(subscriptionA).unsubscribe();
    }
}
