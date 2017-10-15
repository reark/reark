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

import static io.reark.reark.utils.Preconditions.get;

public class GitHubOwner {

    @SerializedName("avatar_url")
    @NonNull
    private final String avatarUrl;

    public GitHubOwner(@NonNull final String avatarUrl) {
        this.avatarUrl = get(avatarUrl);
    }

    @NonNull
    public static GitHubOwner empty() {
        return new GitHubOwner("");
    }

    public boolean isSome() {
        return !avatarUrl.isEmpty();
    }

    @NonNull
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @Override
    public String toString() {
        return "GitHubOwner{" +
                "avatarUrl='" + avatarUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GitHubOwner that = (GitHubOwner) o;

        return avatarUrl.equals(that.avatarUrl);
    }

    @Override
    public int hashCode() {
        return avatarUrl.hashCode();
    }
}
