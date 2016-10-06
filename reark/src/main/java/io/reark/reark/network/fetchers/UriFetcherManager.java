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
package io.reark.reark.network.fetchers;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

import static io.reark.reark.utils.Preconditions.checkNotNull;

public final class UriFetcherManager extends FetcherManagerBase<Uri> {

    private UriFetcherManager(@NonNull final Collection<Fetcher<Uri>> fetchers) {
        super(fetchers);
    }

    public static class Builder {
        private Collection<Fetcher<Uri>> fetchers = new ArrayList<>();

        @NonNull
        public Builder fetchers(@NonNull final Collection<Fetcher<Uri>> fetchers) {
            checkNotNull(fetchers);

            this.fetchers = new ArrayList<>(fetchers);
            return this;
        }

        @NonNull
        public UriFetcherManager build() {
            return new UriFetcherManager(fetchers);
        }
    }
}
