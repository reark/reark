package io.reark.rxgithubapp.network.fetchers;

import android.support.annotation.NonNull;

import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Preconditions;
import io.reark.rxgithubapp.network.NetworkApi;
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
