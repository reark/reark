package com.tehmou.rxbookapp.data.provider;

import android.net.Uri;
import android.util.Log;

import com.tehmou.rxbookapp.data.base.route.DatabaseRouteBase;

import java.util.List;

/**
 * Created by ttuo on 04/05/15.
 */
public class NetworkRequestStatusByOwnerAndUriHashRoute extends DatabaseRouteBase {
    private static final String TAG = NetworkRequestStatusByOwnerAndUriHashRoute.class.getSimpleName();

    private static final String SINGLE_MIME_TYPE =
            "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.networkrequeststatus";

    public NetworkRequestStatusByOwnerAndUriHashRoute(final String tableName) {
        super(tableName);
    }

    @Override
    public String getMimeType() {
        return SINGLE_MIME_TYPE;
    }

    @Override
    public String getWhere(Uri uri) {
        List<String> pathSegments = uri.getPathSegments();
        String owner = pathSegments.get(pathSegments.size() - 3);
        String uriHashString = pathSegments.get(pathSegments.size() - 1);
        int uriHash = Integer.parseInt(uriHashString);
        final String query = NetworkRequestStatusContract.OWNER + " = '" + owner +
                "' AND " + NetworkRequestStatusContract.URI_HASH + " = " + uriHash;
        Log.v(TAG, query);
        return query;
    }

    @Override
    public String getDefaultSortOrder() {
        return "DESC";
    }

    @Override
    public String getPath() {
        return tableName + "/owner/*/uri_hash/#";
    }
}
