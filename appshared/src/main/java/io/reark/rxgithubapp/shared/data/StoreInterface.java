package io.reark.rxgithubapp.shared.data;

/**
 * Created by ttuo on 27/06/16.
 */
public interface StoreInterface<T, U> extends StorePutInterface<T>, StoreGetInterface<T, U> {
}
