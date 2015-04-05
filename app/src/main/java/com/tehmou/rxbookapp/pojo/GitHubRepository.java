package com.tehmou.rxbookapp.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ttuo on 06/01/15.
 */
public class GitHubRepository {
    final private int id;

    final private String name;

    @SerializedName("stargazers_count")
    final private int stargazersCount;

    @SerializedName("forks_count")
    final private int forksCount;

    public GitHubRepository(int id, String name, int stargazersCount, int forksCount) {
        this.name = name;
        this.id = id;
        this.stargazersCount = stargazersCount;
        this.forksCount = forksCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStargazersCount() {
        return stargazersCount;
    }

    public int getForksCount() {
        return forksCount;
    }

    @Override
    public String toString() {
        return name;
    }
}
