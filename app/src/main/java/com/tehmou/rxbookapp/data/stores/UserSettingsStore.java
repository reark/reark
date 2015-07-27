package com.tehmou.rxbookapp.data.stores;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.tehmou.rxbookapp.data.DataLayer;
import com.tehmou.rxbookapp.data.base.store.ContentProviderStoreBase;
import com.tehmou.rxbookapp.data.schematicProvider.GitHubProvider;
import com.tehmou.rxbookapp.data.schematicProvider.JsonIdColumns;
import com.tehmou.rxbookapp.data.schematicProvider.UserSettingsColumns;
import com.tehmou.rxbookapp.pojo.UserSettings;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 07/01/15.
 */
public class UserSettingsStore extends ContentProviderStoreBase<UserSettings, Integer> {
    private static final String TAG = UserSettingsStore.class.getSimpleName();

    private static final int DEFAULT_REPOSITORY_ID = 15491874;

    public UserSettingsStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
        if (!hasUserSettings()) {
            insertOrUpdate(new UserSettings(DEFAULT_REPOSITORY_ID));
        }
    }

    @NonNull
    @Override
    protected Integer getIdFor(@NonNull UserSettings item) {
        return DataLayer.DEFAULT_USER_ID;
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return GitHubProvider.UserSettings.USER_SETTINGS;
    }

    private boolean hasUserSettings() {
        return query(DataLayer.DEFAULT_USER_ID) != null;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { UserSettingsColumns.ID, UserSettingsColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(UserSettings item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(JsonIdColumns.ID, DataLayer.DEFAULT_USER_ID);
        contentValues.put(JsonIdColumns.JSON, new Gson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected UserSettings read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON));
        final UserSettings value = new Gson().fromJson(json, UserSettings.class);
        return value;
    }


    @NonNull
    @Override
    public Uri getUriForKey(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return GitHubProvider.UserSettings.withId(id);
    }
}
