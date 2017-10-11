package io.reark.rxgithubapp.shared.pojo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GitHubRepositoryTest {

    private static final GitHubOwner OWNER = new GitHubOwner("owner");

    @Test
    public void testSameIdPojoEquals() {
        GitHubRepository repo1 = new GitHubRepository(100, "", 10, 10, OWNER);
        GitHubRepository repo2 = new GitHubRepository(100, "", 10, 10, OWNER);

        assertEquals(repo1, repo2);
    }

    @Test
    public void testDifferentIdPojoDoesNotEqual() {
        GitHubRepository repo1 = new GitHubRepository(100, "", 10, 10, OWNER);
        GitHubRepository repo2 = new GitHubRepository(200, "", 10, 10, OWNER);

        assertFalse(repo1.equals(repo2));
    }

    @Test
    public void testOverwrite_WithItself() {
        GitHubRepository repo = new GitHubRepository(100, "foo", 10, 10, OWNER);

        repo.overwrite(repo);

        assertEquals(100, repo.getId());
        assertEquals("foo", repo.getName());
    }

    @Test
    public void testOverwrite_WithSameId_WithDifferentName() {
        GitHubRepository repo1 = new GitHubRepository(100, "foo", 10, 10, OWNER);
        GitHubRepository repo2 = new GitHubRepository(100, "bar", 10, 10, OWNER);

        repo1.overwrite(repo2);

        assertEquals(repo1, repo2);
    }

    @Test
    public void testOverwrite_WithSameId_WithDifferentCount() {
        GitHubRepository repo1 = new GitHubRepository(100, "foo", 10, 10, OWNER);
        GitHubRepository repo2 = new GitHubRepository(100, "foo", 20, 20, OWNER);

        repo1.overwrite(repo2);

        assertEquals(repo1, repo2);
    }

    @Test
    public void testOverwrite_WithSameId_WithDifferentOwner() {
        GitHubRepository repo1 = new GitHubRepository(100, "foo", 10, 10, OWNER);
        GitHubRepository repo2 = new GitHubRepository(100, "foo", 10, 10, new GitHubOwner("thief"));

        repo1.overwrite(repo2);

        assertEquals(repo1, repo2);
    }

    @Test
    public void testOverwrite_WithAnother() {
        GitHubRepository repo1 = new GitHubRepository(100, "foo", 10, 10, OWNER);
        GitHubRepository repo2 = new GitHubRepository(200, "bar", 10, 10, OWNER);

        repo1.overwrite(repo2);

        assertEquals(repo1, repo2);
        assertEquals(200, repo1.getId());
        assertEquals("bar", repo1.getName());
    }
}
