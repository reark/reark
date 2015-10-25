package com.tehmou.rxbookapp.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.tehmou.rxandroidarchitecture.network.fetchers.FetcherBase;
import com.tehmou.rxandroidarchitecture.pojo.NetworkRequestStatus;
import com.tehmou.rxbookapp.network.NetworkApi;

import rx.android.internal.Preconditions;
import rx.functions.Action1;

/**
 * Created by antti on 25.10.2015.
 */
public abstract class AppFetcherBase extends FetcherBase {

    @NonNull
    NetworkApi networkApi;

    public AppFetcherBase(@NonNull NetworkApi networkApi,
                          @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus) {
        super(updateNetworkRequestStatus);

        Preconditions.checkNotNull(networkApi, "Network Api cannot be null.");

        this.networkApi = networkApi;
    }
}
