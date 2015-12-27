package io.reark.reark.pojo;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


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

    protected abstract Class<T> getTypeParameterClass();

    public T overwrite(T other) {
        if (equals(other)) {
            return (T) this;
        }

        for (Field field : getTypeParameterClass().getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)) {
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

    protected boolean isEmpty(Field field, IdentifiablePojo pojo) {
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
