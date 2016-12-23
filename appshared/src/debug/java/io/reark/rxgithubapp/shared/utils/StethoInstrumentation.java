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

import io.reark.rxgithubapp.shared.network.NetworkInstrumentation;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import static com.facebook.stetho.Stetho.defaultDumperPluginsProvider;
import static com.facebook.stetho.Stetho.defaultInspectorModulesProvider;
import static com.facebook.stetho.Stetho.initialize;
import static com.facebook.stetho.Stetho.newInitializerBuilder;
import static io.reark.reark.utils.Preconditions.get;

public class StethoInstrumentation implements NetworkInstrumentation<OkHttpClient.Builder> {

    @NonNull
    private final Context context;

    @NonNull
    private final Interceptor interceptor;

    public StethoInstrumentation(@NonNull final Context context,
                                 @NonNull final Interceptor interceptor) {
        this.context = get(context);
        this.interceptor = get(interceptor);
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

    @Override
    @NonNull
    public OkHttpClient.Builder decorateNetwork(@NonNull final OkHttpClient.Builder clientBuilder) {
        return get(clientBuilder).addNetworkInterceptor(interceptor);
    }
}
