package com.tehmou.rxbookapp.utils;

import android.content.Context;
import android.support.annotation.NonNull;

public class NullInstrumentation implements ApplicationInstrumentation
{

    @SuppressWarnings("unused")
    public NullInstrumentation(Context context) { }

    @Override
    public void init() { }

    @NonNull
    @Override
    public LeakTracing getLeakTracing() {
        return new NullLeakTracing();
    }

}
