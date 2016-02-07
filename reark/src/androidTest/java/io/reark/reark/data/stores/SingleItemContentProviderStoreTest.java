package io.reark.reark.data.stores;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.test.IsolatedContext;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import java.util.ArrayList;

import io.reark.reark.data.store.SingleItemContentProviderStore;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import rx.functions.Action1;
import rx.observers.TestSubscriber;

public class SingleItemContentProviderStoreTest extends ProviderTestCase2<SimpleMockContentProvider> {

    private static final String AUTHORITY = SimpleMockContentProvider.AUTHORITY;
    private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    private static final Uri DATA_URI = Uri.withAppendedPath(AUTHORITY_URI, "testPath");


    private MockContentResolver resolver;
    private Context context;
    private TestStore store;

    public interface DataColumns {
        String KEY = "key";
        String VALUE = "value";
    }

    public SingleItemContentProviderStoreTest() {
        super(SimpleMockContentProvider.class, SimpleMockContentProvider.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        resolver = getMockContentResolver();
        store = new TestStore(resolver);

        Action1<String> insert = value ->
                SingleItemContentProviderStoreTest.this.getProvider().insert(
                        store.getUriForId(store.getIdFor(value)),
                        store.getContentValuesForItem(value));

        // Prepare the mock content provider with values
        insert.call("parsnip");
        insert.call("lettuce");
    }

    @Override
    public MockContentResolver getMockContentResolver() {
        MockContentResolver resolver = new MockContentResolver();
        resolver.addProvider(AUTHORITY, getProvider());
        return resolver;
    }

    public void testGetOneWithData() {
        // ARRANGE
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();

        // ACT
        store.getOne(store.getIdFor("parsnip")).subscribe(testSubscriber);

        // ASSERT
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(new ArrayList<String>(){{ add("parsnip"); }});
    }

    public class TestStore extends SingleItemContentProviderStore<String, Integer> {

        public TestStore(@NonNull ContentResolver contentResolver) {
            super(contentResolver);
        }

        @NonNull
        @Override
        public Uri getUriForId(@NonNull Integer id) {
            return Uri.parse(getContentUri() + "/" + id);
        }

        @NonNull
        @Override
        protected Integer getIdFor(@NonNull String item) {
            return item.hashCode();
        }

        @NonNull
        @Override
        public Uri getContentUri() {
            return DATA_URI;
        }

        @NonNull
        @Override
        protected String[] getProjection() {
            return SimpleMockContentProvider.PROJECTION;
        }

        @NonNull
        @Override
        protected String read(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndex(DataColumns.VALUE));
        }

        @NonNull
        @Override
        protected ContentValues readRaw(Cursor cursor) {
            final String value = cursor.getString(cursor.getColumnIndex(DataColumns.VALUE));
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataColumns.VALUE, value);
            return contentValues;
        }

        @NonNull
        @Override
        protected ContentValues getContentValuesForItem(String item) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataColumns.KEY, getIdFor(item));
            contentValues.put(DataColumns.VALUE, item);
            return contentValues;
        }

        @Override
        protected boolean contentValuesEqual(ContentValues v1, ContentValues v2) {
            return v1.getAsString(DataColumns.KEY).equals(v2.getAsString(DataColumns.KEY));
        }
    }
}
