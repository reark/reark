package io.reark.rxgithubapp.advanced.data.stores.cores;

import android.content.ContentProvider;
import android.content.pm.ProviderInfo;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reark.rxgithubapp.advanced.data.schematicProvider.generated.GitHubProvider;
import io.reark.rxgithubapp.shared.pojo.GitHubOwner;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import rx.observers.TestSubscriber;

import static io.reark.rxgithubapp.advanced.data.schematicProvider.GitHubProvider.GitHubRepositories.GITHUB_REPOSITORIES;
import static java.util.Arrays.asList;

@RunWith(AndroidJUnit4.class)
public class GitHubRepositoryStoreCoreTest extends ProviderTestCase2<GitHubProvider> {

    private GitHubRepositoryStoreCore gitHubRepositoryStoreCore;

    private ContentProvider contentProvider;

    public GitHubRepositoryStoreCoreTest() {
        super(GitHubProvider.class, GitHubProvider.AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());

        final ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.authority = GitHubProvider.AUTHORITY;

        contentProvider = new GitHubProvider();
        contentProvider.attachInfo(InstrumentationRegistry.getTargetContext(), providerInfo);
        contentProvider.delete(GITHUB_REPOSITORIES, null, null);

        Thread.sleep(2000);

        final MockContentResolver contentResolver = new MockContentResolver();
        contentResolver.addProvider(GitHubProvider.AUTHORITY, contentProvider);

        gitHubRepositoryStoreCore = new GitHubRepositoryStoreCore(contentResolver, new Gson());

        super.setUp();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        contentProvider.delete(GITHUB_REPOSITORIES, null, null);

        super.tearDown();
    }

    // PUT

    @Test
    public void put_WithTwoDifferentIds_StoresTwoValues() throws InterruptedException {
        final GitHubRepository value1 = create(100, "test name 1");
        final GitHubRepository value2 = create(200, "test name 2");
        TestSubscriber<GitHubRepository> testSubscriber1 = new TestSubscriber<>();
        TestSubscriber<GitHubRepository> testSubscriber2 = new TestSubscriber<>();

        gitHubRepositoryStoreCore.put(100, value1);
        gitHubRepositoryStoreCore.put(200, value2);
        Thread.sleep(1500);
        gitHubRepositoryStoreCore.getCached(100).subscribe(testSubscriber1);
        gitHubRepositoryStoreCore.getCached(200).subscribe(testSubscriber2);

        testSubscriber1.awaitTerminalEvent();
        testSubscriber1.assertCompleted();
        testSubscriber1.assertNoErrors();
        testSubscriber1.assertValue(value1);

        testSubscriber2.awaitTerminalEvent();
        testSubscriber2.assertCompleted();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertValue(value2);
    }

    // GET STREAM

    @Test
    public void getStream_EmitsValuesForId_AndDoesNotComplete() {
        final GitHubRepository value1 = create(100, "test name 1");
        final GitHubRepository value2 = create(200, "test name 2");
        TestSubscriber<GitHubRepository> testSubscriber1 = new TestSubscriber<>();
        TestSubscriber<GitHubRepository> testSubscriber2 = new TestSubscriber<>();

        gitHubRepositoryStoreCore.getStream(100).subscribe(testSubscriber1);
        gitHubRepositoryStoreCore.getStream(200).subscribe(testSubscriber2);
        gitHubRepositoryStoreCore.put(100, value1);
        gitHubRepositoryStoreCore.put(200, value2);

        testSubscriber1.awaitTerminalEvent(1500, TimeUnit.MILLISECONDS);
        testSubscriber1.assertNotCompleted();
        testSubscriber1.assertNoErrors();
        testSubscriber1.assertValue(value1);

        testSubscriber2.awaitTerminalEvent(1500, TimeUnit.MILLISECONDS);
        testSubscriber2.assertNotCompleted();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertValue(value2);
    }

    @Test
    public void getStream_DoesNotEmitInitialValue() {
        final GitHubRepository value = create(100, "test name");
        TestSubscriber<GitHubRepository> testSubscriber = new TestSubscriber<>();

        gitHubRepositoryStoreCore.put(100, value);
        gitHubRepositoryStoreCore.getStream(100).subscribe(testSubscriber);
        gitHubRepositoryStoreCore.put(100, value);

        testSubscriber.awaitTerminalEvent(1500, TimeUnit.MILLISECONDS);
        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(value);
    }

    // GET ALL CACHED

    @Test
    public void getAllCached_ReturnsAllData_AndCompletes() throws InterruptedException {
        // ARRANGE
        final GitHubRepository value1 = create(100, "test name 1");
        final GitHubRepository value2 = create(200, "test name 2");
        final GitHubRepository value3 = create(300, "test name 3");
        TestSubscriber<List<GitHubRepository>> testSubscriber = new TestSubscriber<>();

        // ACT
        gitHubRepositoryStoreCore.put(100, value1);
        gitHubRepositoryStoreCore.put(200, value2);
        Thread.sleep(1500);
        gitHubRepositoryStoreCore.getAllCached().subscribe(testSubscriber);
        gitHubRepositoryStoreCore.put(300, value3);

        // ASSERT
        testSubscriber.awaitTerminalEvent(1500, TimeUnit.MILLISECONDS);
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(asList(value1, value2));
    }

    // GET ALL STREAM

    @Test
    public void getAllStream_ReturnsAllData_AndDoesNotComplete() throws InterruptedException {
        // ARRANGE
        final GitHubRepository value1 = create(100, "test name 1");
        final GitHubRepository value2 = create(200, "test name 2");
        final GitHubRepository value3 = create(300, "test name 3");
        TestSubscriber<GitHubRepository> testSubscriber = new TestSubscriber<>();

        // ACT
        gitHubRepositoryStoreCore.put(100, value1);
        Thread.sleep(1500);
        gitHubRepositoryStoreCore.getAllStream().subscribe(testSubscriber);
        gitHubRepositoryStoreCore.put(200, value2);
        gitHubRepositoryStoreCore.put(300, value3);

        // ASSERT
        testSubscriber.awaitTerminalEvent(1500, TimeUnit.MILLISECONDS);
        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(asList(value2, value3));
    }

    @NonNull
    private static GitHubRepository create(Integer id, String name) {
        return new GitHubRepository(id, name, 10, 10, new GitHubOwner("testAvatar"));
    }
}
