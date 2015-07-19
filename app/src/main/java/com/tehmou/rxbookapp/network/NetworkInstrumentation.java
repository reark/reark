package com.tehmou.rxbookapp.network;

import com.tehmou.rxbookapp.utils.Instrumentation;

import android.support.annotation.NonNull;

public interface NetworkInstrumentation<T> extends Instrumentation
{

    @NonNull
    T decorateNetwork(@NonNull final T httpClient);

}
