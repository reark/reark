package com.tehmou.rxbookapp.data.provider;

import android.net.Uri;

import com.tehmou.rxbookapp.data.base.route.DatabaseRouteBase;

import java.util.List;

/**
 * Created by ttuo on 04/05/15.
 */
public class NetworkRequestStatusSingleRoute extends DatabaseRouteBase {
    private static final String SINGLE_MIME_TYPE =
            "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.networkrequeststatus";


    public NetworkRequestStatusSingleRoute(final String tableName) {
        super(tableName);
    }

    @Override
    public String getMimeType() {
        return SINGLE_MIME_TYPE;
    }

    @Override
    public String getWhere(Uri uri) {
        List<String> pathSegments = uri.getPathSegments();
        String owner = pathSegments.get(pathSegments.size() - 2);
        int uriHash = Integer.getInteger(pathSegments.get(pathSegments.size() - 1));
        return NetworkRequestStatusContract.OWNER + " = '" + owner +
                "' AND " + NetworkRequestStatusContract.URI_HASH + " = " + uriHash;
    }

    @Override
    public String getDefaultSortOrder() {
        return "DESC";
    }

    @Override
    public String getPath() {
        return tableName + "/*";
    }
}
