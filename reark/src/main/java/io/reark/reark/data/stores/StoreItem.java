/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.reark.data.stores;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static io.reark.reark.utils.Preconditions.get;

/**
 * A class used internally in stores. It behaves much like a Pair.
 *
 * @param <T> Type of the identifier.
 * @param <U> Type of the data item.
 */
public class StoreItem<T, U> {

    @NonNull
    private final T id;

    @Nullable
    private final U item;

    public StoreItem(@NonNull final T id, @Nullable final U item) {
        this.id = get(id);
        this.item = get(item);
    }

    @NonNull
    public T id() {
        return id;
    }

    @Nullable
    public U item() {
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

        final StoreItem<?, ?> storeItem = (StoreItem<?, ?>) o;

        return id.equals(storeItem.id)
               && (item != null ? item.equals(storeItem.item) : storeItem.item == null);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StoreItem{");
        sb.append("id=").append(id);
        sb.append(", item=").append(item);
        sb.append('}');
        return sb.toString();
    }
}
