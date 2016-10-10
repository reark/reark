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
package io.reark.rxgithubapp.shared.pojo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import io.reark.reark.pojo.OverwritablePojo;
import io.reark.reark.utils.Preconditions;

public class GitHubRepository extends OverwritablePojo<GitHubRepository> {
    private final int id;
    private final String name;

    @SerializedName("stargazers_count")
    private int stargazersCount;

    @SerializedName("forks_count")
    private int forksCount;

    @Nullable
    @SerializedName("owner")
    private final GitHubOwner owner;

    @SuppressWarnings("NullableProblems")
    public GitHubRepository(int id,
                            String name,
                            int stargazersCount,
                            int forksCount,
                            @NonNull GitHubOwner owner) {
        Preconditions.checkNotNull(owner, "Owner cannot be null.");

        this.id = id;
        this.name = name;
        this.stargazersCount = stargazersCount;
        this.forksCount = forksCount;
        this.owner = owner;
    }

    public GitHubRepository(GitHubRepository gitHubRepository) {
        this(gitHubRepository.getId(),
             gitHubRepository.getName(),
             gitHubRepository.getStargazersCount(),
             gitHubRepository.getForksCount(),
             gitHubRepository.getOwner());
    }

    @NonNull
    @Override
    protected Class<GitHubRepository> getTypeParameterClass() {
        return GitHubRepository.class;
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
