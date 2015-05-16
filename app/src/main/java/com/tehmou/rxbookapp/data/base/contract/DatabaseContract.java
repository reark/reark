package com.tehmou.rxbookapp.data.base.contract;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by ttuo on 13/01/15.
 */
public interface DatabaseContract<T> {
    public String getCreateTable();
    public String getDropTable();
    public String getTableName();
    public T read(Cursor cursor);
    public ContentValues getContentValuesForItem(T item);
    public String[] getProjection();
}
