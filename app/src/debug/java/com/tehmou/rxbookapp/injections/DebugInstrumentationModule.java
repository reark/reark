package com.tehmou.rxbookapp.injections;

import com.tehmou.rxbookapp.utils.DebugInstrumentation;
import com.tehmou.rxbookapp.utils.Instrumentation;
import com.tehmou.rxbookapp.utils.LeakCanaryTracing;
import com.tehmou.rxbookapp.utils.LeakTracing;

import android.app.Application;

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
    public Instrumentation providesInstrumentation(LeakTracing leakTracing) {
        return new DebugInstrumentation(leakTracing);
    }

    @Provides
    @Singleton
    public LeakTracing providesLeakTracing(Application application) {
        return new LeakCanaryTracing(application);
    }

}
