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
package io.reark.reark.data.stores;

import android.support.v4.util.Pair;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

public class MemoryStoreTest {

    private MemoryStore<Integer, Pair<Integer, String>, Pair<Integer, String>> memoryStore;
    private TestObserver<Pair<Integer, String>> testObserver;

    // Store will emit an empty value in case an instant value is requested but does not exist.
    // Each store needs to define the strategy for the empty value -- it may be a specific
    // value representing none, or e.g. an empty optional.
    private static final Pair<Integer, String> NONE = Pair.create(-1, null);

    @Before
    public void setup() {
        memoryStore = new MemoryStore<>(pair -> pair.first,
                pair -> pair != null ? pair : NONE,
                () -> NONE);

        testObserver = new TestObserver<>();
    }

    @Test
    public void getOnce_WithData_ReturnsData_AndCompletes() {
        final Pair<Integer, String> value = new Pair<>(100, "test string 1");

        // getOnce is expected to return a observable that emits the value and then completes.
        memoryStore.put(value);
        memoryStore.getOnce(100)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue(value);
    }

    @Test
    public void getOnce_WithNoData_ReturnsNoneValue_AndCompletes() {
        // getOnce is expected to return null observable in case it does not have the value.
        memoryStore.getOnce(100)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue(NONE);
    }

    @Test
    public void getOnceAndStream_ReturnsOnlyValuesForSubscribedId_AndDoesNotComplete() {
        final Pair<Integer, String> value1 = new Pair<>(100, "test string 1");
        final Pair<Integer, String> value2 = new Pair<>(200, "test string 2");

        memoryStore.getOnceAndStream(100).subscribeWith(testObserver);
        memoryStore.put(value1);
        memoryStore.put(value2);

        testObserver.awaitDone(50, TimeUnit.MILLISECONDS)
                .assertNotComplete()
                .assertNoErrors()
                .assertValues(NONE, value1);
    }

    @Test
    public void getOnceAndStream_ReturnsAllValuesForSubscribedId_AndDoesNotComplete() {
        final Pair<Integer, String> value1 = new Pair<>(100, "test string 1");
        final Pair<Integer, String> value2 = new Pair<>(100, "test string 2");
        final Pair<Integer, String> value3 = new Pair<>(100, "test string 3");

        memoryStore.getOnceAndStream(100).subscribe(testObserver);
        memoryStore.put(value1);
        memoryStore.put(value2);
        memoryStore.put(value3);

        testObserver.awaitDone(50, TimeUnit.MILLISECONDS)
                .assertNotComplete()
                .assertNoErrors()
                .assertValues(NONE, value1, value2, value3);
    }

    @Test
    public void getOnceAndStream_ReturnsOnlyNewValues_AndDoesNotComplete() {
        final Pair<Integer, String> value = new Pair<>(100, "test string 1");

        // In the default store implementation identical values are filtered out.
        memoryStore.getOnceAndStream(100).subscribe(testObserver);
        memoryStore.put(value);
        memoryStore.put(value);

        testObserver.awaitDone(50, TimeUnit.MILLISECONDS)
                .assertNotComplete()
                .assertNoErrors()
                .assertValues(NONE, value);
    }

    @Test
    public void getOnceAndStream_WithInitialValue_ReturnsInitialValues_AndDoesNotComplete() {
        final Pair<Integer, String> value = new Pair<>(100, "test string 1");

        memoryStore.put(value);
        memoryStore.getOnceAndStream(100).subscribe(testObserver);

        testObserver.awaitDone(50, TimeUnit.MILLISECONDS)
                .assertNotComplete()
                .assertNoErrors()
                .assertValue(value);
    }

    @Test
    public void getOnceAndStream_WithInitialValue_WithDelayedSubscription_ReturnsFirstValue_AndDoesNotComplete() {
        final Pair<Integer, String> value1 = new Pair<>(100, "test string 1");
        final Pair<Integer, String> value2 = new Pair<>(100, "test string 2");

        // This behavior is a little surprising, but it is because we cannot guarantee that the
        // observable that is produced as the stream will keep its first (cached) value up to date.
        // The only ways to around this would be custom subscribe function or converting the
        // source observable into a behavior, but these would significantly increase the
        // complexity and are hard to implement in other kinds of store (such as content providers).

        // Put initial value.
        memoryStore.put(value1);

        // Create the stream observable but do not subscribe immediately.
        Observable<Pair<Integer, String>> stream = memoryStore.getOnceAndStream(100);

        // Put new value into the store.
        memoryStore.put(value2);

        // Subscribe to stream that was created potentially a long time ago.
        stream.subscribe(testObserver);

        // Observe that the stream actually gives as the first item the cached value at the time of
        // creating the stream observable.
        testObserver.awaitDone(50, TimeUnit.MILLISECONDS)
                .assertNotComplete()
                .assertNoErrors()
                .assertValue(value1);
    }

    @Test
    public void put_WithNewData_EmitsTrue() {
        final Pair<Integer, String> value = new Pair<>(100, "test string 1");

        memoryStore.put(value)
                .test()
                .assertValue(true);
    }

    @Test
    public void put_WithDifferentData_OverExistingData_EmitsTrue() {
        final Pair<Integer, String> value1 = new Pair<>(100, "test string 1");
        final Pair<Integer, String> value2 = new Pair<>(100, "test string 2");
        memoryStore.put(value1);

        memoryStore.put(value2)
                .test()
                .assertValue(true);
    }

    @Test
    public void put_WithIdenticalData_OverExistingData_EmitsFalse() {
        final Pair<Integer, String> value = new Pair<>(100, "test string 1");
        memoryStore.put(value);

        memoryStore.put(value)
                .test()
                .assertValue(false);
    }

    @Test
    public void delete_WithNoData_EmitsFalse() {
        memoryStore.delete(765)
                .test()
                .assertComplete()
                .assertValue(false);
    }

    @Test
    public void delete_WithData_DeletesData_AndEmitsTrue() {
        final Pair<Integer, String> value = new Pair<>(100, "test string 1");
        memoryStore.put(value);

        memoryStore.delete(100)
                .test()
                .assertComplete()
                .assertValue(true);
        memoryStore.getOnce(100)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue(NONE);
    }

    @Test
    public void getOnceAndStream_ThenDelete_DoesNotEmit() {
        final Pair<Integer, String> value = new Pair<>(100, "test string 1");
        memoryStore.put(value);

        TestObserver<Pair<Integer, String>> ts = memoryStore.getOnceAndStream(100).test();
        memoryStore.delete(100);

        ts.assertNotComplete()
                .assertNoErrors()
                .assertValue(value);
    }
}
