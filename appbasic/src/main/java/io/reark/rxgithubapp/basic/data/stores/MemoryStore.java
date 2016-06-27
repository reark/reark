package io.reark.rxgithubapp.basic.data.stores;

import io.reark.rxgithubapp.shared.data.StoreInterface;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by ttuo on 27/06/16.
 */
public class MemoryStore<T, U> implements StoreInterface<T, U> {
    private final MemoryStoreCore<T, U> core;
    private final GetIdForItem<T, U> getIdForItem;

    public MemoryStore(GetIdForItem<T, U> getIdForItem) {
        core = new MemoryStoreCore<>();
        this.getIdForItem = getIdForItem;
    }

    @Override
    public void put(T item) {
        core.put(getIdForItem.call(item), item);
    }

    @Override
    public Observable<T> getStream(U id) {
        return core.getStream(id);
    }

    public interface GetIdForItem<T, U> {
        U call(T item);
    }
}
