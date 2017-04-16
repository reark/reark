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
package io.reark.reark.data.stores;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.subscribers.TestSubscriber;
import io.reark.reark.data.stores.SimpleMockContentProvider.DataColumns;
import io.reark.reark.data.stores.cores.ContentProviderStoreCore;

@RunWith(AndroidJUnit4.class)
public class ContentProviderStoreTest extends ProviderTestCase2<SimpleMockContentProvider> {

    private static final String AUTHORITY = "test.authority";
    private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    private static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "veggies");
    private static final String[] PROJECTION = { DataColumns.KEY, DataColumns.VALUE };
    private static final String NONE = "";

    private TestStoreCore core;
    private TestStore store;

    public ContentProviderStoreTest() {
        super(SimpleMockContentProvider.class, AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        core = new TestStoreCore(getMockContentResolver());
        store = new TestStore(core);

        Consumer<String> insert = value ->
                getProvider().insert(
                        core.getUriForId(TestStore.getIdFor(value)),
                        core.getContentValuesForItem(value)
                );

        // Prepare the mock content provider with values
        insert.accept("parsnip");
        insert.accept("lettuce");
        insert.accept("spinach");
    }

    @Test
    public void getOnce_WithData_ReturnsData_AndCompletes() {
        // ARRANGE
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        String expected = "parsnip";

        // ACT
        store.getOnce(TestStore.getIdFor("parsnip")).subscribe(testSubscriber);

        // ASSERT
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertResult(expected);
    }

    @Test
    public void getOnce_WithNoData_ReturnsNoneValue_AndCompletes() {
        // ARRANGE
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        // ACT
        store.getOnce(TestStore.getIdFor("bacon")).subscribe(testSubscriber);

        // ASSERT
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertResult(NONE);
    }

    @Test
    public void getOnceAndStream_WithData_ReturnsData_AndDoesNotComplete() {
        // ARRANGE
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        String expected = "spinach";

        // ACT
        store.getOnceAndStream(TestStore.getIdFor("spinach")).subscribe(testSubscriber);

        // ASSERT
        testSubscriber.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);
        testSubscriber.assertNotComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertResult(expected);
    }

    @Test
    public void getOnceAndStream_WithNoData_ReturnsNoneValue_AndDoesNotComplete() {
        // ARRANGE
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();

        // ACT
        store.getOnceAndStream(TestStore.getIdFor("bacon")).subscribe(testSubscriber);

        // ASSERT
        testSubscriber.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);
        testSubscriber.assertNotComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertResult(NONE);
    }

    // The following tests are not part of the public API, but rather test what
    // the content provider core exposes for extending classes.

    @Test
    public void core_GetAllOnce_WithData_ReturnsData_AndCompletes() {
        // ARRANGE
        TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
        List<String> expected = Arrays.asList("parsnip");

        // ACT
        core.getAllCached(TestStore.getIdFor("parsnip")).subscribe(testSubscriber);

        // ASSERT
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertResult(expected);
    }

    @Test
    public void core_GetAllOnce_WithWildcardQuery_WithData_ReturnsAllData_AndCompletes() {
        // ARRANGE
        TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
        List<String> expected = (Arrays.asList("parsnip", "lettuce", "spinach"));

        // ACT
        // Wildcard depends on content provider. For tests we just use 0 while on SQL backend
        // this would be an asterisk. The exact wildcard is not important for the test as we just
        // want to make sure the provider stores can return a larger listing of results.
        core.getAllCached(0).subscribe(testSubscriber);

        // ASSERT
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertResult(expected);
    }

    /**
     * A simple store containing String values tracked with Integer keys.
     */
    public static class TestStore extends ContentProviderStore<Integer, String, String> {

        public TestStore(@NonNull final TestStoreCore core) {
            super(core,
                    TestStore::getIdFor,
                    value -> value != null ? value : NONE,
                    () -> NONE);
        }

        @NonNull
        private static Integer getIdFor(@NonNull final String item) {
            return item.hashCode();
        }
    }

    /**
     * A simple store core implementing the methods content provider requires.
     */
    public static class TestStoreCore extends ContentProviderStoreCore<Integer, String> {

        protected TestStoreCore(@NonNull final ContentResolver contentResolver) {
            super(contentResolver);
        }

        @NonNull
        public Flowable<List<String>> getAllCached(@NonNull Integer id) {
            return getAllOnce(getUriForId(id));
        }

        @NonNull
        @Override
        protected String getAuthority() {
            return AUTHORITY;
        }

        @NonNull
        @Override
        public Uri getContentUri() {
            return CONTENT_URI;
        }

        @NonNull
        @Override
        protected String[] getProjection() {
            return PROJECTION;
        }

        @NonNull
        @Override
        protected String read(@NonNull final Cursor cursor) {
            return cursor.getString(cursor.getColumnIndex(DataColumns.VALUE));
        }

        @NonNull
        @Override
        protected ContentValues getContentValuesForItem(@NonNull final String item) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataColumns.KEY, item.hashCode());
            contentValues.put(DataColumns.VALUE, item);
            return contentValues;
        }

        @NonNull
        @Override
        public Uri getUriForId(@NonNull final Integer id) {
            return Uri.withAppendedPath(getContentUri(), String.valueOf(id));
        }

        @NonNull
        @Override
        protected Integer getIdForUri(@NonNull final Uri uri) {
            return Integer.valueOf(uri.getLastPathSegment());
        }
    }
}
