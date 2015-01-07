package com.tehmou.rxbookapp.pojo;

/**
 * Created by ttuo on 06/01/15.
 */
public class GitHubRepository {
    final private String name;
    final private String fullName;

    public GitHubRepository(String name, String fullName) {
        this.name = name;
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return name;
    }
}
