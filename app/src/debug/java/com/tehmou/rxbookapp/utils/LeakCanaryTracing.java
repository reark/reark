package com.tehmou.rxbookapp.utils;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Pawel Polanski on 5/9/15.
 */
public class LeakCanaryTracing implements LeakTracing {

    private RefWatcher refWatcher;

    private final Application application;

    public LeakCanaryTracing(Application application) {
        this.application = application;
    }

    @Override
    public void init() {
        refWatcher = LeakCanary.install(application);
    }

    @Override
    public void traceLeakage(Object reference) {
        refWatcher.watch(reference);
    }
}
