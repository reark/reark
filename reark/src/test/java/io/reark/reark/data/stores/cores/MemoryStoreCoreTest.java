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

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import io.reark.reark.data.stores.StoreItem;


public class MemoryStoreCoreTest {

    private MemoryStoreCore<Integer, String> memoryStoreCore;

    @Before
    public void setup() {
        memoryStoreCore = new MemoryStoreCore<>();
    }

    @Test
    public void put_StoresValue() {
        TestObserver<Boolean> putSubscriber = new TestObserver<>();
        TestSubscriber<String> getSubscriber = new TestSubscriber<>();
        memoryStoreCore.getStream(100).subscribe(getSubscriber);

        memoryStoreCore.put(100, "test value 1").subscribe(putSubscriber);

        putSubscriber.assertValue(true);
        getSubscriber.assertValue("test value 1");
    }

    @Test
    public void put_WithTwoDifferentIds_StoresTwoValues() {
        TestObserver<Boolean> putSubscriber1 = new TestObserver<>();
        TestObserver<Boolean> putSubscriber2 = new TestObserver<>();
        TestSubscriber<String> getSubscriber1 = new TestSubscriber<>();
        TestSubscriber<String> getSubscriber2 = new TestSubscriber<>();
        memoryStoreCore.getStream(100).subscribe(getSubscriber1);
        memoryStoreCore.getStream(200).subscribe(getSubscriber2);

        memoryStoreCore.put(100, "test value 1").subscribe(putSubscriber1);
        memoryStoreCore.put(200, "test value 2").subscribe(putSubscriber2);

        putSubscriber1.assertValue(true);
        putSubscriber2.assertValue(true);
        getSubscriber1.assertValue("test value 1");
        getSubscriber2.assertValue("test value 2");
    }

    @Test
    public void put_WithTwoSameIds_WithSameValues_StoresValueOnce() {
        TestObserver<Boolean> putSubscriber1 = new TestObserver<>();
        TestObserver<Boolean> putSubscriber2 = new TestObserver<>();
        TestSubscriber<String> getSubscriber = new TestSubscriber<>();
        memoryStoreCore.getStream(100).subscribe(getSubscriber);

        memoryStoreCore.put(100, "test value 1").subscribe(putSubscriber1);
        memoryStoreCore.put(100, "test value 1").subscribe(putSubscriber2);

        putSubscriber1.assertValue(true);
        putSubscriber2.assertValue(false);
        getSubscriber.assertValue("test value 1");
    }

    @Test
    public void put_WithTwoSameIds_WithDifferentValues_StoresValueTwice() {
        TestObserver<Boolean> putSubscriber1 = new TestObserver<>();
        TestObserver<Boolean> putSubscriber2 = new TestObserver<>();
        TestSubscriber<String> getSubscriber = new TestSubscriber<>();
        memoryStoreCore.getStream(100).subscribe(getSubscriber);

        memoryStoreCore.put(100, "test value 1").subscribe(putSubscriber1);
        memoryStoreCore.put(100, "test value 2").subscribe(putSubscriber2);

        putSubscriber1.assertValue(true);
        putSubscriber2.assertValue(true);
        getSubscriber.assertValues("test value 1", "test value 2");
    }

    @Test
    public void delete_WithNoExistingValue_EmitsFalse() {
        memoryStoreCore.delete(100)
                .test()
                .assertComplete()
                .assertValue(false);
    }

    @Test
    public void delete_WithExistingValue_DeletesValue_AndEmitsTrue() {
        memoryStoreCore.put(100, "test value 1");

        memoryStoreCore.delete(100)
                .test()
                .assertComplete()
                .assertValue(true);
        memoryStoreCore.getCached(100)
                .test()
                .assertComplete()
                .assertNoValues();
    }

    @Test
    public void delete_DoesNotTriggerStream() {
        TestSubscriber<String> getSubscriber = new TestSubscriber<>();
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.getStream(100).subscribe(getSubscriber);

        memoryStoreCore.delete(100);

        getSubscriber.assertNotComplete();
        getSubscriber.assertNoValues();
    }

    @Test
    public void getStream_WithNoId_EmitsAllValues_AndDoesNotComplete() {
        TestSubscriber<StoreItem<Integer, String>> testSubscriber = new TestSubscriber<>();

        // Stream just gives all values the store receives.
        memoryStoreCore.getStream().subscribe(testSubscriber);
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.put(200, "test value 2");

        testSubscriber.assertNotComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValues(
                        new StoreItem<>(100, "test value 1"),
                        new StoreItem<>(200, "test value 2")
                );
    }

    @Test
    public void getStream_WithNoId_DoesNotEmitInitialValue() {
        // MemoryStoreCore does not provide cached values as part of the stream.
        TestSubscriber<StoreItem<Integer, String>> testSubscriber = new TestSubscriber<>();

        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.getStream().subscribe(testSubscriber);
        memoryStoreCore.put(200, "test value 2");
        memoryStoreCore.put(300, "test value 3");

        testSubscriber.assertNotComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValues(
                        new StoreItem<>(200, "test value 2"),
                        new StoreItem<>(300, "test value 3")
                );
    }

    @Test
    public void getStream_WithNoId_DoesNotEmitValues_BeforeSubscribing() {
        // MemoryStoreCore does not provide cached values as part of the stream.
        TestSubscriber<StoreItem<Integer, String>> testSubscriber = new TestSubscriber<>();

        Flowable<StoreItem<Integer, String>> stream = memoryStoreCore.getStream();
        memoryStoreCore.put(100, "test value 1");
        stream.subscribe(testSubscriber);
        memoryStoreCore.put(200, "test value 2");
        memoryStoreCore.put(300, "test value 3");

        testSubscriber.assertNotComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValues(
                        new StoreItem<>(200, "test value 2"),
                        new StoreItem<>(300, "test value 3")
                );
    }

    @Test
    public void getStream_WithId_EmitsValuesForId_AndDoesNotComplete() {
        // MemoryStoreCore gives separate streams subscribable by id.
        TestSubscriber<String> testSubscriber1 = new TestSubscriber<>();
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<>();

        memoryStoreCore.getStream(100).subscribe(testSubscriber1);
        memoryStoreCore.getStream(200).subscribe(testSubscriber2);
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.put(200, "test value 2");

        testSubscriber1.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);
        testSubscriber1.assertNotComplete();
        testSubscriber1.assertNoErrors();
        testSubscriber1.assertValue("test value 1");

        testSubscriber2.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);
        testSubscriber2.assertNotComplete();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertValue("test value 2");
    }

    @Test
    public void getStream_WithId_DoesNotEmitInitialValue() {
        // MemoryStoreCore does not provide cached values as part of the stream.
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();

        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.getStream(100).subscribe(testSubscriber);
        memoryStoreCore.put(100, "test value 2");

        testSubscriber.assertNotComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue("test value 2");
    }
}
