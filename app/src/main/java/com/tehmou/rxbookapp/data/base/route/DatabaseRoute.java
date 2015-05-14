package com.tehmou.rxbookapp.data.base.route;

import android.net.Uri;

/**
 * Created by ttuo on 04/05/15.
 */
public interface DatabaseRoute {
    String getPath();
    String getTableName();
    String getDefaultSortOrder();
    String getWhere(Uri uri);
    String getMimeType();
}
