package com.tehmou.rxbookapp.pojo;

/**
 * Created by ttuo on 26/04/15.
 */
public class NetworkRequestStatus {
    public static final String NETWORK_STATUS_ONGOING = "networkStatusOngoing";
    public static final String NETWORK_STATUS_ERROR = "networkStatusError";
    public static final String NETWORK_STATUS_COMPLETED = "networkStatusCompleted";

    private final String uri;
    private final String status;

    public NetworkRequestStatus(String uri,
                                String status) {
        this.uri = uri;
        this.status = status;
    }

    public String getUri() {
        return uri;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "NetworkRequestStatus(" + uri + ", " + status + ")";
    }
}
