package io.reark.reark.data.stores;

import io.reark.reark.data.stores.cores.StoreCoreInterface;
import rx.Observable;

/**
 * DefaultStore is a simple implementation of store logic. It can be used with any data types by
 * providing a function for deducing the id of an item. This could be done, for instance, with
 * T getId(U item).
 *
 * The DefaultStore works with any StoreCore instance.
 *
 * @param <T> Type of the id used in this store.
 * @param <U> Type of the data this store contains.
 */
public class DefaultStore<T, U> implements StoreInterface<T, U> {
    private final StoreCoreInterface<T, U> core;
    private final GetIdForItem<T, U> getIdForItem;

    public DefaultStore(StoreCoreInterface<T, U> core,
                        GetIdForItem<T, U> getIdForItem) {
        this.core = core;
        this.getIdForItem = getIdForItem;
    }

    @Override
    public void put(U item) {
        core.put(getIdForItem.call(item), item);
    }

    @Override
    public Observable<U> getOne(T id) {
        return core.getCached(id).filter(item -> item != null);
    }

    @Override
    public Observable<U> getStream(T id) {
        return Observable.concat(
                getOne(id),
                core.getFutureStream(id));
    }

    public interface GetIdForItem<T, U> {
        T call(U item);
    }
}
