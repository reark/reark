/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.reark.pojo;

import android.support.annotation.NonNull;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OverwritablePojoTest {

    @Test
    public void testSameIdPojoEquals() {
        TestPojo pojo1 = new TestPojo(100, "");
        TestPojo pojo2 = new TestPojo(100, "");

        assertEquals(pojo1, pojo2);
    }

    @Test
    public void testDifferentIdPojoDoesNotEqual() {
        TestPojo pojo1 = new TestPojo(100, "");
        TestPojo pojo2 = new TestPojo(200, "");

        assertFalse(pojo1.equals(pojo2));
    }

    @Test
    public void testOverwriteWithItself() {
        TestPojo pojo1 = new TestPojo(100, "foo");

        pojo1.overwrite(pojo1);

        assertEquals(100, pojo1.id);
        assertEquals("foo", pojo1.value);
    }

    @Test
    public void testOverwriteWithAnother() {
        TestPojo pojo1 = new TestPojo(100, "foo");
        TestPojo pojo2 = new TestPojo(200, "bar");

        pojo1.overwrite(pojo2);

        assertFalse(pojo1.equals(pojo2));
        assertEquals(100, pojo1.id);
        assertEquals("bar", pojo1.value);
    }

    private static final class TestPojo extends OverwritablePojo<TestPojo> {
        private final int id;
        private String value;

        private TestPojo(int id, String value) {
            this.id = id;
            this.value = value;
        }

        @NonNull
        @Override
        protected Class<TestPojo> getTypeParameterClass() {
            return TestPojo.class;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            TestPojo testPojo = (TestPojo) o;

            if (id != testPojo.id) {
                return false;
            } else {
                return value != null
                        ? value.equals(testPojo.value)
                        : testPojo.value == null;
            }
        }
    }
}
