/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.rxgithubapp.shared.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.shared.network.NetworkInstrumentation;

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
