package com.tehmou.rxbookapp.data.stores;

import android.content.ContentResolver;
import android.net.Uri;

import com.tehmou.rxbookapp.data.base.store.ContentProviderStoreBase;
import com.tehmou.rxbookapp.data.provider.UserSettingsContract;
import com.tehmou.rxbookapp.pojo.UserSettings;

/**
 * Created by ttuo on 07/01/15.
 */
public class UserSettingsStore extends ContentProviderStoreBase<UserSettings, Integer> {
    private static final String TAG = UserSettingsStore.class.getSimpleName();

    private static final int DEFAULT_REPOSITORY_ID = 15491874;

    public UserSettingsStore(ContentResolver contentResolver) {
        super(contentResolver, new UserSettingsContract());
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
