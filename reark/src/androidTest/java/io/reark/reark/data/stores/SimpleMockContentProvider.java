package io.reark.reark.data.stores;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.mock.MockContentProvider;

import java.util.LinkedHashMap;

/**
 * Mock content provider to act as the underlying provider for stores in unit tests.
 */
public class SimpleMockContentProvider extends MockContentProvider {

    // LinkedHashMap keeps the insertion order for more straightforward testing
    private LinkedHashMap<Uri, String> values = new LinkedHashMap<>();

    public interface DataColumns {
        String KEY = "key";
        String VALUE = "value";
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        values.put(uri, contentValues.getAsString(DataColumns.VALUE));
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return getCursor(uri, projection);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder, CancellationSignal cancellationSignal) {
        return getCursor(uri, projection);
    }

    // Crude imitation of the behavior of SQL content provider
    private Cursor getCursor(Uri uri, String[] projection) {
        MatrixCursor cursor = new MatrixCursor(projection);

        if (values.containsKey(uri)) {
            String[] result = { uri.getLastPathSegment(), values.get(uri) };
            cursor.addRow(result);
        } else if (uri.getLastPathSegment().equals("0")) {
            for (Uri key : values.keySet()) {
                String[] result = { key.getLastPathSegment(), values.get(key) };
                cursor.addRow(result);
            }
        }

        return cursor;
    }
}
