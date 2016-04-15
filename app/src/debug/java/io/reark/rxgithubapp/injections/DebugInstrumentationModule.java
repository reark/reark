package io.reark.rxgithubapp.injections;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import io.reark.rxgithubapp.network.NetworkInstrumentation;
import io.reark.rxgithubapp.utils.ApplicationInstrumentation;
import io.reark.rxgithubapp.utils.DebugApplicationInstrumentation;
import io.reark.rxgithubapp.utils.LeakCanaryTracing;
import io.reark.rxgithubapp.utils.LeakTracing;
import io.reark.rxgithubapp.utils.StethoInstrumentation;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

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
