package com.tehmou.rxbookapp.data.base.contract;

import android.database.Cursor;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by ttuo on 13/01/15.
 */
abstract public class SerializedJsonContract<T> implements DatabaseContract<T> {
    private static final String TAG = SerializedJsonContract.class.getSimpleName();

    public static final String ID = "id";
    public static final String JSON = "json";

    private static final String[] PROJECTION = new String[]{ ID, JSON };

    public String getDefaultSortOrder() {
        return ID + " ASC";
    }

    public String getCreateTable() {
        return " CREATE TABLE " + getTableName()
                + " ( " + getCreateIdColumn() + ", "
                + JSON + " TEXT NOT NULL);";
    }

    public String getDropTable() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getName() {
        return getTableName();
    }

    @Override
    public String[] getProjection() {
        return PROJECTION;
    }

    abstract protected String getTableName();
    abstract protected String getCreateIdColumn();
    abstract protected Type getType();

    @Override
    public T read(Cursor cursor) {
        if (cursor.moveToFirst()) {
            final String json = cursor.getString(cursor.getColumnIndex(SerializedJsonContract.JSON));
            return new Gson().fromJson(json, getType());
        }
        return null;
    }
}
