package com.tehmou.rxbookapp.injections;

import com.tehmou.rxbookapp.utils.Instrumentation;
import com.tehmou.rxbookapp.utils.NullInstrumentation;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pawel Polanski on 4/24/15.
 */
@Module
public class InstrumentationModule {

    @Provides
    @Singleton
    public Instrumentation providesInstrumentation(@ForApplication Context context) {
        return new NullInstrumentation(context);
    }

}
