package io.reark.reark.data.stores;

import android.database.Cursor;
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

    private HashMap<Uri, Cursor> results = new HashMap<>();

    public void addQueryResult(Uri uri, Cursor result) {
        results.put(uri, result);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return results.get(uri);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        return results.get(uri);
    }
}
