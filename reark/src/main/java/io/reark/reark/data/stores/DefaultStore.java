package io.reark.reark.data.stores;

import io.reark.reark.data.stores.cores.StoreCoreInterface;
import rx.Observable;

/**
 * Created by ttuo on 29/06/16.
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
    public Observable<U> getStream(T id) {
        return Observable.concat(
                core.getCached(id).filter(item -> item != null),
                core.getStream(id));
    }

    public interface GetIdForItem<T, U> {
        T call(U item);
    }
}
