package com.tehmou.rxbookapp.pojo;

/**
 * Created by ttuo on 06/01/15.
 */
public class GitHubRepository {
    final private int id;
    final private String name;

    public GitHubRepository(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
