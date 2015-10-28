package io.reark.rxbookapp.injections;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import io.reark.rxbookapp.network.NetworkInstrumentation;
import io.reark.rxbookapp.utils.ApplicationInstrumentation;
import io.reark.rxbookapp.utils.DebugApplicationInstrumentation;
import io.reark.rxbookapp.utils.LeakCanaryTracing;
import io.reark.rxbookapp.utils.LeakTracing;
import io.reark.rxbookapp.utils.StethoInstrumentation;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pawel Polanski on 4/24/15.
 */
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
