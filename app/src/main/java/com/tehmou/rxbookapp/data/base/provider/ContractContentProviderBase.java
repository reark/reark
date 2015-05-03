package com.tehmou.rxbookapp.data.base.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.tehmou.rxbookapp.data.base.contract.DatabaseContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttuo on 10/01/15.
 */
abstract public class ContractContentProviderBase extends ContentProviderBase {
    private final List<DatabaseContract> databaseContracts = new ArrayList<>();

    protected void addDatabaseContract(DatabaseContract databaseContract) {
        assert(databaseHelper == null);
        databaseContracts.add(databaseContract);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        final List<DatabaseContract> databaseContracts;

        DatabaseHelper(Context context,
                       String databaseName,
                       int databaseVersion,
                       List<DatabaseContract> databaseContracts) {
            super(context, databaseName, null, databaseVersion);
            this.databaseContracts = databaseContracts;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for (DatabaseContract databaseContract : databaseContracts) {
                db.execSQL(databaseContract.getCreateTable());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            for (DatabaseContract databaseContract : databaseContracts) {
                db.execSQL(databaseContract.getDropTable());
            }
            onCreate(db);
        }
    }

    @Override
    protected String getDefaultSortOrder(int match) {
        return getDatabaseContractForMatch(match).getDefaultSortOrder();
    }

    @Override
    protected String getTableName(int match) {
        return getDatabaseContractForMatch(match).getName();
    }

    @Override
    protected String getWhere(int match, Uri uri) {
        return getDatabaseContractForMatch(match).getWhere(uri);
    }

    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        final DatabaseContract databaseContract = getDatabaseContractForMatch(match);
        final boolean isIdUri = isIdUri(match);
        return isIdUri ?
                databaseContract.getSingleMimeType() : databaseContract.getMultipleMimeType();
    }

    @Override
    protected SQLiteOpenHelper createDatabaseHelper(Context context) {
        return new DatabaseHelper(context, getDatabaseName(),
                getDatabaseVersion(), databaseContracts);
    }

    @Override
    protected void createUriMatcher() {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        int i = 0;
        for (DatabaseContract databaseContract : databaseContracts) {
            URI_MATCHER.addURI(getProviderName(), databaseContract.getName(), i++);
            URI_MATCHER.addURI(getProviderName(), databaseContract.getName() + "/*", i++);
        }
    }

    protected DatabaseContract getDatabaseContractForMatch(final int match) {
        if (match == -1) {
            throw new IllegalArgumentException("Unknown URI");
        }
        return databaseContracts.get(match / 2);
    }

    private boolean isIdUri(final int match) {
        return match % 2 != 0;
    }

    abstract protected String getProviderName();
    abstract protected String getDatabaseName();
    abstract protected int getDatabaseVersion();
}
