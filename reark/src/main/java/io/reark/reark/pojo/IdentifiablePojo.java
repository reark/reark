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
package io.reark.reark.pojo;

import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;

/**
 * Pojo base class that supports overwriting the fields with fields from
 * another instance of the same class.
 */
public abstract class IdentifiablePojo<T extends IdentifiablePojo> {
    private static final String TAG = IdentifiablePojo.class.getSimpleName();

    protected final int id;

    public IdentifiablePojo(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @NonNull
    protected abstract Class<T> getTypeParameterClass();

    @NonNull
    @SuppressWarnings("unchecked")
    public T overwrite(@NonNull T other) {
        Preconditions.checkNotNull(other, "Can't overwrite with null value");

        if (equals(other)) {
            return (T) this;
        }

        for (Field field : getTypeParameterClass().getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            if (hasIllegalAccessModifiers(modifiers)) {
                continue;
            }

            try {
                if (!isEmpty(field, other)) {
                    field.set(this, field.get(other));
                }
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Failed set at " + field.getName(), e);
            }
        }

        return (T) this;
    }

    private boolean hasIllegalAccessModifiers(int modifiers) {
        return Modifier.isFinal(modifiers)
                || Modifier.isStatic(modifiers)
                || Modifier.isTransient(modifiers);
    }

    private boolean isEmpty(Field field, IdentifiablePojo pojo) {
        try {
            Object value = field.get(pojo);
            if (value == null) {
                return true;
            } if (value instanceof String) {
                return isEmpty((String) value);
            } else if (value instanceof Long) {
                return isEmpty((Long) value);
            } else if (value instanceof Integer) {
                return isEmpty((Integer) value);
            } else if (value instanceof Float) {
                return isEmpty((Float) value);
            } else if (value instanceof Boolean) {
                return false;
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Failed get at " + field.getName(), e);
        }

        // Derived objects with more types should first check the new types, and then the base
        // types with this method. Thus, we can assume reaching here is always an error.
        Log.e(TAG, "Unknown field type: " + field.getName());

        return true;
    }

    protected boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    // Default implementation expects all values to be zero or greater,
    // override for other expected behavior.
    protected boolean isEmpty(long value) {
        return value <= 0;
    }

    protected boolean isEmpty(int value) {
        return value <= 0;
    }

    protected boolean isEmpty(float value) {
        return value <= 0.0f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!getTypeParameterClass().isInstance(o)) {
            return false;
        }

        IdentifiablePojo that = (IdentifiablePojo) o;

        if (id != that.id) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
