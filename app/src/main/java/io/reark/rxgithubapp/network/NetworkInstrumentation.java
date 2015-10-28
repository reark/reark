package io.reark.rxgithubapp.network;

import android.support.annotation.NonNull;

import io.reark.rxgithubapp.utils.Instrumentation;

public interface NetworkInstrumentation<T> extends Instrumentation
{

    @NonNull
    T decorateNetwork(@NonNull final T httpClient);

}
