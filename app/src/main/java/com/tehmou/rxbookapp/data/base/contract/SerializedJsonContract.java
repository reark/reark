package com.tehmou.rxbookapp.data.base.contract;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 13/01/15.
 */
abstract public class SerializedJsonContract<T> implements DatabaseContract<T> {
    private static final String TAG = SerializedJsonContract.class.getSimpleName();

    public static final String ID = "id";
    public static final String JSON = "json";

    private static final String[] PROJECTION = new String[]{ ID, JSON };

    @NonNull
    public String getCreateTable() {
        return " CREATE TABLE " + getTableName()
                + " ( " + getCreateIdColumn() + ", "
                + JSON + " TEXT NOT NULL);";
    }

    @NonNull
    public String getDropTable() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    @NonNull
    @Override
    public String[] getProjection() {
        return PROJECTION;
    }

    @NonNull
    abstract public String getTableName();

    @NonNull
    abstract protected String getCreateIdColumn();

    @NonNull
    abstract protected Type getType();

    @Nullable
    @Override
    public T read(@NonNull Cursor cursor) {
        Preconditions.checkNotNull(cursor, "Cursor cannot be null.");

        if (cursor.moveToFirst()) {
            final String json = cursor.getString(cursor.getColumnIndex(SerializedJsonContract.JSON));
            return new Gson().fromJson(json, getType());
        }
        return null;
    }
}
