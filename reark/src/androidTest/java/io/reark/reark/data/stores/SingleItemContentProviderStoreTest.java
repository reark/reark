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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.test.ProviderTestCase2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reark.reark.data.stores.SimpleMockContentProvider.DataColumns;
import rx.functions.Action1;
import rx.observers.TestSubscriber;

public class SingleItemContentProviderStoreTest extends ProviderTestCase2<SimpleMockContentProvider> {

    private static final String AUTHORITY = "test.authority";
    private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    private static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "veggies");
    private static final String[] PROJECTION = new String[] { DataColumns.KEY, DataColumns.VALUE };

    private TestStore store;

    public SingleItemContentProviderStoreTest() {
        super(SimpleMockContentProvider.class, AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        store = new TestStore(getMockContentResolver());

        Action1<String> insert = value ->
                getProvider().insert(
                        store.getUriForId(store.getIdFor(value)),
                        store.getContentValuesForItem(value)
                );

        // Prepare the mock content provider with values
        insert.call("parsnip");
        insert.call("lettuce");
        insert.call("spinach");
    }

    public void testGetOneWithData() {
        // ARRANGE
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        List<String> expected = new ArrayList<String>(){{ add("parsnip"); }};

        // ACT
        store.getOne(store.getIdFor("parsnip")).subscribe(testSubscriber);

        // ASSERT
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(expected);
    }

    public void testGetOneWithoutData() {
        // ARRANGE
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        List<String> expected = new ArrayList<String>(){{ add(null); }};

        // ACT
        store.getOne(store.getIdFor("bacon")).subscribe(testSubscriber);

        // ASSERT
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(expected);
    }

    public void testGetWithData() {
        // ARRANGE
        TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
        List<List<String>> expected = new ArrayList<List<String>>(){{
            add(new ArrayList<String>() {{ add("parsnip"); }});
        }};

        // ACT
        store.get(store.getIdFor("parsnip")).subscribe(testSubscriber);

        // ASSERT
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(expected);
    }

    public void testGetAll() {
        // ARRANGE
        TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
        List<List<String>> expected = new ArrayList<List<String>>(){{
            add(new ArrayList<String>() {{
                add("parsnip");
                add("lettuce");
                add("spinach");
            }});
        }};

        // ACT
        // Wildcard depends on content provider. For tests we just use 0 while on SQL backend
        // this would be an asterisk. The exact wildcard is not important for the test as we just
        // want to make sure the provider stores can return a larger listing of results.
        store.get(0).subscribe(testSubscriber);

        // ASSERT
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(expected);
    }

    public void testGetStream() {
        // ARRANGE
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        List<String> expected = new ArrayList<String>(){{ add("spinach"); }};

        // ACT
        store.getStream(store.getIdFor("spinach")).subscribe(testSubscriber);

        // ASSERT
        testSubscriber.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);
        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(expected);
    }

    public void testGetEmptyStream() {
        // ARRANGE
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();

        // ACT
        store.getStream(store.getIdFor("bacon")).subscribe(testSubscriber);

        // ASSERT
        testSubscriber.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);
        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertNoValues();
    }

    /**
     * A simple store containing String values tracked with Integer keys.
     */
    public class TestStore extends SingleItemContentProviderStore<String, Integer> {

        public TestStore(@NonNull ContentResolver contentResolver) {
            super(contentResolver);
        }

        @NonNull
        @Override
        public Uri getUriForId(@NonNull Integer id) {
            return Uri.withAppendedPath(getContentUri(), String.valueOf(id));
        }

        @NonNull
        @Override
        protected Integer getIdFor(@NonNull String item) {
            return item.hashCode();
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
        protected String read(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndex(DataColumns.VALUE));
        }

        @NonNull
        @Override
        protected ContentValues getContentValuesForItem(String item) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataColumns.KEY, getIdFor(item));
            contentValues.put(DataColumns.VALUE, item);
            return contentValues;
        }
    }
}
