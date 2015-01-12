package com.tehmou.rxbookapp.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.tehmou.rxbookapp.data.provider.GitHubRepositoryContract;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

/**
 * Created by ttuo on 07/01/15.
 */
public class GitHubRepositoryStore extends ContentProviderStoreBase<GitHubRepository, Integer> {
    private static final String TAG = GitHubRepositoryStore.class.getSimpleName();

    public GitHubRepositoryStore(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    protected void insertOrUpdate(GitHubRepository item) {
        Uri uri = Uri.withAppendedPath(GitHubRepositoryContract.CONTENT_URI, "" + item.getId());
        ContentValues values = new ContentValues();
        values.put("id", item.getId());
        values.put("name", item.getName());
        if (contentResolver.update(uri, values, null, null) == 0) {
            final Uri resultUri = contentResolver.insert(uri, values);
            Log.v(TAG, "Inserted at " + resultUri);
        } else {
            Log.v(TAG, "Updated at " + uri);
        }
    }

    @Override
    protected GitHubRepository query(Integer id) {
        Uri uri = Uri.withAppendedPath(GitHubRepositoryContract.CONTENT_URI, "" + id);
        Cursor cursor = contentResolver.query(uri, new String[]{"id", "name"}, null, null, null);
        GitHubRepository gitHubRepository = null;
        if (cursor.moveToFirst()) {
            gitHubRepository = readFromCursor(cursor);
        } else {
            Log.e(TAG, "Could not find with id: " + id);
        }
        cursor.close();
        return gitHubRepository;
    }

    private GitHubRepository readFromCursor(Cursor cursor) {
        final int id = cursor.getInt(cursor.getColumnIndex("id"));
        final String name = cursor.getString(cursor.getColumnIndex("name"));
        return new GitHubRepository(id, name);
    }
}
