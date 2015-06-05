package com.tehmou.rxbookapp.utils;

public class DebugInstrumentation implements Instrumentation {

    private final LeakTracing leakTracing;

    public DebugInstrumentation(LeakTracing leakTracing) {
        this.leakTracing = leakTracing;
    }

    @Override
    public void init() {
        initLeakTracing();
    }

    @Override
    public LeakTracing getLeakTracing() {
        return leakTracing;
    }

    private void initLeakTracing() {
        leakTracing.init();
    }

}
