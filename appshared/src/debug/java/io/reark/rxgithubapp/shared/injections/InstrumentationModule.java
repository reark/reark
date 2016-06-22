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
package io.reark.rxgithubapp.shared.injections;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reark.rxgithubapp.shared.network.NetworkInstrumentation;
import io.reark.rxgithubapp.shared.utils.ApplicationInstrumentation;
import io.reark.rxgithubapp.shared.utils.DebugApplicationInstrumentation;
import io.reark.rxgithubapp.shared.utils.LeakCanaryTracing;
import io.reark.rxgithubapp.shared.utils.LeakTracing;
import io.reark.rxgithubapp.shared.utils.StethoInstrumentation;

@Module
public class InstrumentationModule {

    @Provides
    @Singleton
    public ApplicationInstrumentation providesInstrumentation(LeakTracing leakTracing, StethoInstrumentation instrumentation) {
        return new DebugApplicationInstrumentation(leakTracing, instrumentation);
    }

    @Provides
    @Singleton
    public LeakTracing providesLeakTracing(Application application) {
        return new LeakCanaryTracing(application);
    }

    @Provides
    @Singleton
    public StethoInstrumentation providesStethoInstrumentation(@ForApplication Context context,
                                                               Interceptor interceptor) {
        return new StethoInstrumentation(context, interceptor);
    }

    @Provides
    @Singleton
    public NetworkInstrumentation<OkHttpClient> providesNetworkInstrumentation(StethoInstrumentation instrumentation) {
        return instrumentation;
    }

    @Provides
    @Singleton
    public Interceptor providesStethoIntercetor() {
        return new StethoInterceptor();
    }

}
