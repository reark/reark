package com.tehmou.rxbookapp.data.base.route;

/**
 * Created by ttuo on 04/05/15.
 */
public abstract class DatabaseRouteBase implements DatabaseRoute {
    protected final String tableName;

    public DatabaseRouteBase(final String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getTableName() {
        return tableName;
    }
}
