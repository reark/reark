package com.tehmou.rxbookapp.data.base.contract;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by ttuo on 13/01/15.
 */
public interface DatabaseContract<T> {
    public String getName();
    public String getDefaultSortOrder();
    public String getCreateTable();
    public String getDropTable();
    public String getSingleMimeType();
    public String getMultipleMimeType();
    public T read(Cursor cursor);
    public ContentValues getContentValuesForItem(T item);
    public String getWhere(Uri uri);
    public String[] getProjection();
}
