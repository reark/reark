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

import static io.reark.reark.utils.Preconditions.checkNotNull;

/**
 * Pojo base class that supports overwriting the fields with fields from
 * another instance of the same class.
 */
public abstract class OverwritablePojo<T extends OverwritablePojo<T>> {
    private static final String TAG = OverwritablePojo.class.getSimpleName();

    @NonNull
    protected abstract Class<T> getTypeParameterClass();

    @NonNull
    @SuppressWarnings("unchecked")
    public T overwrite(@NonNull final T other) {
        checkNotNull(other, "Can't overwrite with null value");

        if (equals(other)) {
            return (T) this;
        }

        for (Field field : getTypeParameterClass().getDeclaredFields()) {
            final int modifiers = field.getModifiers();

            if (hasIllegalAccessModifiers(modifiers)) {
                continue;
            }

            if (!Modifier.isPublic(modifiers) || Modifier.isFinal(modifiers)) {
                // We want to overwrite also private and final fields. This allows field access
                // for this instance of the field. The actual field of the class isn't modified.
                field.setAccessible(true);
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

    protected boolean hasIllegalAccessModifiers(int modifiers) {
        return Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers);
    }

    protected boolean isEmpty(@NonNull final Field field, @NonNull final OverwritablePojo<T> pojo) {
        try {
            Object value = field.get(pojo);

            if (value == null) {
                return true;
            } else if (value instanceof String) {
                return isEmpty((String) value);
            } else if (value instanceof Boolean) {
                return false;
            } else if (value instanceof Long) {
                return isEmpty((Long) value);
            } else if (value instanceof Integer) {
                return isEmpty((Integer) value);
            } else if (value instanceof Double) {
                return isEmpty((Double) value);
            } else if (value instanceof Float) {
                return isEmpty((Float) value);
            } else if (value instanceof Short) {
                return isEmpty((Short) value);
            } else if (value instanceof Byte) {
                return isEmpty((Byte) value);
            } else if (value instanceof Character) {
                return isEmpty((Character) value);
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
        return false;
    }

    protected boolean isEmpty(long value) {
        return false;
    }

    protected boolean isEmpty(int value) {
        return false;
    }

    protected boolean isEmpty(double value) {
        return false;
    }

    protected boolean isEmpty(float value) {
        return false;
    }

    protected boolean isEmpty(short value) {
        return false;
    }

    protected boolean isEmpty(byte value) {
        return false;
    }

    protected boolean isEmpty(char value) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return getTypeParameterClass().isInstance(o);
    }
}
