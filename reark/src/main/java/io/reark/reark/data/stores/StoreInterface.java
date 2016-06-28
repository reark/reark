package io.reark.reark.data.stores;

/**
 * Created by ttuo on 27/06/16.
 */
public interface StoreInterface<T, U> extends StorePutInterface<U>, StoreGetInterface<T, U> {
}
