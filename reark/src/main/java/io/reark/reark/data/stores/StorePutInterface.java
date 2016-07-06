package io.reark.reark.data.stores;

/**
 * Interface for stores into which it is possible to insert data. This default interface is the most
 * simple put interface possible.
 *
 * @param <T> Type of the data items.
 */
public interface StorePutInterface<T> {
    /**
     * The standard store interface for inserting a singular data item. The id of the item is
     * expected to be deduced from the item itself by the store. This could be done through an
     * interface such as getId(), though the put interface does not have an opinion of that.
     *
     * @param item The data item to insert into the store.
     */
    void put(T item);
}
