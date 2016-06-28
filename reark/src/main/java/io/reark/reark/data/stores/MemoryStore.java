package io.reark.reark.data.stores;

import rx.Observable;

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
    public void put(U item) {
        core.put(getIdForItem.call(item), item);
    }

    @Override
    public Observable<U> getStream(T id) {
        if (core.contains(id)) {
            return core.getStream(id).startWith(core.get(id));
        }
        return core.getStream(id);
    }

    public interface GetIdForItem<T, U> {
        T call(U item);
    }
}
