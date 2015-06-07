package com.tehmou.rxbookapp.utils;

/**
 * Created by Pawel Polanski on 5/9/15.
 */
public class NullLeakTracing implements LeakTracing {

    @Override
    public void init() { }

    @Override
    public void traceLeakage(Object reference) { }
}
