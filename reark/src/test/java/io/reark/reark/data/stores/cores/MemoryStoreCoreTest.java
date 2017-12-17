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

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class MemoryStoreCoreTest {

    private MemoryStoreCore<Integer, String> memoryStoreCore;

    @Before
    public void setup() {
        memoryStoreCore = new MemoryStoreCore<>();
    }

    @Test
    public void put_StoresValue() {
        TestObserver<Boolean> putObserver = new TestObserver<>();
        TestObserver<String> getObserver = new TestObserver<>();
        memoryStoreCore.getStream(100).subscribe(getObserver);

        memoryStoreCore.put(100, "test value 1").subscribe(putObserver);

        putObserver.assertValue(true);
        getObserver.assertValue("test value 1");
    }

    @Test
    public void put_WithTwoDifferentIds_StoresTwoValues() {
        TestObserver<Boolean> putObserver1 = new TestObserver<>();
        TestObserver<Boolean> putObserver2 = new TestObserver<>();
        TestObserver<String> getObserver1 = new TestObserver<>();
        TestObserver<String> getObserver2 = new TestObserver<>();
        memoryStoreCore.getStream(100).subscribe(getObserver1);
        memoryStoreCore.getStream(200).subscribe(getObserver2);

        memoryStoreCore.put(100, "test value 1").subscribe(putObserver1);
        memoryStoreCore.put(200, "test value 2").subscribe(putObserver2);

        putObserver1.assertValue(true);
        putObserver2.assertValue(true);
        getObserver1.assertValue("test value 1");
        getObserver2.assertValue("test value 2");
    }

    @Test
    public void put_WithTwoSameIds_WithSameValues_StoresValueOnce() {
        TestObserver<Boolean> putObserver1 = new TestObserver<>();
        TestObserver<Boolean> putObserver2 = new TestObserver<>();
        TestObserver<String> getSubscriber = new TestObserver<>();
        memoryStoreCore.getStream(100).subscribe(getSubscriber);

        memoryStoreCore.put(100, "test value 1").subscribe(putObserver1);
        memoryStoreCore.put(100, "test value 1").subscribe(putObserver2);

        putObserver1.assertValue(true);
        putObserver2.assertValue(false);
        getSubscriber.assertValue("test value 1");
    }

    @Test
    public void put_WithTwoSameIds_WithDifferentValues_StoresValueTwice() {
        TestObserver<Boolean> putObserver1 = new TestObserver<>();
        TestObserver<Boolean> putObserver2 = new TestObserver<>();
        TestObserver<String> getSubscriber = new TestObserver<>();
        memoryStoreCore.getStream(100).subscribe(getSubscriber);

        memoryStoreCore.put(100, "test value 1").subscribe(putObserver1);
        memoryStoreCore.put(100, "test value 2").subscribe(putObserver2);

        putObserver1.assertValue(true);
        putObserver2.assertValue(true);
        getSubscriber.assertValues("test value 1", "test value 2");
    }

    @Test
    public void put_WhenMergeThrows_Errors() {
        memoryStoreCore = new MemoryStoreCore<>((s, s2) -> {
            throw new IllegalStateException("Mock");
        });

        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.put(100, "test value 2")
                .test()
                .assertError(IllegalStateException.class);
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
        TestObserver<String> testObserver = new TestObserver<>();
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.getStream(100).subscribe(testObserver);

        memoryStoreCore.delete(100);

        testObserver.assertNotComplete()
                .assertNoValues();
    }

    @Test
    public void getCached_ReturnsAllValues() {
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.put(200, "test value 2");

        memoryStoreCore.getCached()
                .test()
                .assertValue(asList("test value 1", "test value 2"));
    }

    @Test
    public void getCached_WhenEmptyStore_ReturnsEmptyList() {
        memoryStoreCore.getCached()
                .test()
                .assertValue(emptyList());
    }

    @Test
    public void getCached_WithId_ReturnsValue() {
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.put(200, "test value 2");

        memoryStoreCore.getCached(200)
                .test()
                .assertValue("test value 2");
    }

    @Test
    public void getCached_WithId_WhenNoValue_Completes() {
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.put(200, "test value 2");

        memoryStoreCore.getCached(300)
                .test()
                .assertComplete()
                .assertNoValues();
    }

    @Test
    public void getStream_WithNoId_EmitsAllValues_AndDoesNotComplete() {
        TestObserver<String> testObserver = new TestObserver<>();

        // Stream just gives all values the store receives.
        memoryStoreCore.getStream().subscribe(testObserver);
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.put(200, "test value 2");

        testObserver.assertNotComplete()
                .assertNoErrors()
                .assertValues("test value 1", "test value 2");
    }

    @Test
    public void getStream_WithNoId_DoesNotEmitInitialValue() {
        // MemoryStoreCore does not provide cached values as part of the stream.
        TestObserver<String> testObserver = new TestObserver<>();

        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.getStream().subscribe(testObserver);
        memoryStoreCore.put(200, "test value 2");
        memoryStoreCore.put(300, "test value 3");

        testObserver.assertNotComplete()
                .assertNoErrors()
                .assertValues("test value 2", "test value 3");
    }

    @Test
    public void getStream_WithNoId_DoesNotEmitValues_BeforeSubscribing() {
        // MemoryStoreCore does not provide cached values as part of the stream.
        TestObserver<String> testObserver = new TestObserver<>();

        Observable<String> stream = memoryStoreCore.getStream();
        memoryStoreCore.put(100, "test value 1");
        stream.subscribe(testObserver);
        memoryStoreCore.put(200, "test value 2");
        memoryStoreCore.put(300, "test value 3");

        testObserver.assertNotComplete()
                .assertNoErrors()
                .assertValues("test value 2", "test value 3");
    }

    @Test
    public void getStream_WithId_EmitsValuesForId_AndDoesNotComplete() {
        // MemoryStoreCore gives separate streams subscribable by id.
        TestObserver<String> testObserver1 = new TestObserver<>();
        TestObserver<String> testObserver2 = new TestObserver<>();

        memoryStoreCore.getStream(100).subscribe(testObserver1);
        memoryStoreCore.getStream(200).subscribe(testObserver2);
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.put(200, "test value 2");

        testObserver1.awaitDone(50, TimeUnit.MILLISECONDS)
                .assertNotComplete()
                .assertNoErrors()
                .assertValue("test value 1");

        testObserver2.awaitDone(50, TimeUnit.MILLISECONDS)
                .assertNotComplete()
                .assertNoErrors()
                .assertValue("test value 2");
    }

    @Test
    public void getStream_WithId_DoesNotEmitInitialValue() {
        // MemoryStoreCore does not provide cached values as part of the stream.
        TestObserver<String> testObserver = new TestObserver<>();

        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.getStream(100).subscribe(testObserver);
        memoryStoreCore.put(100, "test value 2");

        testObserver.assertNotComplete()
                .assertNoErrors()
                .assertValue("test value 2");
    }
}
