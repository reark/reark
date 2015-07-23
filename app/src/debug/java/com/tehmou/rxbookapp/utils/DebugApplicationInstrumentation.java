package com.tehmou.rxbookapp.utils;

import android.support.annotation.NonNull;

import rx.android.internal.Preconditions;

public class DebugApplicationInstrumentation implements ApplicationInstrumentation
{

    @NonNull
    private final LeakTracing leakTracing;

    @NonNull
    private final Instrumentation instrumentation;

    public DebugApplicationInstrumentation(@NonNull LeakTracing leakTracing,
                                           @NonNull Instrumentation instrumentation) {

        Preconditions.checkNotNull(leakTracing, "Leak Tracing cannot be null.");
        Preconditions.checkNotNull(instrumentation, "Instrumentation cannot be null.");

        this.leakTracing = leakTracing;
        this.instrumentation = instrumentation;
    }

    @Override
    public void init() {
        leakTracing.init();
        instrumentation.init();
    }

    @NonNull
    @Override
    public LeakTracing getLeakTracing() {
        return leakTracing;
    }

}
