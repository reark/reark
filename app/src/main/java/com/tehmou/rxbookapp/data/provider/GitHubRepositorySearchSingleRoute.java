package com.tehmou.rxbookapp.data.provider;

import android.net.Uri;

import com.tehmou.rxbookapp.data.base.contract.DatabaseRoute;
import com.tehmou.rxbookapp.data.base.contract.SerializedJsonContract;

/**
 * Created by ttuo on 04/05/15.
 */
public class GitHubRepositorySearchSingleRoute implements DatabaseRoute {
    private static final String SINGLE_MIME_TYPE =
            "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.repositorysearch";

    final private String tableName;

    public GitHubRepositorySearchSingleRoute(String tableName) {
        this.tableName = tableName;
    }

    public String getDefaultSortOrder() {
        return SerializedJsonContract.ID + " ASC";
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public String getMimeType() {
        return SINGLE_MIME_TYPE;
    }

    public String getWhere(Uri uri) {
        return SerializedJsonContract.ID + " = '" + uri.getLastPathSegment() + "'";
    }

    @Override
    public String getPath() {
        return tableName + "/*";
    }
}
