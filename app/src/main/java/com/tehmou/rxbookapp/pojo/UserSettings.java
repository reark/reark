package com.tehmou.rxbookapp.pojo;

/**
 * Created by ttuo on 06/04/15.
 */
public class UserSettings {
    private final int selectedRepositoryId;

    public UserSettings(int selectedRepositoryId) {
        this.selectedRepositoryId = selectedRepositoryId;
    }

    public int getSelectedRepositoryId() {
        return selectedRepositoryId;
    }
}
