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

import io.reactivex.observers.TestObserver;
import io.reark.rxgithubapp.advanced.data.schematicProvider.generated.GitHubProvider;
import io.reark.rxgithubapp.shared.pojo.GitHubOwner;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;

import static io.reark.rxgithubapp.advanced.data.schematicProvider.GitHubProvider.GitHubRepositories.GITHUB_REPOSITORIES;
import static io.reark.rxgithubapp.shared.Constants.Tests.PROVIDER_WAIT_TIME;
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

        Thread.sleep(PROVIDER_WAIT_TIME);

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
        TestObserver<GitHubRepository> testObserver1 = new TestObserver<>();
        TestObserver<GitHubRepository> testObserver2 = new TestObserver<>();

        gitHubRepositoryStoreCore.put(100, value1);
        gitHubRepositoryStoreCore.put(200, value2);
        Thread.sleep(PROVIDER_WAIT_TIME);
        gitHubRepositoryStoreCore.getCached(100).subscribe(testObserver1);
        gitHubRepositoryStoreCore.getCached(200).subscribe(testObserver2);

        testObserver1.awaitDone(PROVIDER_WAIT_TIME, TimeUnit.MILLISECONDS)
                .assertComplete()
                .assertNoErrors()
                .assertValue(value1);

        testObserver2.awaitDone(PROVIDER_WAIT_TIME, TimeUnit.MILLISECONDS)
                .assertComplete()
                .assertNoErrors()
                .assertValue(value2);
    }

    // GET CACHED

    @Test
    public void getCached_WithId_EmitsInitialValues_AndCompletes() throws InterruptedException {
        final GitHubRepository value1 = create(100, "test name 1");
        final GitHubRepository value2 = create(100, "test name 2");
        TestObserver<GitHubRepository> testObserver = new TestObserver<>();

        gitHubRepositoryStoreCore.put(100, value1);
        Thread.sleep(PROVIDER_WAIT_TIME);
        gitHubRepositoryStoreCore.getCached(100).subscribe(testObserver);
        gitHubRepositoryStoreCore.put(100, value2);

        testObserver.awaitDone(PROVIDER_WAIT_TIME, TimeUnit.MILLISECONDS)
                .assertComplete()
                .assertNoErrors()
                .assertValue(value1);
    }

    // GET STREAM

    @Test
    public void getStream_WithId_EmitsValuesForId_AndDoesNotComplete() {
        final GitHubRepository value1 = create(100, "test name 1");
        final GitHubRepository value2 = create(200, "test name 2");
        TestObserver<GitHubRepository> testObserver1 = new TestObserver<>();
        TestObserver<GitHubRepository> testObserver2 = new TestObserver<>();

        gitHubRepositoryStoreCore.getStream(100).subscribe(testObserver1);
        gitHubRepositoryStoreCore.getStream(200).subscribe(testObserver2);
        gitHubRepositoryStoreCore.put(100, value1);
        gitHubRepositoryStoreCore.put(200, value2);

        testObserver1.awaitDone(PROVIDER_WAIT_TIME, TimeUnit.MILLISECONDS)
                .assertNotComplete()
                .assertNoErrors()
                .assertValue(value1);

        testObserver2.awaitDone(PROVIDER_WAIT_TIME, TimeUnit.MILLISECONDS)
                .assertNotComplete()
                .assertNoErrors()
                .assertValue(value2);
    }

    @Test
    public void getStream_DoesNotEmitInitialValue() throws InterruptedException {
        final GitHubRepository value1 = create(100, "test name 1");
        final GitHubRepository value2 = create(100, "test name 2");
        TestObserver<GitHubRepository> testObserver = new TestObserver<>();

        gitHubRepositoryStoreCore.put(100, value1);
        Thread.sleep(PROVIDER_WAIT_TIME);
        gitHubRepositoryStoreCore.getStream(100).subscribe(testObserver);
        gitHubRepositoryStoreCore.put(100, value2);

        testObserver.awaitDone(PROVIDER_WAIT_TIME, TimeUnit.MILLISECONDS)
                .assertNotComplete()
                .assertNoErrors()
                .assertValue(value2);
    }

    // GET ALL CACHED

    @Test
    public void getCached_WithNoId_ReturnsAllData_AndCompletes() throws InterruptedException {
        // ARRANGE
        final GitHubRepository value1 = create(100, "test name 1");
        final GitHubRepository value2 = create(200, "test name 2");
        final GitHubRepository value3 = create(300, "test name 3");
        TestObserver<List<GitHubRepository>> testObserver = new TestObserver<>();

        // ACT
        gitHubRepositoryStoreCore.put(100, value1);
        gitHubRepositoryStoreCore.put(200, value2);
        Thread.sleep(PROVIDER_WAIT_TIME);
        gitHubRepositoryStoreCore.getCached().subscribe(testObserver);
        gitHubRepositoryStoreCore.put(300, value3);

        // ASSERT
        testObserver.awaitDone(PROVIDER_WAIT_TIME, TimeUnit.MILLISECONDS)
                .assertComplete()
                .assertNoErrors()
                .assertValue(asList(value1, value2));
    }

    // GET ALL STREAM

    @Test
    public void getStream_WithNoId_ReturnsAllData_AndDoesNotComplete() throws InterruptedException {
        // ARRANGE
        final GitHubRepository value1 = create(100, "test name 1");
        final GitHubRepository value2 = create(200, "test name 2");
        final GitHubRepository value3 = create(300, "test name 3");
        TestObserver<GitHubRepository> testObserver = new TestObserver<>();

        // ACT
        gitHubRepositoryStoreCore.put(100, value1);
        Thread.sleep(PROVIDER_WAIT_TIME);
        gitHubRepositoryStoreCore.getStream().subscribe(testObserver);
        gitHubRepositoryStoreCore.put(200, value2);
        Thread.sleep(PROVIDER_WAIT_TIME);
        gitHubRepositoryStoreCore.put(300, value3);

        // ASSERT
        testObserver.awaitDone(PROVIDER_WAIT_TIME, TimeUnit.MILLISECONDS)
                .assertNotComplete()
                .assertNoErrors()
                .assertValues(value2, value3);
    }

    @NonNull
    private static GitHubRepository create(Integer id, String name) {
        return new GitHubRepository(id, name, 10, 10, new GitHubOwner("testAvatar"));
    }
}
