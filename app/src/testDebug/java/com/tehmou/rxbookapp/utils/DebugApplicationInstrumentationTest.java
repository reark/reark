package com.tehmou.rxbookapp.utils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

/**
 * Created by Pawel Polanski on 7/20/15.
 */
public class DebugApplicationInstrumentationTest {

    private DebugApplicationInstrumentation applicationInstrumentation;

    @Mock
    LeakTracing leakTracing;

    @Mock
    Instrumentation instrumentation;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        applicationInstrumentation = new DebugApplicationInstrumentation(leakTracing,
                                                                         instrumentation);
    }

    @Test
    public void testInitSetsUpAllInstrumentations() {
        applicationInstrumentation.init();

        verify(leakTracing).init();
        verify(instrumentation).init();
    }

}
