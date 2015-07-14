package com.tehmou.rxbookapp.data.schematicProvider;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by ttuo on 14/07/15.
 */
@Database(version = GitHubDatabase.VERSION)
public final class GitHubDatabase {
    public static final int VERSION = 1;

    @Table(GitHubRepositoryColumns.class) public static final String GITHUB_REPOSITORIES = "repositories";
    @Table(GitHubRepositorySearchColumns.class) public static final String GITHUB_REPOSITORY_SEARCHES = "repositorySearches";
    @Table(NetworkRequestStatusColumns.class) public static final String NETWORK_REQUEST_STATUSES = "networkRequestStatuses";
    @Table(UserSettingsColumns.class) public static final String USER_SETTINGS = "userSettings";
}
