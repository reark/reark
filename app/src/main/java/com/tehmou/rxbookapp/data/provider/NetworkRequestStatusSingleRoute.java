package com.tehmou.rxbookapp.data.provider;

import com.tehmou.rxbookapp.data.base.contract.SerializedJsonContract;
import com.tehmou.rxbookapp.data.base.route.DatabaseRouteBase;

import android.net.Uri;

/**
 * Created by ttuo on 04/05/15.
 */
public class NetworkRequestStatusSingleRoute extends DatabaseRouteBase {
    private static final String MULTIPLE_MIME_TYPE =
            "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.networkrequeststatus";

    public NetworkRequestStatusSingleRoute(final String tableName) {
        super(tableName);
    }

    @Override
    public String getMimeType() {
        return MULTIPLE_MIME_TYPE;
    }

    @Override
    public String getWhere(Uri uri) {
        int uriHash = Integer.parseInt(uri.getLastPathSegment());
        return NetworkRequestStatusContract.ID + " = " + uriHash;
    }

    public String getDefaultSortOrder() {
        return SerializedJsonContract.ID + " ASC";
    }

    @Override
    public String getPath() {
        return tableName + "/*";
    }
}
