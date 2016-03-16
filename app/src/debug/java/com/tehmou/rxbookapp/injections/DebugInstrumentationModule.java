package com.tehmou.rxbookapp.injections;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import com.tehmou.rxbookapp.network.NetworkInstrumentation;
import com.tehmou.rxbookapp.utils.ApplicationInstrumentation;
import com.tehmou.rxbookapp.utils.DebugApplicationInstrumentation;
import com.tehmou.rxbookapp.utils.LeakCanaryTracing;
import com.tehmou.rxbookapp.utils.LeakTracing;
import com.tehmou.rxbookapp.utils.StethoInstrumentation;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DebugInstrumentationModule {

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
    public StethoInstrumentation providesStethoInstrumentation(@ForApplication Context context) {
        return new StethoInstrumentation(context);
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
