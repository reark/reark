/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.reark.reark.data.stores.cores;

import android.content.ContentProviderOperation;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * A class used to represent a change to the database.
 */
public final class CoreDeleteValue<U> implements CoreValue<U> {

    private final int id;

    @NonNull
    private final Uri uri;

    private CoreDeleteValue(int id, @NonNull Uri uri) {
        this.id = id;
        this.uri = uri;
    }

    @NonNull
    static <U> CoreDeleteValue<U> create(int id, @NonNull Uri uri) {
        return new CoreDeleteValue<>(id, uri);
    }

    @NonNull
    CoreOperation toOperation() {
        return new CoreOperation(id, uri,
                ContentProviderOperation.newDelete(uri).build());
    }

    public int id() {
        return id;
    }

    @NonNull
    public Uri uri() {
        return uri;
    }

    @Override
    public Type type() {
        return Type.DELETE;
    }
}
