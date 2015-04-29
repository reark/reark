package com.tehmou.rxbookapp.data.provider;

/**
 * Created by ttuo on 22/03/15.
 */
public class GithubContentProvider extends ContractContentProviderBase {
    public static final String PROVIDER_NAME = "com.tehmou.rxbookapp.data.provider.GithubContentProvider";
    private static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 9;

    public GithubContentProvider() {
        databaseContracts.add(new GitHubRepositoryContract());
        databaseContracts.add(new GitHubRepositorySearchContract());
        databaseContracts.add(new UserSettingsContract());
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
