package com.tehmou.rxbookapp;

import com.tehmou.rxbookapp.injections.Graph;

import android.app.Application;

/**
 * Created by pt2121 on 2/20/15.
 */
public class RxBookApp extends Application {

    private static RxBookApp instance;

    private Graph mGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mGraph = Graph.Initializer.init(this);
    }

    public static RxBookApp getInstance() {
        return instance;
    }

    public Graph getGraph() {
        return mGraph;
    }

}
