package io.reark.reark.data.stores;

import io.reark.reark.data.stores.cores.MemoryStoreCore;
import rx.functions.Func2;

/**
 * Created by ttuo on 27/06/16.
 */
public class MemoryStore<T, U> extends DefaultStore<T, U> {
    public MemoryStore(GetIdForItem<T, U> getIdForItem) {
        super(new MemoryStoreCore<>(),
                getIdForItem);
    }

    public MemoryStore(GetIdForItem<T, U> getIdForItem,
                       Func2<U, U, U> putMergeFunction) {
        super(new MemoryStoreCore<>(putMergeFunction),
                getIdForItem);
    }
}
