import com.timotuominen.app.utils.SubscriptionAnnotationUtils;
import com.timotuominen.app.utils.Unsubscribable;

import junit.framework.TestCase;


/**
 * Created by ttuo on 12/25/13.
 */
public class SubscriptionAnnotationUtilsTest extends TestCase {
    public void testUnsubscribeAllAnnotated() {
        AnnotationTest annotationTest = new AnnotationTest();
        SubscriptionAnnotationUtils.unsubscribeAllAnnotated(annotationTest);
        assertEquals(true, annotationTest.testVariable.unsubscribed);
    }

    public static class AnnotationTest {
        public AnnotationTest() { }

        @Unsubscribable
        final public UnsubscribeOnceTest testVariable = new UnsubscribeOnceTest();
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
