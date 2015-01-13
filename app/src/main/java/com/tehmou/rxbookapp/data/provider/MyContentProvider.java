package com.tehmou.rxbookapp.data.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttuo on 10/01/15.
 */
public class MyContentProvider extends ContentProviderBase {
    public static final String PROVIDER_NAME = "com.tehmou.rxbookapp.data.provider.MyContentProvider";

    static final List<DatabaseContract> databaseContracts;

    static {
        databaseContracts = new ArrayList<>();
        databaseContracts.add(new GitHubRepositoryContract());
        databaseContracts.add(new GitHubRepositorySearchContract());
    }

    static final String DATABASE_NAME = "database";
    static final int DATABASE_VERSION = 3;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
    protected String getIdColumnName(int match) {
        return getDatabaseContractForMatch(match).getIdColumnName();
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
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        final DatabaseContract databaseContract = getDatabaseContractForMatch(match);
        final boolean isIdUri = isIdUri(match);
        return isIdUri ?
                databaseContract.getSingleMimeType() : databaseContract.getMultipleMimeType();
    }

    @Override
    protected SQLiteOpenHelper createDatabaseHelper(Context context) {
        return new DatabaseHelper(context);
    }

    @Override
    protected void createUriMatcher() {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        int i = 0;
        for (DatabaseContract databaseContract : databaseContracts) {
            URI_MATCHER.addURI(PROVIDER_NAME, databaseContract.getName(), i++);
            URI_MATCHER.addURI(PROVIDER_NAME, databaseContract.getName() + "/#", i++);
        }
    }

    private DatabaseContract getDatabaseContractForMatch(final int match) {
        return databaseContracts.get(match / 2);
    }

    private boolean isIdUri(final int match) {
        return match % 2 != 0;
    }
}
