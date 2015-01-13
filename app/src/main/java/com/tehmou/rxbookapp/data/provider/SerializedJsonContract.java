package com.tehmou.rxbookapp.data.provider;

/**
 * Created by ttuo on 13/01/15.
 */
abstract public class SerializedJsonContract implements DatabaseContract {
    public static final String ID = "id";
    public static final String JSON = "json";

    public static final String[] PROJECTION = new String[]{ID, JSON};

    public String getDefaultSortOrder() {
        return ID + " ASC";
    }

    public String getCreateTable() {
        return " CREATE TABLE " + getTableName()
                + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + JSON + " TEXT NOT NULL);";
    }

    public String getDropTable() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getName() {
        return getTableName();
    }

    @Override
    public String getIdColumnName() {
        return ID;
    }

    abstract protected String getTableName();
}
