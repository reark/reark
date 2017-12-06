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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AndroidSchedulers.class)
public class RxViewBinderTest {

    private RxViewBinder binder;

    private Subject<String> testSubject;

    private TestObserver<String> testObserver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Mocking AndroidSchedulers.mainThread() as loopers are not mocked by android unit tests
        PowerMockito.stub(PowerMockito.method(AndroidSchedulers.class, "mainThread"))
                    .toReturn(Schedulers.single());

        testSubject = PublishSubject.create();

        binder = new RxViewBinder() {
            @Override
            protected void bindInternal(@NonNull final CompositeDisposable compositeDisposable) {
                assertNull(testObserver);

                // It is not possible to subscribe multiple with the same subscriber.
                testObserver = new TestObserver<>();
                compositeDisposable.add(testSubject.subscribeWith(testObserver));
            }
        };
    }

    @Test
    public void testInitialState() throws Exception {
        testSubject.onNext("testString");

        assertNull(testObserver);
    }

    @Test
    public void testInitialUnbind() throws Exception {
        binder.unbind();

        assertNull(testObserver);
    }

    @Test
    public void testBind() throws Exception {
        binder.bind();
        testSubject.onNext("testString");

        assertNotNull(testObserver);
        assertFalse(testObserver.isDisposed());
        assertEquals(1, testObserver.getEvents().get(0).size());
        assertEquals("testString", testObserver.getEvents().get(0).get(0));
    }

    @Test
    public void testUnbind() throws Exception {
        binder.bind();
        binder.unbind();
        testSubject.onNext("testString");

        assertNotNull(testObserver);
        assertTrue(testObserver.isDisposed());
        assertEquals(0, testObserver.getEvents().get(0).size());
    }

    @Test
    public void testReBind() throws Exception {
        binder.bind();
        binder.unbind();

        // The test subscriber changes with each bind.
        TestObserver<String> firstTestObserver = testObserver;
        testObserver = null;

        binder.bind();
        testSubject.onNext("testString");

        assertNotNull(firstTestObserver);
        assertNotNull(testObserver);
        assertTrue(firstTestObserver.isDisposed());
        assertFalse(testObserver.isDisposed());
        assertEquals(0, firstTestObserver.getEvents().get(0).size());
        assertEquals(1, testObserver.getEvents().get(0).size());
        assertEquals("testString", testObserver.getEvents().get(0).get(0));
    }

    @Test
    public void testDoubleBind() throws Exception {
        binder.bind();

        // The test subscriber changes with each bind.
        TestObserver<String> firstTestObserver = testObserver;
        testObserver = null;

        binder.bind();
        testSubject.onNext("testString");

        assertNotNull(firstTestObserver);
        assertNotNull(testObserver);
        assertTrue(firstTestObserver.isDisposed());
        assertFalse(testObserver.isDisposed());
        assertEquals(0, firstTestObserver.getEvents().get(0).size());
        assertEquals(1, testObserver.getEvents().get(0).size());
        assertEquals("testString", testObserver.getEvents().get(0).get(0));
    }
}
