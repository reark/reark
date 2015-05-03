package com.tehmou.rxbookapp.data.provider;

import android.net.Uri;

import com.tehmou.rxbookapp.data.base.contract.SerializedJsonContract;
import com.tehmou.rxbookapp.data.base.route.DatabaseRouteBase;

/**
 * Created by ttuo on 04/05/15.
 */
public class UserSettingsSingleRoute extends DatabaseRouteBase {
    private static final String SINGLE_MIME_TYPE =
            "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.usersettings";

    public UserSettingsSingleRoute(final String tableName) {
        super(tableName);
    }

    public String getDefaultSortOrder() {
        return SerializedJsonContract.ID + " ASC";
    }

    @Override
    public String getMimeType() {
        return SINGLE_MIME_TYPE;
    }

    public String getWhere(Uri uri) {
        return SerializedJsonContract.ID + " = " + uri.getLastPathSegment();
    }

    @Override
    public String getPath() {
        return tableName + "/*";
    }
}
