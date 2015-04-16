package com.tehmou.rxbookapp.network.fetchers;

import com.tehmou.rxbookapp.network.NetworkApi;

/**
 * Created by ttuo on 16/04/15.
 */
abstract public class FetcherBase implements Fetcher {
    protected final NetworkApi networkApi;

    public FetcherBase(NetworkApi networkApi) {
        this.networkApi = networkApi;
    }
}
