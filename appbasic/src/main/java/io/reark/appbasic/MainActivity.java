package io.reark.appbasic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.reark.rxgithubapp.shared.network.NetworkApi;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
