package io.reark.reark.data.stores;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.test.ProviderTestCase2;

import io.reark.reark.data.store.SingleItemContentProviderStore;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;

public class SingleItemContentProviderStoreTest extends ProviderTestCase2<SimpleMockContentProvider> {

    private static final String AUTHORITY = SimpleMockContentProvider.AUTHORITY;
    private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    private static final Uri DATA_URI = Uri.withAppendedPath(AUTHORITY_URI, "testPath");
    private static final String[] PROJECTION = new String[] { DataColumns.KEY, DataColumns.VALUE };

    public SingleItemContentProviderStoreTest() {
        super(SimpleMockContentProvider.class, SimpleMockContentProvider.AUTHORITY);
    }

    public interface DataColumns {
        String KEY = "key";
        String VALUE = "value";
    }

    public void testSomething() {
        // ARRANGE
        MatrixCursor cursor = new MatrixCursor(PROJECTION);
        String[] exampleData = { "1001", "parsnip" };
        cursor.addRow(exampleData);
        getProvider().addQueryResult(DATA_URI, cursor);

        // ACT
        Cursor resultCursor = getProvider().query(DATA_URI, PROJECTION, null, null, null);

        // ASSERT
        assertEquals(1, resultCursor.getCount());
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
            return PROJECTION;
        }

        @NonNull
        @Override
        protected String read(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndex(DataColumns.KEY));
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
