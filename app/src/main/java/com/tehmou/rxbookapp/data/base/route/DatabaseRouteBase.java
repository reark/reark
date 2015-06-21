package com.tehmou.rxbookapp.data.base.route;

import android.net.Uri;
import android.support.annotation.NonNull;

import rx.android.internal.Preconditions;
import rx.functions.Action1;

/**
 * Created by ttuo on 04/05/15.
 */
public abstract class DatabaseRouteBase implements DatabaseRoute {

    @NonNull
    protected final String tableName;

    public DatabaseRouteBase(@NonNull final String tableName) {
        Preconditions.checkNotNull(tableName, "Table Name cannot be null.");

        this.tableName = tableName;
    }

    @NonNull
    @Override
    public String getTableName() {
        return tableName;
    }

    public void notifyChange(@NonNull Uri uri, @NonNull Action1<Uri> notifyChange) {
        Preconditions.checkNotNull(uri, "URI cannot be null.");
        Preconditions.checkNotNull(notifyChange, "Notify Change function cannot be null.");

        notifyChange.call(uri);
    }
}
