package com.tehmou.rxbookapp.data.provider;

import com.tehmou.rxbookapp.data.base.contract.SerializedJsonContract;
import com.tehmou.rxbookapp.data.base.route.DatabaseRouteBase;

import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by ttuo on 04/05/15.
 */
public class NetworkRequestStatusSingleRoute extends DatabaseRouteBase {
    private static final String MULTIPLE_MIME_TYPE =
            "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.networkrequeststatus";

    public NetworkRequestStatusSingleRoute(@NonNull final String tableName) {
        super(tableName);
    }

    @NonNull
    @Override
    public String getMimeType() {
        return MULTIPLE_MIME_TYPE;
    }

    @NonNull
    @Override
    public String getWhere(Uri uri) {
        int uriHash = Integer.parseInt(uri.getLastPathSegment());
        return NetworkRequestStatusContract.ID + " = " + uriHash;
    }

    @NonNull
    public String getDefaultSortOrder() {
        return SerializedJsonContract.ID + " ASC";
    }

    @NonNull
    @Override
    public String getPath() {
        return tableName + "/*";
    }
}
