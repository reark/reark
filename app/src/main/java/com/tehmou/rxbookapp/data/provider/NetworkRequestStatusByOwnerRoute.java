package com.tehmou.rxbookapp.data.provider;

import android.net.Uri;
import android.util.Log;

import com.tehmou.rxbookapp.data.base.route.DatabaseRouteBase;

import java.util.List;

/**
 * Created by ttuo on 04/05/15.
 */
public class NetworkRequestStatusByOwnerRoute extends DatabaseRouteBase {
    private static final String MULTIPLE_MIME_TYPE =
            "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.networkrequeststatus";

    private static final String TAG = NetworkRequestStatusByOwnerRoute.class.getSimpleName();

    public NetworkRequestStatusByOwnerRoute(final String tableName) {
        super(tableName);
    }

    @Override
    public String getMimeType() {
        return MULTIPLE_MIME_TYPE;
    }

    @Override
    public String getWhere(Uri uri) {
        String owner = uri.getLastPathSegment();
        return NetworkRequestStatusContract.OWNER + " = '" + owner + "'";
    }

    @Override
    public String getDefaultSortOrder() {
        return "DESC";
    }

    @Override
    public String getPath() {
        return tableName + "/owner/*";
    }
}
