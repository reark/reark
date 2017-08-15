/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.rxgithubapp.advanced.data.schematicProvider;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

import io.reark.reark.utils.Log;

@Database(version = GitHubDatabase.VERSION)
public final class GitHubDatabase {

    private static final String TAG = GitHubDatabase.class.getSimpleName();

    public static final int VERSION = 2;

    @Table(GitHubRepositoryColumns.class) public static final String GITHUB_REPOSITORIES = "repositories";
    @Table(GitHubRepositorySearchColumns.class) public static final String GITHUB_REPOSITORY_SEARCHES = "repositorySearches";
    @Table(NetworkRequestStatusColumns.class) public static final String NETWORK_REQUEST_STATUSES = "networkRequestStatuses";
    @Table(UserSettingsColumns.class) public static final String USER_SETTINGS = "userSettings";

    private static final String[] MIGRATIONS = {
            // Version 1 -> 2: request format was changed
            "DELETE FROM " + NETWORK_REQUEST_STATUSES + ";"
    };

    @OnUpgrade
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) {
            String migration = MIGRATIONS[i - 1];
            db.beginTransaction();

            try {
                db.execSQL(migration);
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                Log.e(TAG, String.format("Error executing database migration: %s", migration), e);
            } finally {
                db.endTransaction();
            }
        }
    }

}
