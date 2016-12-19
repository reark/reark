package io.reark.rxgithubapp.advanced.data.stores;

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

import java.util.concurrent.TimeUnit;

import io.reark.rxgithubapp.advanced.data.schematicProvider.generated.GitHubProvider;
import io.reark.rxgithubapp.shared.pojo.GitHubOwner;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import rx.Observable;
import rx.observers.TestSubscriber;

import static io.reark.rxgithubapp.advanced.data.schematicProvider.GitHubProvider.GitHubRepositories.GITHUB_REPOSITORIES;
import static io.reark.rxgithubapp.shared.pojo.GitHubRepository.none;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@RunWith(AndroidJUnit4.class)
public class GitHubRepositoryStoreTest extends ProviderTestCase2<GitHubProvider> {

    private GitHubRepositoryStore gitHubRepositoryStore;

    private TestSubscriber<GitHubRepository> testSubscriber;

    private GitHubProvider contentProvider;

    private Gson gson = new Gson();
    
    public GitHubRepositoryStoreTest() {
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

        Thread.sleep(2500);

        final MockContentResolver contentResolver = new MockContentResolver();
        contentResolver.addProvider(GitHubProvider.AUTHORITY, contentProvider);

        gitHubRepositoryStore = new GitHubRepositoryStore(contentResolver, gson);
        testSubscriber = new TestSubscriber<>();
        
        super.setUp();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        contentProvider.delete(GITHUB_REPOSITORIES, null, null);

        super.tearDown();
    }

    @Test
    public void getOne_WithData_ReturnsData_AndCompletes() throws InterruptedException {
        final GitHubRepository value = create(100, "repository1");
        gitHubRepositoryStore.put(value); // TODO synchronous init with contentProvider
        Thread.sleep(1500);

        // getOnce is expected to return a observable that emits the value and then completes.
        gitHubRepositoryStore.getOnce(100).subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(1500, TimeUnit.MILLISECONDS);
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(singletonList(value));
    }

    @Test
    public void getOne_WithNoData_ReturnsNoneValue_AndCompletes() {
        // getOnce is expected to emit empty value in case no actual value exists.
        gitHubRepositoryStore.getOnce(100).subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(1500, TimeUnit.MILLISECONDS);
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(none());
    }

    @Test
    public void getOnceAndStream_ReturnsOnlyValuesForSubscribedId_AndDoesNotComplete() throws InterruptedException {
        final GitHubRepository value1 = create(100, "repository1");
        final GitHubRepository value2 = create(200, "repository2");

        gitHubRepositoryStore.getOnceAndStream(100).subscribe(testSubscriber);
        Thread.sleep(1500);
        gitHubRepositoryStore.put(value1);
        gitHubRepositoryStore.put(value2);

        testSubscriber.awaitTerminalEvent(1500, TimeUnit.MILLISECONDS);
        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(asList(none(), value1));
    }

    @Test
    public void getOnceAndStream_ReturnsAllValuesForSubscribedId_AndDoesNotComplete() throws InterruptedException {
        final GitHubRepository value1 = create(100, "repository-1");
        final GitHubRepository value2 = create(100, "repository-2");
        final GitHubRepository value3 = create(100, "repository-3");

        gitHubRepositoryStore.getOnceAndStream(100).subscribe(testSubscriber);
        Thread.sleep(1500);
        gitHubRepositoryStore.put(value1);
        Thread.sleep(1500);
        gitHubRepositoryStore.put(value2);
        Thread.sleep(1500);
        gitHubRepositoryStore.put(value3);

        testSubscriber.awaitTerminalEvent(1500, TimeUnit.MILLISECONDS);
        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(asList(none(), value1, value2, value3));
    }

    @Test
    public void getOnceAndStream_ReturnsOnlyNewValues_AndDoesNotComplete() throws InterruptedException {
        final GitHubRepository value = create(100, "repository1");

        // In the default store implementation identical values are filtered out.
        gitHubRepositoryStore.getOnceAndStream(100).subscribe(testSubscriber);
        Thread.sleep(1500);
        gitHubRepositoryStore.put(value);
        gitHubRepositoryStore.put(value);

        testSubscriber.awaitTerminalEvent(1500, TimeUnit.MILLISECONDS);
        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(asList(none(), value));
    }

    @Test
    public void getOnceAndStream_WithInitialValue_ReturnsInitialValues_AndDoesNotComplete() throws InterruptedException {
        final GitHubRepository value = create(100, "repository1");
        gitHubRepositoryStore.put(value);
        Thread.sleep(1500);

        gitHubRepositoryStore.getOnceAndStream(100).subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(1500, TimeUnit.MILLISECONDS);
        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(singletonList(value));
    }

    @Test
    public void getOnceAndStream_WithInitialValue_WithDelayedSubscription_ReturnsFirstValue_AndDoesNotComplete() throws InterruptedException {
        final GitHubRepository value1 = create(100, "repository1");
        final GitHubRepository value2 = create(100, "repository1");

        // This behavior is a little surprising, but it is because we cannot guarantee that the
        // observable that is produced as the stream will keep its first (cached) value up to date.
        // The only ways to around this would be custom subscribe function or converting the
        // source observable into a behavior, but these would significantly increase the
        // complexity and are hard to implement in other kinds of store (such as content providers).

        // Put initial value.
        gitHubRepositoryStore.put(value1);
        Thread.sleep(1500);

        // Create the stream observable but do not subscribe immediately.
        Observable<GitHubRepository> stream = gitHubRepositoryStore.getOnceAndStream(100);

        // Put new value into the store.
        gitHubRepositoryStore.put(value2);

        // Subscribe to stream that was created potentially a long time ago.
        stream.subscribe(testSubscriber);

        // Observe that the stream actually gives as the first item the cached value at the time of
        // creating the stream observable.
        testSubscriber.awaitTerminalEvent(1500, TimeUnit.MILLISECONDS);
        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(singletonList(value1));
    }

    @NonNull
    private static GitHubRepository create(int id, String name) {
        return new GitHubRepository(id, name, 10, 10, new GitHubOwner("owner"));
    }
}
