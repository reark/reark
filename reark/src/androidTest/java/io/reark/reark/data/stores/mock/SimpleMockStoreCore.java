package io.reark.reark.data.stores.mock;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

import io.reark.reark.data.stores.cores.ContentProviderStoreCore;
import rx.Observable;

/**
 * A simple store core implementing the methods content provider requires.
 */
public class SimpleMockStoreCore extends ContentProviderStoreCore<Integer, String> {

    public static final String AUTHORITY = "test.authority";

    private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    private static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "veggies");

    private static final String[] PROJECTION = { SimpleMockContentProvider.DataColumns.KEY, SimpleMockContentProvider.DataColumns.VALUE };

    public SimpleMockStoreCore(@NonNull final ContentResolver contentResolver) {
        super(contentResolver);
    }

    @NonNull
    @Override
    public Observable<List<String>> getCached() {
        return getAllOnce(getUriForId(0));
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
        return cursor.getString(cursor.getColumnIndex(SimpleMockContentProvider.DataColumns.VALUE));
    }

    @NonNull
    @Override
    public ContentValues getContentValuesForItem(@NonNull final String item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SimpleMockContentProvider.DataColumns.KEY, item.hashCode());
        contentValues.put(SimpleMockContentProvider.DataColumns.VALUE, item);
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