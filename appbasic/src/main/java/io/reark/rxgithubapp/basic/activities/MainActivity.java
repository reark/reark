package io.reark.rxgithubapp.basic.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import io.reark.reark.utils.Log;
import io.reark.rxgithubapp.basic.R;
import io.reark.rxgithubapp.basic.RxGitHubApp;
import io.reark.rxgithubapp.basic.fragments.RepositoryFragment;
import io.reark.rxgithubapp.shared.data.DataFunctions;
import io.reark.rxgithubapp.shared.pojo.UserSettings;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int DEFAULT_REPOSITORY_ID = 15491874;

    @Inject
    DataFunctions.SetUserSettings setUserSettings;

    public MainActivity() {
        RxGitHubApp.getInstance().getGraph().inject(this);

        // User settings are not persisted
        setUserSettings.call(new UserSettings(DEFAULT_REPOSITORY_ID));
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
