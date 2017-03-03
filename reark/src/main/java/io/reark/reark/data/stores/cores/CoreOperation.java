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
 * A class wrapping ContentProviderOperation, the operation Uri, and operation identifier.
 */
public final class CoreOperation {

    @NonNull
    private static final ContentProviderOperation NO_OP =
            ContentProviderOperation.newInsert(Uri.EMPTY).build();

    private final int id;

    @NonNull
    private final Uri uri;

    @NonNull
    private final ContentProviderOperation operation;

    CoreOperation(int id, @NonNull Uri uri) {
        this(id, uri, NO_OP);
    }

    CoreOperation(int id, @NonNull Uri uri, @NonNull ContentProviderOperation operation) {
        this.id = id;
        this.uri = uri;
        this.operation = operation;
    }

    public int id() {
        return id;
    }

    @NonNull
    public Uri uri() {
        return uri;
    }

    @NonNull
    ContentProviderOperation contentOperation() {
        return operation;
    }

    boolean isValid() {
        return !NO_OP.equals(operation);
    }

}
