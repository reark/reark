package com.tehmou.rxbookapp.data.provider;

import com.tehmou.rxbookapp.data.base.contract.DatabaseContract;
import com.tehmou.rxbookapp.data.base.provider.ContractContentProviderBase;

/**
 * Created by ttuo on 22/03/15.
 */
public class GithubContentProvider extends ContractContentProviderBase {
    public static final String PROVIDER_NAME = "com.tehmou.rxbookapp.data.provider.GithubContentProvider";
    private static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 12;

    public GithubContentProvider() {
        DatabaseContract gitHubRepositoryContract = new GitHubRepositoryContract();
        addDatabaseContract(gitHubRepositoryContract);
        addDatabaseRoute(
                new GitHubRepositorySingleRoute(gitHubRepositoryContract.getTableName()));

        DatabaseContract gitHubRepositorySearchContract = new GitHubRepositorySearchContract();
        addDatabaseContract(gitHubRepositorySearchContract);
        addDatabaseRoute(
                new GitHubRepositorySearchSingleRoute(gitHubRepositorySearchContract.getTableName()));

        DatabaseContract userSettingsContract = new UserSettingsContract();
        addDatabaseContract(userSettingsContract);
        addDatabaseRoute(
                new UserSettingsSingleRoute(userSettingsContract.getTableName()));

        DatabaseContract networkRequestStatusContract = new NetworkRequestStatusContract();
        addDatabaseContract(networkRequestStatusContract);
        addDatabaseRoute(
                new NetworkRequestStatusSingleRoute(networkRequestStatusContract.getTableName()));
    }

    @Override
    protected String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    protected String getDatabaseName() {
        return DATABASE_NAME;
    }

    @Override
    protected int getDatabaseVersion() {
        return DATABASE_VERSION;
    }
}
