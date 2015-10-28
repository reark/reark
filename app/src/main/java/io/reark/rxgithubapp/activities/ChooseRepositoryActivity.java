package io.reark.rxgithubapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.reark.rxgithubapp.R;
import io.reark.rxgithubapp.fragments.RepositoriesFragment;


public class ChooseRepositoryActivity extends AppCompatActivity {
    private static final String TAG = ChooseRepositoryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RepositoriesFragment())
                    .commit();
        }
    }

    public void chooseRepository(int repositoryId) {
        Intent data = new Intent();
        data.putExtra("repositoryId", repositoryId);
        setResult(0, data);
        finish();
    }
}
