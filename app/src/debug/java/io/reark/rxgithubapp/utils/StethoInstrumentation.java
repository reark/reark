package io.reark.rxgithubapp.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.network.NetworkInstrumentation;

import static com.facebook.stetho.Stetho.defaultDumperPluginsProvider;
import static com.facebook.stetho.Stetho.defaultInspectorModulesProvider;
import static com.facebook.stetho.Stetho.initialize;
import static com.facebook.stetho.Stetho.newInitializerBuilder;

public class StethoInstrumentation implements NetworkInstrumentation<OkHttpClient> {

    @NonNull
    private final Context context;

    @NonNull
    private final Interceptor interceptor;

    public StethoInstrumentation(@NonNull Context context, @NonNull Interceptor interceptor) {
        Preconditions.checkNotNull(context, "Context cannot be null.");
        Preconditions.checkNotNull(interceptor, "Interceptor cannot be null.");

        this.context = context;
        this.interceptor = interceptor;
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

        addInterceptor(httpClient, interceptor);

        return httpClient;
    }

    @VisibleForTesting
    void addInterceptor(@NonNull OkHttpClient httpClient, @NonNull Interceptor interceptor) {
        Preconditions.checkNotNull(httpClient, "Http Client cannot be null.");
        Preconditions.checkNotNull(interceptor, "Interceptor cannot be null.");

        httpClient.networkInterceptors().add(interceptor);
    }

}
