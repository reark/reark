package io.reark.rxgithubapp.injections;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reark.rxgithubapp.network.NetworkInstrumentation;
import io.reark.rxgithubapp.utils.ApplicationInstrumentation;
import io.reark.rxgithubapp.utils.NullInstrumentation;
import io.reark.rxgithubapp.utils.NullNetworkInstrumentation;

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
