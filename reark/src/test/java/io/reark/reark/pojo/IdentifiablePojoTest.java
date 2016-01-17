package io.reark.reark.pojo;

import android.support.annotation.NonNull;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by apoi on 27/12/15.
 */
public class IdentifiablePojoTest {

    @Test
    public void testSameIdPojoEquals() {
        TestPojo pojo1 = new TestPojo(100, "");
        TestPojo pojo2 = new TestPojo(100, "");

        assertEquals(true, pojo1.equals(pojo2));
    }

    @Test
    public void testDifferentIdPojoDoesNotEqual() {
        TestPojo pojo1 = new TestPojo(100, "");
        TestPojo pojo2 = new TestPojo(200, "");

        assertEquals(false, pojo1.equals(pojo2));
    }

    @Test
    public void testOverwriteWithItself() {
        TestPojo pojo1 = new TestPojo(100, "foo");

        pojo1.overwrite(pojo1);

        assertEquals(100, pojo1.getId());
        assertEquals("foo", pojo1.value);
    }

    @Test
    public void testOverwriteWithAnother() {
        TestPojo pojo1 = new TestPojo(100, "foo");
        TestPojo pojo2 = new TestPojo(200, "bar");

        pojo1.overwrite(pojo2);

        assertEquals(false, pojo1.equals(pojo2));
        assertEquals(100, pojo1.getId());
        assertEquals("bar", pojo1.value);
    }

    private class TestPojo extends IdentifiablePojo<TestPojo> {
        public String value;

        public TestPojo(int id, String value) {
            super(id);
            this.value = value;
        }

        @NonNull
        @Override
        protected Class<TestPojo> getTypeParameterClass() {
            return TestPojo.class;
        }
    }
}
