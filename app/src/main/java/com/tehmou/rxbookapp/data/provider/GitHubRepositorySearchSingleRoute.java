package com.tehmou.rxbookapp.data.provider;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.tehmou.rxbookapp.data.base.contract.SerializedJsonContract;
import com.tehmou.rxbookapp.data.base.route.DatabaseRouteBase;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 04/05/15.
 */
public class GitHubRepositorySearchSingleRoute extends DatabaseRouteBase {
    private static final String SINGLE_MIME_TYPE =
            "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.repositorysearch";

    public GitHubRepositorySearchSingleRoute(@NonNull final String tableName) {
        super(tableName);
    }

    @NonNull
    public String getDefaultSortOrder() {
        return SerializedJsonContract.ID + " ASC";
    }

    @NonNull
    @Override
    public String getMimeType() {
        return SINGLE_MIME_TYPE;
    }

    @NonNull
    public String getWhere(@NonNull Uri uri) {
        Preconditions.checkNotNull(uri, "URI cannot be null.");

        return SerializedJsonContract.ID + " = '" + uri.getLastPathSegment() + "'";
    }

    @NonNull
    @Override
    public String getPath() {
        return tableName + "/*";
    }
}
