package com.tehmou.rxbookapp;

import com.tehmou.rxbookapp.injections.Graph;
import com.tehmou.rxbookapp.utils.ApplicationInstrumentation;

import android.app.Application;
import android.support.annotation.NonNull;

import javax.inject.Inject;

/**
 * Created by pt2121 on 2/20/15.
 */
public class RxBookApp extends Application {

    private static RxBookApp instance;

    private Graph mGraph;

    @Inject
    ApplicationInstrumentation mApplicationInstrumentation;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mGraph = Graph.Initializer.init(this);
        getGraph().inject(this);

        mApplicationInstrumentation.init();
    }

    @NonNull
    public static RxBookApp getInstance() {
        return instance;
    }

    @NonNull
    public Graph getGraph() {
        return mGraph;
    }

}
