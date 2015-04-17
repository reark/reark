package com.tehmou.rxbookapp.network.fetchers;

import com.tehmou.rxbookapp.network.NetworkApi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Subscription;

/**
 * Created by ttuo on 16/04/15.
 */
abstract public class FetcherBase implements Fetcher {
    protected final NetworkApi networkApi;
    protected final Map<Integer, Subscription> requestMap = new ConcurrentHashMap<>();

    public FetcherBase(NetworkApi networkApi) {
        this.networkApi = networkApi;
    }
}
