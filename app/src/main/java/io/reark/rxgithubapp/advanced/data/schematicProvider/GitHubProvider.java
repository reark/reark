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

import android.net.Uri;
import android.net.Uri.Builder;
import android.support.annotation.NonNull;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import static io.reark.reark.utils.Preconditions.get;

@ContentProvider(authority = GitHubProvider.AUTHORITY, database = GitHubDatabase.class)
public final class GitHubProvider {
    public static final String AUTHORITY = "io.reark.rxgithubapp.advanced.data.schematicProvider.GitHubProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static Uri buildUri(@NonNull final String... paths) {
        Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }

        return get(builder.build());
    }

    @TableEndpoint(table = GitHubDatabase.GITHUB_REPOSITORIES)
    public static final class GitHubRepositories {
        @ContentUri(
                path = GitHubDatabase.GITHUB_REPOSITORIES,
                type = "vnd.android.cursor.dir/vnd.io.reark.rxgithubapp.repository",
                defaultSort = JsonIdColumns.ID + " ASC")
        public static final Uri GITHUB_REPOSITORIES = Uri.parse("content://" + AUTHORITY + "/" + GitHubDatabase.GITHUB_REPOSITORIES);

        @InexactContentUri(
                path = GitHubDatabase.GITHUB_REPOSITORIES + "/*",
                name = "GITHUB_REPOSITORIES_ID",
                type = "vnd.android.cursor.item/vnd.io.reark.rxgithubapp.repository",
                whereColumn = GitHubRepositoryColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(GitHubDatabase.GITHUB_REPOSITORIES, String.valueOf(id));
        }

        public static long fromUri(Uri uri) {
            String lastSegment = uri.getLastPathSegment();
            if (GitHubDatabase.GITHUB_REPOSITORIES.equals(lastSegment)) {
                return 0;
            } else {
                return Long.valueOf(uri.getLastPathSegment());
            }
        }
    }

    @TableEndpoint(table = GitHubDatabase.GITHUB_REPOSITORY_SEARCHES)
    public static final class GitHubRepositorySearches {
        @ContentUri(
                path = GitHubDatabase.GITHUB_REPOSITORY_SEARCHES,
                type = "vnd.android.cursor.dir/vnd.io.reark.rxgithubapp.repositorysearch",
                defaultSort = GitHubRepositorySearchColumns.SEARCH + " ASC")
        public static final Uri GITHUB_REPOSITORY_SEARCHES = Uri.parse("content://" + AUTHORITY + "/" + GitHubDatabase.GITHUB_REPOSITORY_SEARCHES);

        @InexactContentUri(
                path = GitHubDatabase.GITHUB_REPOSITORY_SEARCHES + "/*",
                name = "GITHUB_REPOSITORY_SEARCHES_SEARCH",
                type = "vnd.android.cursor.item/vnd.io.reark.rxgithubapp.repositorysearch",
                whereColumn = GitHubRepositorySearchColumns.SEARCH,
                pathSegment = 1)
        public static Uri withSearch(String search) {
            return buildUri(GitHubDatabase.GITHUB_REPOSITORY_SEARCHES, search);
        }

        public static String fromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    @TableEndpoint(table = GitHubDatabase.USER_SETTINGS)
    public static final class UserSettings {
        @ContentUri(
                path = GitHubDatabase.USER_SETTINGS,
                type = "vnd.android.cursor.dir/vnd.io.reark.rxgithubapp.usersettings",
                defaultSort = JsonIdColumns.ID + " ASC")
        public static final Uri USER_SETTINGS = Uri.parse("content://" + AUTHORITY + "/" + GitHubDatabase.USER_SETTINGS);

        @InexactContentUri(
                path = GitHubDatabase.USER_SETTINGS + "/#",
                name = "USER_SETTINGS_ID",
                type = "vnd.android.cursor.item/vnd.io.reark.rxgithubapp.usersettings",
                whereColumn = UserSettingsColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(GitHubDatabase.USER_SETTINGS, String.valueOf(id));
        }

        public static long fromUri(Uri uri) {
            return Long.valueOf(uri.getLastPathSegment());
        }
    }

    @TableEndpoint(table = GitHubDatabase.NETWORK_REQUEST_STATUSES)
    public static final class NetworkRequestStatuses {
        @ContentUri(
                path = GitHubDatabase.NETWORK_REQUEST_STATUSES,
                type = "vnd.android.cursor.dir/vnd.io.reark.rxgithubapp.networkrequeststatus",
                defaultSort = JsonIdColumns.ID + " ASC")
        public static final Uri NETWORK_REQUEST_STATUSES = Uri.parse("content://" + AUTHORITY + "/" + GitHubDatabase.NETWORK_REQUEST_STATUSES);

        @InexactContentUri(
                path = GitHubDatabase.NETWORK_REQUEST_STATUSES + "/*",
                name = "NETWORK_REQUEST_STATUSES_ID",
                type = "vnd.android.cursor.item/vnd.io.reark.rxgithubapp.networkrequeststatus",
                whereColumn = NetworkRequestStatusColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(GitHubDatabase.NETWORK_REQUEST_STATUSES, String.valueOf(id));
        }

        public static long fromUri(Uri uri) {
            return Long.valueOf(uri.getLastPathSegment());
        }
    }
}
