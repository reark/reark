package com.tehmou.rxbookapp.data.base.contract;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by ttuo on 13/01/15.
 */
public interface DatabaseContract<T> {
    String getCreateTable();
    String getDropTable();
    String getTableName();
    T read(Cursor cursor);
    ContentValues getContentValuesForItem(T item);
    String[] getProjection();
}
