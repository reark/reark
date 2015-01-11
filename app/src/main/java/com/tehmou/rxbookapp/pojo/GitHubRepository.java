package com.tehmou.rxbookapp.pojo;

/**
 * Created by ttuo on 06/01/15.
 */
public class GitHubRepository {
    final private String name;

    public GitHubRepository(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
