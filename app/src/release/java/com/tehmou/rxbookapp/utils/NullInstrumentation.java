package com.tehmou.rxbookapp.utils;

import android.content.Context;

public class NullInstrumentation implements Instrumentation {

    @SuppressWarnings("unused")
    public NullInstrumentation(Context context) { }

    @Override
    public void init() { }

    @Override
    public LeakTracing getLeakTracing() {
        return new NullLeakTracing();
    }

}
