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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Created by ttuo on 13/12/15.
 */
public class FetcherManagerBaseTest {

    @Test
    public void testFindFetcher() {
        // Assign
        final Fetcher<String> fetcher = mock(Fetcher.class);
        when(fetcher.getServiceUri()).thenReturn("path/to/resource");
        final FetcherManagerBase<String> fetcherManager =
                new TestFetcherManager(Arrays.asList(fetcher));

        // Act
        Fetcher foundFetcher = fetcherManager.findFetcher("path/to/resource");

        // Assert
        assertEquals(foundFetcher, fetcher);
    }

    @Test
    public void testFindFetcherNull() {
        // Assign
        final FetcherManagerBase<String> fetcherManager = new TestFetcherManager(new ArrayList<>());

        // Act
        Fetcher<String> foundFetcher = fetcherManager.findFetcher("path/to/resource");

        // Assert
        assertNull(foundFetcher);
    }

    static class TestFetcherManager extends FetcherManagerBase<String> {
        public TestFetcherManager(Collection<Fetcher<String>> fetchers) {
            super(fetchers);
        }
    }
}
