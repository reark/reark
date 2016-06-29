package io.reark.reark.data.stores.cores;

import rx.Observable;

/**
 * Created by ttuo on 29/06/16.
 */
public interface StoreCoreInterface<T, U> {
    void put(T id, U item);
    Observable<U> getCached(T id);
    Observable<U> getStream(T id);
}
