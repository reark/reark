package io.reark.rxgithubapp.data.stores;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import io.reark.reark.data.base.store.SingleItemContentProviderStoreBase;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.data.DataLayer;
import io.reark.rxgithubapp.data.schematicProvider.GitHubProvider;
import io.reark.rxgithubapp.data.schematicProvider.JsonIdColumns;
import io.reark.rxgithubapp.data.schematicProvider.UserSettingsColumns;
import io.reark.rxgithubapp.pojo.UserSettings;

/**
 * Created by ttuo on 07/01/15.
 */
public class UserSettingsStore extends SingleItemContentProviderStoreBase<UserSettings, Integer> {
    private static final String TAG = UserSettingsStore.class.getSimpleName();

    private static final int DEFAULT_REPOSITORY_ID = 15491874;

    public UserSettingsStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
        if (!hasUserSettings()) {
            put(new UserSettings(DEFAULT_REPOSITORY_ID));
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
