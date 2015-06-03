package com.tehmou.rxbookapp.data.base.route;

import android.net.Uri;

import rx.functions.Action1;

/**
 * Created by ttuo on 04/05/15.
 */
public interface DatabaseRoute {
    String getPath();
    String getTableName();
    String getDefaultSortOrder();
    String getWhere(Uri uri);
    String getMimeType();
    void notifyChange(Uri uri, Action1<Uri> notifyChange);
}
