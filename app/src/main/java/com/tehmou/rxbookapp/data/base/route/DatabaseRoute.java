package com.tehmou.rxbookapp.data.base.route;

import android.net.Uri;
import android.support.annotation.NonNull;

import rx.functions.Action1;

/**
 * Created by ttuo on 04/05/15.
 */
public interface DatabaseRoute {
    @NonNull String getPath();
    @NonNull String getTableName();
    @NonNull String getDefaultSortOrder();
    @NonNull String getWhere(Uri uri);
    @NonNull String getMimeType();
    void notifyChange(@NonNull Uri uri, @NonNull Action1<Uri> notifyChange);
}
