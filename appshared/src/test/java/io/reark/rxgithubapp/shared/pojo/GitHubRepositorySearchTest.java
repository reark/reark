package io.reark.rxgithubapp.shared.pojo;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GitHubRepositorySearchTest {

    private static final List<Integer> ITEMS = Arrays.asList(10, 20, 30);
    
    @Test
    public void testSameIdPojoEquals() {
        GitHubRepositorySearch search1 = new GitHubRepositorySearch("search", ITEMS);
        GitHubRepositorySearch search2 = new GitHubRepositorySearch("search", ITEMS);

        assertEquals(search1, search2);
    }

    @Test
    public void testDifferentIdPojoDoesNotEqual() {
        GitHubRepositorySearch search1 = new GitHubRepositorySearch("search", ITEMS);
        GitHubRepositorySearch search2 = new GitHubRepositorySearch("other", ITEMS);

        assertFalse(search1.equals(search2));
    }
}
