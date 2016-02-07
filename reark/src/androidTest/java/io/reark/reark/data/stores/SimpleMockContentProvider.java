package io.reark.reark.data.stores;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.test.mock.MockContentProvider;

import java.util.HashMap;

/**
 * Mock content provider to act as the underlying provider for stores in unit tests.
 */
public class SimpleMockContentProvider extends MockContentProvider {
    public static final String AUTHORITY = "test.authority";
    public static final String[] PROJECTION = new String[] { DataColumns.KEY, DataColumns.VALUE };

    private HashMap<Uri, String> values = new HashMap<>();

    public interface DataColumns {
        String KEY = "key";
        String VALUE = "value";
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        this.values.put(uri, values.getAsString(DataColumns.VALUE));
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        this.values.put(uri, values.getAsString(DataColumns.VALUE));
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return getCursor(uri, projection);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        return getCursor(uri, projection);
    }

    private Cursor getCursor(Uri uri, String[] projection) {
        MatrixCursor cursor = new MatrixCursor(projection);
        String[] result = { uri.getLastPathSegment(), values.get(uri) };
        cursor.addRow(result);
        return cursor;
    }
}
