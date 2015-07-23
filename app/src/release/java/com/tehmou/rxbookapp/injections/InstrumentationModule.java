package com.tehmou.rxbookapp.injections;

import com.squareup.okhttp.OkHttpClient;
import com.tehmou.rxbookapp.network.NetworkInstrumentation;
import com.tehmou.rxbookapp.utils.ApplicationInstrumentation;
import com.tehmou.rxbookapp.utils.NullInstrumentation;
import com.tehmou.rxbookapp.utils.NullNetworkInstrumentation;

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
    public ApplicationInstrumentation providesInstrumentation(@ForApplication Context context) {
        return new NullInstrumentation(context);
    }

    @Provides
    @Singleton
    public NetworkInstrumentation<OkHttpClient> providesNetworkInstrumentation() {
        return new NullNetworkInstrumentation();
    }

}
