package com.tehmou.rxbookapp.network;

import android.support.annotation.NonNull;

import com.tehmou.rxbookapp.utils.Instrumentation;

public interface NetworkInstrumentation<T> extends Instrumentation
{

    @NonNull
    T decorateNetwork(@NonNull final T httpClient);

}
