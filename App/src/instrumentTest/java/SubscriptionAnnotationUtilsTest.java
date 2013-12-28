import com.timotuominen.app.utils.SubscriptionAnnotationUtils;
import com.timotuominen.app.utils.Unsubscribable;

import junit.framework.TestCase;


/**
 * Created by tehmou on 12/25/13.
 */
public class SubscriptionAnnotationUtilsTest extends TestCase {
    public void testUnsubscribeAllAnnotated() {
        AnnotationTest annotationTest = new AnnotationTest();
        SubscriptionAnnotationUtils.unsubscribeAllAnnotated(annotationTest);
        assertEquals(true, annotationTest.testVariable1.unsubscribed);
        assertEquals(true, annotationTest.testVariable2.unsubscribed);
        assertEquals(false, annotationTest.testVariable3.unsubscribed);
    }

    public static class AnnotationTest {
        public AnnotationTest() { }

        @Unsubscribable
        final public UnsubscribeOnceTest testVariable1 = new UnsubscribeOnceTest();

        @Unsubscribable
        final public UnsubscribeOnceTest testVariable2 = new UnsubscribeOnceTest();

        final public UnsubscribeOnceTest testVariable3 = new UnsubscribeOnceTest();
    }

    public static class UnsubscribeOnceTest {
        public boolean unsubscribed = false;

        public void unsubscribe() {
            if (!unsubscribed) {
                unsubscribed = true;
            } else {
                fail("Unsubscribe called more than once!");
            }
        }
    }
}
