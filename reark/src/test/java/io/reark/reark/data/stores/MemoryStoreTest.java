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

import java.util.Arrays;

import rx.Observable;
import rx.observers.TestSubscriber;

public class MemoryStoreTest {
    private MemoryStore<Integer, Pair<Integer, String>, Pair<Integer, String>> memoryStore;
    private TestSubscriber<Pair<Integer, String>> testSubscriber;

    // Store will emit an empty value in case an instant value is requested but does not exist.
    // Each store needs to define the strategy for the empty value -- it may be a specific
    // value representing none, or e.g. an empty optional.
    private static final Pair<Integer, String> NONE = Pair.create(-1, null);

    @Before
    public void setup() {
        memoryStore = new MemoryStore<>(pair -> pair.first,
                pair -> pair != null ? pair : NONE,
                () -> NONE);

        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void testGetOne() {
        // getOnce is expected to return a observable that emits the value and then completes.
        memoryStore.put(new Pair<>(100, "test string 1"));

        memoryStore.getOnce(100).subscribe(testSubscriber);

        testSubscriber.assertValue(new Pair<>(100, "test string 1"));
        testSubscriber.assertCompleted();
    }

    @Test
    public void testGetOneEmpty() {
        // getOnce is expected to return null observable in case it does not have the value.
        memoryStore.getOnce(100).subscribe(testSubscriber);

        testSubscriber.assertValue(NONE);
        testSubscriber.assertCompleted();
    }

    @Test
    public void testGetOnceAndStream() {
        memoryStore.getOnceAndStream(100).subscribe(testSubscriber);
        memoryStore.put(new Pair<>(100, "test string 1"));
        memoryStore.put(new Pair<>(200, "test string 2"));

        testSubscriber.assertReceivedOnNext(
                Arrays.asList(
                        NONE,
                        new Pair<>(100, "test string 1")
                ));
    }

    @Test
    public void testMultipleValues() {
        memoryStore.getOnceAndStream(100).subscribe(testSubscriber);
        memoryStore.put(new Pair<>(100, "test string 1"));
        memoryStore.put(new Pair<>(100, "test string 2"));
        memoryStore.put(new Pair<>(100, "test string 3"));

        testSubscriber.assertReceivedOnNext(
                Arrays.asList(
                        NONE,
                        new Pair<>(100, "test string 1"),
                        new Pair<>(100, "test string 2"),
                        new Pair<>(100, "test string 3")
                ));
    }

    @Test
    public void testIdenticalValues() {
        // In the default store implementation identical values are filtered out.

        memoryStore.getOnceAndStream(100).subscribe(testSubscriber);
        memoryStore.put(new Pair<>(100, "test string"));
        memoryStore.put(new Pair<>(100, "test string"));

        testSubscriber.assertReceivedOnNext(
                Arrays.asList(
                        NONE,
                        new Pair<>(100, "test string")
                ));
    }

    @Test
    public void testGetOnceAndStreamWithInitialValue() {
        memoryStore.put(new Pair<>(100, "test string 1"));
        memoryStore.getOnceAndStream(100).subscribe(testSubscriber);

        testSubscriber.assertValue(new Pair<>(100, "test string 1"));
    }

    @Test
    public void testGetOnceAndStreamWithInitialValueDelayedSubscription() {
        // This behavior is a little surprising, but it is because we cannot guarantee that the
        // observable that is produced as the stream will keep its first (cached) value up to date.
        // The only ways to around this would be custom subscribe function or converting the
        // source observable into a behavior, but these would significantly increase the
        // complexity and are hard to implement in other kinds of store (such as content providers).

        // Put initial value.
        memoryStore.put(new Pair<>(100, "test string 1"));

        // Create the stream observable but do not subscribe immediately.
        Observable<Pair<Integer, String>> stream = memoryStore.getOnceAndStream(100);

        // Put new value into the store.
        memoryStore.put(new Pair<>(100, "test string 2"));

        // Subscribe to stream that was created potentially a long time ago.
        stream.subscribe(testSubscriber);

        // Observe that the stream actually gives as the first item the cached value at the time of
        // creating the stream observable.
        testSubscriber.assertValue(new Pair<>(100, "test string 1"));
    }
}
