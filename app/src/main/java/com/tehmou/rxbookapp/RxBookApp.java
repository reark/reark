package com.tehmou.rxbookapp;

import com.tehmou.rxbookapp.injections.Graph;
import com.tehmou.rxbookapp.utils.Instrumentation;

import android.app.Application;

import javax.inject.Inject;

/**
 * Created by pt2121 on 2/20/15.
 */
public class RxBookApp extends Application {

    private static RxBookApp instance;

    private Graph mGraph;

    @Inject
    Instrumentation instrumentation;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mGraph = Graph.Initializer.init(this);
        getGraph().inject(this);

        instrumentation.init();
    }

    public static RxBookApp getInstance() {
        return instance;
    }

    public Graph getGraph() {
        return mGraph;
    }

}
