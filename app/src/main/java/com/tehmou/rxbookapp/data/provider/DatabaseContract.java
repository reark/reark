package com.tehmou.rxbookapp.data.provider;

/**
 * Created by ttuo on 13/01/15.
 */
public interface DatabaseContract {
    public String getName();
    public String getDefaultSortOrder();
    public String getIdColumnName();
    public String getIdSqlString(String id);
    public String getCreateTable();
    public String getDropTable();
    public String getSingleMimeType();
    public String getMultipleMimeType();
}
