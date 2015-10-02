package com.tehmou.rxbookapp.activities;

import com.tehmou.rxbookapp.R;
import com.tehmou.rxbookapp.RxBookApp;
import com.tehmou.rxbookapp.data.DataLayer;
import com.tehmou.rxbookapp.fragments.RepositoryFragment;
import com.tehmou.rxbookapp.pojo.UserSettings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    DataLayer.SetUserSettings setUserSettings;

    public MainActivity() {
        RxBookApp.getInstance().getGraph().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RepositoryFragment())
                    .commit();
        }
    }

    public void chooseRepository() {
        Log.d(TAG, "chooseRepository");
        Intent intent = new Intent(this, ChooseRepositoryActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (data == null) {
            Log.d(TAG, "No data from onActivityResult");
            return;
        }
        final int repositoryId = data.getIntExtra("repositoryId", 0);
        if (repositoryId == 0) {
            Log.e(TAG, "Invalid repositoryId from onActivityResult");
            return;
        }
        Log.d(TAG, "New repositoryId: " + repositoryId);
        // We should probably send an intent to update the widget
        // in case its service is not alive anymore. This works as
        // long as it is alive, though.
        setUserSettings.call(new UserSettings(repositoryId));
    }
}
