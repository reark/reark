package io.reark.reark.data.store;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reark.reark.utils.Preconditions;

/**
 * Created by Pawel Polanski on 12/28/15.
 */
class StoreItem<T> {

    @NonNull
    private final Uri uri;

    @Nullable
    private final T item;

    StoreItem(@NonNull final Uri uri, @Nullable final T item) {
        Preconditions.checkNotNull(uri, "uri cannot be null.");

        this.uri = uri;
        this.item = item;
    }

    @NonNull
    public Uri uri() {
        return uri;
    }

    @Nullable
    public T item() {
        return item;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoreItem)) {
            return false;
        }

        final StoreItem<?> storeItem = (StoreItem<?>) o;

        return uri.equals(storeItem.uri)
               && (item != null ? item.equals(storeItem.item) : storeItem.item == null);
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StoreItem{");
        sb.append("uri=").append(uri);
        sb.append(", item=").append(item);
        sb.append('}');
        return sb.toString();
    }
}
