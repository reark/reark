package io.reark.rxgithubapp.shared.data;

import rx.Observable;

/**
 * Created by ttuo on 27/06/16.
 */
public interface StoreGetInterface<T, U> {
    Observable<U> getStream(T id);
}
