package com.tehmou.rxbookapp.data.base.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tehmou.rxbookapp.data.base.contract.DatabaseContract;
import com.tehmou.rxbookapp.data.base.route.DatabaseRoute;

import java.util.ArrayList;
import java.util.List;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 10/01/15.
 */
abstract public class ContractContentProviderBase extends ContentProviderBase {
    private final List<DatabaseContract> databaseContracts = new ArrayList<>();
    private final List<DatabaseRoute> databaseRoutes = new ArrayList<>();

    protected void addDatabaseContract(@NonNull DatabaseContract databaseContract) {
        Preconditions.checkNotNull(databaseContract, "Database Contract cannot be null.");

        databaseContracts.add(databaseContract);
    }

    protected void addDatabaseRoute(@NonNull DatabaseRoute databaseRoute) {
        Preconditions.checkNotNull(databaseRoute, "Database Route cannot be null.");

        databaseRoutes.add(databaseRoute);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        @NonNull
        final List<DatabaseContract> databaseContracts;

        DatabaseHelper(@NonNull Context context,
                       @NonNull String databaseName,
                       int databaseVersion,
                       @NonNull List<DatabaseContract> databaseContracts) {
            super(context, databaseName, null, databaseVersion);
            Preconditions.checkNotNull(databaseContracts, "Database Contract cannot be null.");

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

    @NonNull
    @Override
    protected String getDefaultSortOrder(int match) {
        return getDatabaseRouteForMatch(match).getDefaultSortOrder();
    }

    @NonNull
    @Override
    protected String getTableName(int match) {
        return getDatabaseRouteForMatch(match).getTableName();
    }

    @Nullable
    @Override
    protected String getWhere(int match, Uri uri) {
        return getDatabaseRouteForMatch(match).getWhere(uri);
    }

    @NonNull
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = URI_MATCHER.match(uri);
        return getDatabaseRouteForMatch(match).getMimeType();
    }

    @NonNull
    @Override
    protected SQLiteOpenHelper createDatabaseHelper(@NonNull Context context) {
        Preconditions.checkNotNull(context, "Context cannot be null.");

        return new DatabaseHelper(context, getDatabaseName(),
                getDatabaseVersion(), databaseContracts);
    }

    @Override
    protected void createUriMatcher() {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        int i = 0;
        for (DatabaseRoute databaseRoute : databaseRoutes) {
            URI_MATCHER.addURI(getProviderName(), databaseRoute.getPath(), i++);
        }
    }

    @NonNull
    protected DatabaseRoute getDatabaseRouteForMatch(final int match) {
        if (match == -1) {
            throw new IllegalArgumentException("Unknown URI");
        }
        return databaseRoutes.get(match);
    }

    @Override
    protected void notifyChange(int match, @NonNull Uri uri) {
        Preconditions.checkNotNull(uri, "URI cannot be null.");

        getDatabaseRouteForMatch(match).notifyChange(uri,
                (value) -> getContext().getContentResolver().notifyChange(value, null));
    }

    @NonNull
    abstract protected String getProviderName();

    @NonNull
    abstract protected String getDatabaseName();

    abstract protected int getDatabaseVersion();
}
