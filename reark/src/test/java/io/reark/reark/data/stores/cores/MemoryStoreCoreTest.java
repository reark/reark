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
package io.reark.reark.data.stores.cores;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import io.reark.reark.data.stores.StoreItem;
import rx.Observable;
import rx.observers.TestSubscriber;

public class MemoryStoreCoreTest {
    private MemoryStoreCore<Integer, String> memoryStoreCore;

    @Before
    public void setup() {
        memoryStoreCore = new MemoryStoreCore<>();
    }

    @Test
    public void testPut() {
        memoryStoreCore.put(100, "test value");

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        memoryStoreCore.getCached(100).subscribe(testSubscriber);
        testSubscriber.assertValue("test value");
    }

    @Test
    public void testPutTwo() {
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.put(200, "test value 2");

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<>();
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<>();
        memoryStoreCore.getCached(100).subscribe(testSubscriber1);
        memoryStoreCore.getCached(200).subscribe(testSubscriber2);
        testSubscriber1.assertValue("test value 1");
        testSubscriber2.assertValue("test value 2");
    }

    @Test
    public void testStream() {
        // Stream just gives all values the store receives.
        TestSubscriber<StoreItem<Integer, String>> testSubscriber = new TestSubscriber<>();

        memoryStoreCore.getStream().subscribe(testSubscriber);
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.put(200, "test value 2");

        testSubscriber.assertReceivedOnNext(
                Arrays.asList(
                        new StoreItem<>(100, "test value 1"),
                        new StoreItem<>(200, "test value 2")
                ));
    }

    @Test
    public void testStreamNoInitialValue() {
        // MemoryStoreCore does not provide cached values as part of the stream.
        TestSubscriber<StoreItem<Integer, String>> testSubscriber = new TestSubscriber<>();

        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.getStream().subscribe(testSubscriber);
        memoryStoreCore.put(200, "test value 2");
        memoryStoreCore.put(300, "test value 3");

        testSubscriber.assertReceivedOnNext(
                Arrays.asList(
                        new StoreItem<>(200, "test value 2"),
                        new StoreItem<>(300, "test value 3")
                ));
    }

    @Test
    public void testStreamNoValuesBeforeSubscribing() {
        // MemoryStoreCore does not provide cached values as part of the stream.
        TestSubscriber<StoreItem<Integer, String>> testSubscriber = new TestSubscriber<>();

        Observable<StoreItem<Integer, String>> stream = memoryStoreCore.getStream();
        memoryStoreCore.put(100, "test value 1");
        stream.subscribe(testSubscriber);
        memoryStoreCore.put(200, "test value 2");
        memoryStoreCore.put(300, "test value 3");

        testSubscriber.assertReceivedOnNext(
                Arrays.asList(
                        new StoreItem<>(200, "test value 2"),
                        new StoreItem<>(300, "test value 3")
                ));
    }

    @Test
    public void testStreamById() {
        // MemoryStoreCore gives separate streams subscribable by id.
        TestSubscriber<String> testSubscriber1 = new TestSubscriber<>();
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<>();

        memoryStoreCore.getStream(100).subscribe(testSubscriber1);
        memoryStoreCore.getStream(200).subscribe(testSubscriber2);
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.put(200, "test value 2");

        testSubscriber1.assertValue("test value 1");
        testSubscriber2.assertValue("test value 2");
    }

    @Test
    public void testStreamByIdNoInitialValue() {
        // MemoryStoreCore does not provide cached values as part of the stream.
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();

        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.getStream(100).subscribe(testSubscriber);
        memoryStoreCore.put(100, "test value 2");

        testSubscriber.assertValue("test value 2");
    }
}
