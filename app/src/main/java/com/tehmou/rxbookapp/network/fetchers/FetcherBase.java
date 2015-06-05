package com.tehmou.rxbookapp.network.fetchers;

import com.tehmou.rxbookapp.network.NetworkApi;
import com.tehmou.rxbookapp.pojo.NetworkRequestStatus;

import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import retrofit.RetrofitError;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by ttuo on 16/04/15.
 */
abstract public class FetcherBase implements Fetcher {
    private static final String TAG = FetcherBase.class.getSimpleName();

    public static final int NO_ERROR_CODE = -1;

    protected final NetworkApi networkApi;
    private final Action1<NetworkRequestStatus> updateNetworkRequestStatus;
    protected final Map<Integer, Subscription> requestMap = new ConcurrentHashMap<>();

    public FetcherBase(NetworkApi networkApi,
                       Action1<NetworkRequestStatus> updateNetworkRequestStatus) {
        this.networkApi = networkApi;
        this.updateNetworkRequestStatus = updateNetworkRequestStatus;
    }

    protected void startRequest(String uri) {
        Log.v(TAG, "startRequest(" + uri + ")");
        updateNetworkRequestStatus.call(NetworkRequestStatus.ongoing(uri));
    }

    protected void errorRequest(String uri, int errorCode, String errorMessage) {
        Log.v(TAG, "errorRequest(" + uri + ", " + errorCode + ", " + errorMessage + ")");
        updateNetworkRequestStatus.call(NetworkRequestStatus.error(uri, errorCode, errorMessage));
    }

    protected void completeRequest(String uri) {
        Log.v(TAG, "completeRequest(" + uri + ")");
        updateNetworkRequestStatus.call(NetworkRequestStatus.completed(uri));
    }

    public Action1<Throwable> doOnError(final String uri) {
        return throwable -> {
            if (throwable instanceof RetrofitError) {
                RetrofitError retrofitError = (RetrofitError) throwable;
                int statusCode = retrofitError.getResponse() != null ?
                        retrofitError.getResponse().getStatus() : NO_ERROR_CODE;
                errorRequest(uri, statusCode, retrofitError.getMessage());
            } else {
                Log.e(TAG, "The error was not a RetroFitError");
                errorRequest(uri, NO_ERROR_CODE, null);
            }
        };
    }
}
