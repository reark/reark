package com.tehmou.rxbookapp.utils;

import com.squareup.okhttp.OkHttpClient;
import com.tehmou.rxbookapp.network.NetworkInstrumentation;

import android.support.annotation.NonNull;

import rx.android.internal.Preconditions;
/**
 * Created by Pawel Polanski on 7/18/15.
 */
public class NullNetworkInstrumentation implements NetworkInstrumentation<OkHttpClient> {

    @NonNull
    @Override
    public OkHttpClient decorateNetwork(@NonNull OkHttpClient httpClient) {
        return httpClient;
    }

    @Override
    public void init() { }
}
