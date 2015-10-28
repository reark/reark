package io.reark.rxbookapp.network;

import android.support.annotation.NonNull;

import io.reark.rxbookapp.utils.Instrumentation;

public interface NetworkInstrumentation<T> extends Instrumentation
{

    @NonNull
    T decorateNetwork(@NonNull final T httpClient);

}
