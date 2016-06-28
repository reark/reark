package io.reark.reark.data.stores;

import android.support.v4.util.Pair;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import rx.Observable;
import rx.observers.TestSubscriber;

/**
 * Created by ttuo on 28/06/16.
 */
public class MemoryStoreCoreTest {
    private MemoryStoreCore<Integer, String> memoryStoreCore;

    @Before
    public void setup() {
        memoryStoreCore = new MemoryStoreCore<>();
    }

    @Test
    public void testPut() {
        memoryStoreCore.put(100, "test value");

        Assert.assertEquals("test value", memoryStoreCore.get(100));
    }

    @Test
    public void testPutTwo() {
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.put(200, "test value 2");

        Assert.assertEquals("test value 1", memoryStoreCore.get(100));
        Assert.assertEquals("test value 2", memoryStoreCore.get(200));
    }

    @Test
    public void testStream() {
        // Stream just gives all values the store receives.
        TestSubscriber<Pair<Integer, String>> testSubscriber = new TestSubscriber<>();

        memoryStoreCore.getStream().subscribe(testSubscriber);
        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.put(200, "test value 2");

        testSubscriber.assertReceivedOnNext(
                Arrays.asList(
                        new Pair<>(100, "test value 1"),
                        new Pair<>(200, "test value 2")
                ));
    }

    @Test
    public void testStreamNoInitialValue() {
        // MemoryStoreCore does not provide cached values as part of the stream.
        TestSubscriber<Pair<Integer, String>> testSubscriber = new TestSubscriber<>();

        memoryStoreCore.put(100, "test value 1");
        memoryStoreCore.getStream().subscribe(testSubscriber);
        memoryStoreCore.put(200, "test value 2");
        memoryStoreCore.put(300, "test value 3");

        testSubscriber.assertReceivedOnNext(
                Arrays.asList(
                        new Pair<>(200, "test value 2"),
                        new Pair<>(300, "test value 3")
                ));
    }

    @Test
    public void testStreamNoValuesBeforeSubscribing() {
        // MemoryStoreCore does not provide cached values as part of the stream.
        TestSubscriber<Pair<Integer, String>> testSubscriber = new TestSubscriber<>();

        Observable<Pair<Integer, String>> stream = memoryStoreCore.getStream();
        memoryStoreCore.put(100, "test value 1");
        stream.subscribe(testSubscriber);
        memoryStoreCore.put(200, "test value 2");
        memoryStoreCore.put(300, "test value 3");

        testSubscriber.assertReceivedOnNext(
                Arrays.asList(
                        new Pair<>(200, "test value 2"),
                        new Pair<>(300, "test value 3")
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
