package com.tehmou.rxbookapp.network;

import com.tehmou.rxbookapp.utils.Instrumentation;

import android.support.annotation.NonNull;

import okhttp3.Interceptor;

public interface NetworkInstrumentation<T> extends Instrumentation
{

    @NonNull
    T decorateNetwork(@NonNull final T httpClient);

    Interceptor getInterceptor();
}
