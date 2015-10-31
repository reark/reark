package com.tehmou.rxbookapp;

import android.app.Application;
import android.support.annotation.NonNull;

import com.tehmou.rxbookapp.injections.Graph;
import com.tehmou.rxbookapp.utils.ApplicationInstrumentation;

import javax.inject.Inject;

/**
 * Created by pt2121 on 2/20/15.
 */
public class RxBookApp extends Application {

    private static RxBookApp instance;

    private Graph mGraph;

    @Inject
    ApplicationInstrumentation instrumentation;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mGraph = Graph.Initializer.init(this);
        getGraph().inject(this);

        instrumentation.init();
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
