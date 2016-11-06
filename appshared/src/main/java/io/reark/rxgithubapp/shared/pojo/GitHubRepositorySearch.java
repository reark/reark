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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.reark.reark.utils.Preconditions.get;

public class GitHubRepositorySearch {

    @NonNull
    private final String search;

    @NonNull
    private final List<Integer> items;

    public GitHubRepositorySearch(@NonNull final String search, @NonNull final List<Integer> items) {
        this.search = get(search);
        this.items = new ArrayList<>(items);
    }

    @NonNull
    public static GitHubRepositorySearch none() {
        return new GitHubRepositorySearch("", Collections.emptyList());
    }

    public boolean isEmpty() {
        return search.isEmpty();
    }

    @NonNull
    public String getSearch() {
        return search;
    }

    @NonNull
    public List<Integer> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GitHubRepositorySearch that = (GitHubRepositorySearch) o;

        if (!search.equals(that.search)) return false;
        return items.equals(that.items);

    }

    @Override
    public int hashCode() {
        int result = search.hashCode();
        result = 31 * result + items.hashCode();
        return result;
    }
}
