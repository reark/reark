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
package io.reark.reark.utils;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.android.schedulers.AndroidSchedulers;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AndroidSchedulers.class)
public class RxViewBinderTest {

    private RxViewBinder binder;

    private Subject<String, String> testSubject;

    private TestSubscriber<String> testSubscriber;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Mocking AndroidSchedulers.mainThread() as loopers are not mocked by android unit tests
        PowerMockito.stub(PowerMockito.method(AndroidSchedulers.class, "mainThread"))
                    .toReturn(Schedulers.immediate());

        testSubject = PublishSubject.create();

        binder = new RxViewBinder() {
            @Override
            protected void bindInternal(@NonNull final CompositeSubscription compositeSubscription) {
                assertNull(testSubscriber);

                // It is not possible to subscribe multiple with the same subscriber.
                testSubscriber = new TestSubscriber<>();

                compositeSubscription.add(testSubject.subscribe(testSubscriber));
            }
        };
    }

    @Test
    public void testInitialState() throws Exception {
        testSubject.onNext("testString");

        assertNull(testSubscriber);
    }

    @Test
    public void testInitialUnbind() throws Exception {
        binder.unbind();

        assertNull(testSubscriber);
    }

    @Test
    public void testBind() throws Exception {
        binder.bind();
        testSubject.onNext("testString");

        assertNotNull(testSubscriber);
        assertFalse(testSubscriber.isUnsubscribed());
        assertEquals(1, testSubscriber.getOnNextEvents().size());
        assertEquals("testString", testSubscriber.getOnNextEvents().get(0));
    }

    @Test
    public void testUnbind() throws Exception {
        binder.bind();
        binder.unbind();
        testSubject.onNext("testString");

        assertNotNull(testSubscriber);
        assertTrue(testSubscriber.isUnsubscribed());
        assertEquals(0, testSubscriber.getOnNextEvents().size());
    }

    @Test
    public void testReBind() throws Exception {
        binder.bind();
        binder.unbind();

        // The test subscriber changes with each bind.
        TestSubscriber<String> firstTestSubscriber = testSubscriber;
        testSubscriber = null;

        binder.bind();
        testSubject.onNext("testString");

        assertNotNull(firstTestSubscriber);
        assertNotNull(testSubscriber);
        assertTrue(firstTestSubscriber.isUnsubscribed());
        assertFalse(testSubscriber.isUnsubscribed());
        assertEquals(0, firstTestSubscriber.getOnNextEvents().size());
        assertEquals(1, testSubscriber.getOnNextEvents().size());
        assertEquals("testString", testSubscriber.getOnNextEvents().get(0));
    }

    @Test
    public void testDoubleBind() throws Exception {
        binder.bind();

        // The test subscriber changes with each bind.
        TestSubscriber<String> firstTestSubscriber = testSubscriber;
        testSubscriber = null;

        binder.bind();
        testSubject.onNext("testString");

        assertNotNull(firstTestSubscriber);
        assertNotNull(testSubscriber);
        assertTrue(firstTestSubscriber.isUnsubscribed());
        assertFalse(testSubscriber.isUnsubscribed());
        assertEquals(0, firstTestSubscriber.getOnNextEvents().size());
        assertEquals(1, testSubscriber.getOnNextEvents().size());
        assertEquals("testString", testSubscriber.getOnNextEvents().get(0));
    }
}
