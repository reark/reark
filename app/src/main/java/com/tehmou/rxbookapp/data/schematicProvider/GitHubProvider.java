package com.tehmou.rxbookapp.data.schematicProvider;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by ttuo on 14/07/15.
 */
@ContentProvider(authority = GitHubProvider.AUTHORITY, database = GitHubDatabase.class)
public class GitHubProvider {
    public static final String AUTHORITY = "com.tehmou.rxbookapp.data.schematicProvider.GitHubProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = GitHubDatabase.GITHUB_REPOSITORIES) public static class GitHubRepositories {
        @ContentUri(
                path = GitHubDatabase.GITHUB_REPOSITORIES,
                type = "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.repository",
                defaultSort = JsonIdColumns.ID + " ASC")
        public static final Uri GITHUB_REPOSITORIES = Uri.parse("content://" + AUTHORITY + "/" + GitHubDatabase.GITHUB_REPOSITORIES);

        @InexactContentUri(
                path = GitHubDatabase.GITHUB_REPOSITORIES + "/*",
                name = "GITHUB_REPOSITORIES_ID",
                type = "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.repository",
                whereColumn = GitHubRepositoryColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(GitHubDatabase.GITHUB_REPOSITORIES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = GitHubDatabase.GITHUB_REPOSITORY_SEARCHES) public static class GitHubRepositorySearches {
        @ContentUri(
                path = GitHubDatabase.GITHUB_REPOSITORY_SEARCHES,
                type = "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.repositorysearch",
                defaultSort = GitHubRepositorySearchColumns.SEARCH + " ASC")
        public static final Uri GITHUB_REPOSITORY_SEARCHES = Uri.parse("content://" + AUTHORITY + "/" + GitHubDatabase.GITHUB_REPOSITORY_SEARCHES);

        @InexactContentUri(
                path = GitHubDatabase.GITHUB_REPOSITORY_SEARCHES + "/*",
                name = "GITHUB_REPOSITORY_SEARCHES_SEARCH",
                type = "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.repositorysearch",
                whereColumn = GitHubRepositorySearchColumns.SEARCH,
                pathSegment = 1)
        public static Uri withSearch(String search) {
            return buildUri(GitHubDatabase.GITHUB_REPOSITORY_SEARCHES, search);
        }
    }

    @TableEndpoint(table = GitHubDatabase.USER_SETTINGS) public static class UserSettings {
        @ContentUri(
                path = GitHubDatabase.USER_SETTINGS,
                type = "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.usersettings",
                defaultSort = JsonIdColumns.ID + " ASC")
        public static final Uri USER_SETTINGS = Uri.parse("content://" + AUTHORITY + "/" + GitHubDatabase.USER_SETTINGS);

        @InexactContentUri(
                path = GitHubDatabase.USER_SETTINGS + "/#",
                name = "USER_SETTINGS_ID",
                type = "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.usersettings",
                whereColumn = UserSettingsColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(GitHubDatabase.USER_SETTINGS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = GitHubDatabase.NETWORK_REQUEST_STATUSES) public static class NetworkRequestStatuses {
        @ContentUri(
                path = GitHubDatabase.NETWORK_REQUEST_STATUSES,
                type = "vnd.android.cursor.dir/vnd.tehmou.android.rxbookapp.networkrequeststatus",
                defaultSort = JsonIdColumns.ID + " ASC")
        public static final Uri NETWORK_REQUEST_STATUSES = Uri.parse("content://" + AUTHORITY + "/" + GitHubDatabase.NETWORK_REQUEST_STATUSES);

        @InexactContentUri(
                path = GitHubDatabase.NETWORK_REQUEST_STATUSES + "/*",
                name = "NETWORK_REQUEST_STATUSES_ID",
                type = "vnd.android.cursor.item/vnd.tehmou.android.rxbookapp.networkrequeststatus",
                whereColumn = NetworkRequestStatusColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(GitHubDatabase.NETWORK_REQUEST_STATUSES, String.valueOf(id));
        }
    }
}
