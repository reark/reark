import com.timotuominen.app.utils.SubscriptionManager;

import junit.framework.TestCase;

import rx.Subscription;
import static org.mockito.Mockito.*;


/**
 * Created by ttuo on 12/25/13.
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
        subscriptionManager.unsubscribe();
        verify(subscriptionA).unsubscribe();
        verify(subscriptionB).unsubscribe();
    }

    public void testUnsubscribeOnlyOnce() {
        Subscription subscriptionA = mock(Subscription.class);
        subscriptionManager.add(subscriptionA);
        subscriptionManager.add(subscriptionA);
        subscriptionManager.unsubscribe();
        verify(subscriptionA).unsubscribe();
        verifyNoMoreInteractions(subscriptionA);
    }
}
