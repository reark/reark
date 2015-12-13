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
