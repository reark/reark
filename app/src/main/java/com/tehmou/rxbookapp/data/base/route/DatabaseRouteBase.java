package com.tehmou.rxbookapp.data.base.route;

import android.net.Uri;

import rx.functions.Action1;

/**
 * Created by ttuo on 04/05/15.
 */
public abstract class DatabaseRouteBase implements DatabaseRoute {
    protected final String tableName;

    public DatabaseRouteBase(final String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    public void notifyChange(Uri uri, Action1<Uri> notifyChange) {
        notifyChange.call(uri);
    }
}
