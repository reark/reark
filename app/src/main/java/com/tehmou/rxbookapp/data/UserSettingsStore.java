package com.tehmou.rxbookapp.data;

import android.content.ContentResolver;
import android.net.Uri;

import com.google.gson.reflect.TypeToken;
import com.tehmou.rxbookapp.data.provider.GitHubRepositoryContract;
import com.tehmou.rxbookapp.data.provider.UserSettingsContract;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.pojo.UserSettings;

/**
 * Created by ttuo on 07/01/15.
 */
public class UserSettingsStore extends ContentProviderStoreBase<UserSettings, Integer> {
    private static final String TAG = UserSettingsStore.class.getSimpleName();

    public static final int DEFAULT_USER_ID = 0;
    private static final int DEFAULT_REPOSITORY_ID = 15491874;

    public UserSettingsStore(ContentResolver contentResolver) {
        super(contentResolver, new TypeToken<UserSettings>() {}.getType());
        if (!hasUserSettings()) {
            insertOrUpdate(new UserSettings(DEFAULT_REPOSITORY_ID));
        }
    }

    @Override
    protected Integer getIdFor(UserSettings item) {
        return DEFAULT_USER_ID;
    }

    @Override
    public Uri getContentUri() {
        return UserSettingsContract.CONTENT_URI;
    }

    private boolean hasUserSettings() {
        return query(DEFAULT_USER_ID) != null;
    }
}
