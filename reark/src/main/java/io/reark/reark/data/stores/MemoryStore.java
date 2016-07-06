package io.reark.reark.data.stores;

import io.reark.reark.data.stores.cores.MemoryStoreCore;
import rx.functions.Func2;

/**
 * Perhaps the most simple self-contained store. Use this as a starter or when you do not need
 * permanent persistence on disk or sharing between processes. The MemoryStore uses internally a
 * MemoryStoreCore, but otherwise it extends the more abstract DefaultStore.
 *
 * @param <T> Type of the id used in this store.
 * @param <U> Type of the data this store contains.
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
