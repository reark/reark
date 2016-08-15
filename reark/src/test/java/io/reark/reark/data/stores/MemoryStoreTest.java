package io.reark.reark.data.stores;

import android.support.v4.util.Pair;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import rx.Observable;
import rx.observers.TestSubscriber;

/**
 * Created by ttuo on 28/06/16.
 */
public class MemoryStoreTest {
    private MemoryStore<Integer, Pair<Integer, String>> memoryStore;
    private TestSubscriber<Pair<Integer, String>> testSubscriber;

    @Before
    public void setup() {
        memoryStore = new MemoryStore<>(pair -> pair.first);
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void testGetOne() {
        // getOne is expected to return a observable that emits the value and then completes.
        memoryStore.put(new Pair<>(100, "test string 1"));

        memoryStore.getOne(100).subscribe(testSubscriber);

        testSubscriber.assertValue(new Pair<>(100, "test string 1"));
        testSubscriber.assertCompleted();
    }

    @Test
    public void testGetOneEmpty() {
        // getOne is expected to return an empty observable in case it does not have the value.
        memoryStore.getOne(100).subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        testSubscriber.assertCompleted();
    }

    @Test
    public void testGetOneAndStream() {
        memoryStore.getOneAndStream(100).subscribe(testSubscriber);
        memoryStore.put(new Pair<>(100, "test string 1"));
        memoryStore.put(new Pair<>(200, "test string 2"));

        testSubscriber.assertValue(new Pair<>(100, "test string 1"));
    }

    @Test
    public void testMultipleValues() {
        memoryStore.getOneAndStream(100).subscribe(testSubscriber);
        memoryStore.put(new Pair<>(100, "test string 1"));
        memoryStore.put(new Pair<>(100, "test string 2"));
        memoryStore.put(new Pair<>(100, "test string 3"));

        testSubscriber.assertReceivedOnNext(
                Arrays.asList(
                        new Pair<>(100, "test string 1"),
                        new Pair<>(100, "test string 2"),
                        new Pair<>(100, "test string 3")
                ));
    }

    @Test
    public void testIdenticalValues() {
        // In the default store implementation identical values are filtered out.

        memoryStore.getOneAndStream(100).subscribe(testSubscriber);
        memoryStore.put(new Pair<>(100, "test string"));
        memoryStore.put(new Pair<>(100, "test string"));

        testSubscriber.assertValue(new Pair<>(100, "test string"));
    }

    @Test
    public void testGetOneAndStreamWithInitialValue() {
        memoryStore.put(new Pair<>(100, "test string 1"));
        memoryStore.getOneAndStream(100).subscribe(testSubscriber);

        testSubscriber.assertValue(new Pair<>(100, "test string 1"));
    }

    @Test
    public void testGetOneAndStreamWithInitialValueDelayedSubscription() {
        // This behavior is a little surprising, but it is because we cannot guarantee that the
        // observable that is produced as the stream will keep its first (cached) value up to date.
        // The only ways to around this would be custom subscribe function or converting the
        // source observable into a behavior, but these would significantly increase the
        // complexity and are hard to implement in other kinds of store (such as content providers).

        // Put initial value.
        memoryStore.put(new Pair<>(100, "test string 1"));

        // Create the stream observable but do not subscribe immediately.
        Observable<Pair<Integer, String>> stream = memoryStore.getOneAndStream(100);

        // Put new value into the store.
        memoryStore.put(new Pair<>(100, "test string 2"));

        // Subscribe to stream that was created potentially a long time ago.
        stream.subscribe(testSubscriber);

        // Observe that the stream actually gives as the first item the cached value at the time of
        // creating the stream observable.
        testSubscriber.assertValue(new Pair<>(100, "test string 1"));
    }
}