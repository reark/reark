package com.tehmou.rxbookapp.data;

import android.content.ContentResolver;
import android.net.Uri;

import com.google.gson.reflect.TypeToken;
import com.tehmou.rxbookapp.data.provider.UserSettingsContract;
import com.tehmou.rxbookapp.pojo.UserSettings;

/**
 * Created by ttuo on 07/01/15.
 */
public class UserSettingsStore extends ContentProviderJsonStoreBase<UserSettings, Integer> {
    private static final String TAG = UserSettingsStore.class.getSimpleName();

    private static final int DEFAULT_REPOSITORY_ID = 15491874;

    public UserSettingsStore(ContentResolver contentResolver) {
        super(contentResolver,
                new UserSettingsContract(),
                new TypeToken<UserSettings>() {}.getType());
        if (!hasUserSettings()) {
            insertOrUpdate(new UserSettings(DEFAULT_REPOSITORY_ID));
        }
    }

    @Override
    protected Integer getIdFor(UserSettings item) {
        return UserSettingsContract.DEFAULT_USER_ID;
    }

    @Override
    public Uri getContentUri() {
        return UserSettingsContract.CONTENT_URI;
    }

    private boolean hasUserSettings() {
        return query(UserSettingsContract.DEFAULT_USER_ID) != null;
    }
}
