package com.tehmou.rxbookapp.pojo;

import com.google.gson.annotations.SerializedName;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tehmou.rxandroidarchitecture.utils.Preconditions;

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

    @Nullable
    @SerializedName("owner")
    final private GitHubOwner owner;

    @SuppressWarnings("NullableProblems")
    public GitHubRepository(int id,
                            String name,
                            int stargazersCount,
                            int forksCount,
                            @NonNull GitHubOwner owner) {
        Preconditions.checkNotNull(owner, "Owner cannot be null.");

        this.name = name;
        this.id = id;
        this.stargazersCount = stargazersCount;
        this.forksCount = forksCount;
        this.owner = owner;
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

    @NonNull
    public GitHubOwner getOwner() {
        return owner == null ? new GitHubOwner() : owner;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GitHubRepository{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", stargazersCount=").append(stargazersCount);
        sb.append(", forksCount=").append(forksCount);
        sb.append(", owner=").append(owner);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GitHubRepository)) {
            return false;
        }

        GitHubRepository that = (GitHubRepository) o;

        if (id != that.id) {
            return false;
        }
        if (stargazersCount != that.stargazersCount) {
            return false;
        }
        if (forksCount != that.forksCount) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return !(owner != null ? !owner.equals(that.owner) : that.owner != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + stargazersCount;
        result = 31 * result + forksCount;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }

}
