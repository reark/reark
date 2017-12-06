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

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;

import io.reark.reark.pojo.OverwritablePojo;
import io.reark.reark.utils.Log;

import static io.reark.reark.utils.Preconditions.get;

public class GitHubRepository extends OverwritablePojo<GitHubRepository> {
    private static final String TAG = GitHubRepository.class.getSimpleName();

    private final int id;

    @NonNull
    private String name;

    @SerializedName("stargazers_count")
    private int stargazersCount;

    @SerializedName("forks_count")
    private int forksCount;

    @NonNull
    @SerializedName("owner")
    private GitHubOwner owner;

    public GitHubRepository(int id,
                            @NonNull final String name,
                            int stargazersCount,
                            int forksCount,
                            @NonNull final GitHubOwner owner) {
        this.id = id;
        this.name = get(name);
        this.stargazersCount = stargazersCount;
        this.forksCount = forksCount;
        this.owner = get(owner);
    }

    public GitHubRepository(@NonNull final GitHubRepository other) {
        this(other.getId(),
             other.getName(),
             other.getStargazersCount(),
             other.getForksCount(),
             other.getOwner());
    }

    @NonNull
    public static GitHubRepository none() {
        return new GitHubRepository(-1, "", -1, -1, GitHubOwner.empty());
    }

    public boolean isSome() {
        return id != -1;
    }

    @NonNull
    @Override
    protected Class<GitHubRepository> getTypeParameterClass() {
        return GitHubRepository.class;
    }

    @Override
    protected boolean isEmpty(@NonNull final Field field, @NonNull final OverwritablePojo<GitHubRepository> pojo) {
        try {
            if (field.get(pojo) instanceof GitHubOwner) {
                return false;
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Failed get at " + field.getName(), e);
        }

        return super.isEmpty(field, pojo);
    }

    public int getId() {
        return id;
    }

    @NonNull
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
        return owner;
    }

    @Override
    public String toString() {
        return "GitHubRepository{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stargazersCount=" + stargazersCount +
                ", forksCount=" + forksCount +
                ", owner=" + owner +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GitHubRepository that = (GitHubRepository) o;

        if (id != that.id) return false;
        if (stargazersCount != that.stargazersCount) return false;
        if (forksCount != that.forksCount) return false;
        if (!name.equals(that.name)) return false;
        return owner.equals(that.owner);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + stargazersCount;
        result = 31 * result + forksCount;
        result = 31 * result + owner.hashCode();
        return result;
    }
}
