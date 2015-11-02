package io.reark.rxgithubapp;

import android.app.Application;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reark.rxgithubapp.injections.Graph;
import io.reark.rxgithubapp.utils.ApplicationInstrumentation;

/**
 * Created by pt2121 on 2/20/15.
 */
public class RxGitHubApp extends Application {

    private static RxGitHubApp instance;

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
    public static RxGitHubApp getInstance() {
        return instance;
    }

    @NonNull
    public Graph getGraph() {
        return mGraph;
    }

}
