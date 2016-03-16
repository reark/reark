package com.tehmou.rxbookapp.network;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public final class NetworkModule {

    @Provides
    @Singleton
    public NetworkApi provideNetworkApi(OkHttpClient client) {
        return new NetworkApi(client);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(NetworkInstrumentation<OkHttpClient> networkInstrumentation) {
        OkHttpClient client = new OkHttpClient().newBuilder().addNetworkInterceptor(networkInstrumentation.getInterceptor()).build();
        return networkInstrumentation.decorateNetwork(client);
    }

}