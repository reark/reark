package io.reark.rxbookapp.injections;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;
import io.reark.rxbookapp.network.NetworkInstrumentation;
import io.reark.rxbookapp.utils.ApplicationInstrumentation;
import io.reark.rxbookapp.utils.NullInstrumentation;
import io.reark.rxbookapp.utils.NullNetworkInstrumentation;

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
