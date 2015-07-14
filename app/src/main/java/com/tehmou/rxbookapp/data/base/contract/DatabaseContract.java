package com.tehmou.rxbookapp.data.base.contract;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by ttuo on 13/01/15.
 */
public interface DatabaseContract<T> {
    @NonNull String getCreateTable();
    @NonNull String getDropTable();
    @NonNull String getTableName();
    @Nullable T read(Cursor cursor);
    @NonNull ContentValues getContentValuesForItem(T item);
    @NonNull String[] getProjection();
}
