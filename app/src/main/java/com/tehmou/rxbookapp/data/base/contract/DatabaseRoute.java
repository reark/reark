package com.tehmou.rxbookapp.data.base.contract;

import android.net.Uri;

/**
 * Created by ttuo on 04/05/15.
 */
public interface DatabaseRoute {
    public String getPath();
    public String getTableName();
    public String getDefaultSortOrder();
    public String getWhere(Uri uri);
    public String getMimeType();
}
