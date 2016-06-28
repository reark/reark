package io.reark.rxgithubapp.basic.data.stores;

import io.reark.rxgithubapp.shared.pojo.UserSettings;

/**
 * Created by ttuo on 27/06/16.
 */
public class UserSettingsStore extends MemoryStore<Integer, UserSettings> {
    public UserSettingsStore(int userId) {
        super(userSettings -> userId);
    }
}
