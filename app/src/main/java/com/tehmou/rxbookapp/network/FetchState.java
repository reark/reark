package com.tehmou.rxbookapp.network;

/**
 * Created by ttuo on 20/04/15.
 */
public class FetchState {
    private final String uri;
    private final int state;

    public FetchState(String uri, int state) {
        this.uri = uri;
        this.state = state;
    }

    public String getUri() {
        return uri;
    }

    public int getState() {
        return state;
    }
}
