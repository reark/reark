package com.tehmou.rxbookapp.utils;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.tehmou.rxbookapp.network.NetworkInstrumentation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import rx.android.internal.Preconditions;

import static com.facebook.stetho.Stetho.defaultDumperPluginsProvider;
import static com.facebook.stetho.Stetho.defaultInspectorModulesProvider;
import static com.facebook.stetho.Stetho.initialize;
import static com.facebook.stetho.Stetho.newInitializerBuilder;

public class StethoInstrumentation implements NetworkInstrumentation<OkHttpClient> {

    @NonNull
    private final Context context;


    public StethoInstrumentation(@NonNull Context context) {
        Preconditions.checkNotNull(context, "Context cannot be null.");
        this.context = context;
    }


    @Override
    public void init() {
        initStetho();
    }

    @VisibleForTesting
    void initStetho() {
        initialize(
                newInitializerBuilder(context)
                        .enableDumpapp(defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(defaultInspectorModulesProvider(context))
                        .build());
    }

    @NonNull
    public OkHttpClient decorateNetwork(@NonNull final OkHttpClient httpClient) {
        Preconditions.checkNotNull(httpClient, "Http Client cannot be null.");

        return httpClient;
    }

    @VisibleForTesting
    @Override
    public Interceptor getInterceptor() {
        return new StethoInterceptor();
    }



}
