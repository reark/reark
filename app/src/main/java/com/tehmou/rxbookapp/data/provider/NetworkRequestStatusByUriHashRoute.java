package com.tehmou.rxbookapp.data.provider;

import android.net.Uri;

import com.tehmou.rxbookapp.data.base.route.DatabaseRouteBase;

import java.util.List;

/**
 * Created by ttuo on 04/05/15.
 */
public class NetworkRequestStatusByUriHashRoute extends DatabaseRouteBase {
    private static final String MULTIPLE_MIME_TYPE =
            "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.networkrequeststatus";

    public NetworkRequestStatusByUriHashRoute(final String tableName) {
        super(tableName);
    }

    @Override
    public String getMimeType() {
        return MULTIPLE_MIME_TYPE;
    }

    @Override
    public String getWhere(Uri uri) {
        int uriHash = Integer.parseInt(uri.getLastPathSegment());
        return NetworkRequestStatusContract.URI_HASH + " = " + uriHash;
    }

    @Override
    public String getDefaultSortOrder() {
        return "DESC";
    }

    @Override
    public String getPath() {
        return tableName + "/uri_hash/#";
    }
}
