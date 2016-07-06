package io.reark.reark.data.stores;

/**
 * A combined default interface for a store. A store acts as a data container, in which all data
 * items are identified with an id that can be deduced from the item itself. Usually this would be
 * done through a function such as U getId(T item), but it can be defined in the store
 * implementation itself.
 *
 * @param <T> Type of the id used in this store.
 * @param <U> Type of the data this store contains.
 */
public interface StoreInterface<T, U> extends StorePutInterface<U>, StoreGetInterface<T, U> {
}
