package com.tehmou.rxbookapp.pojo;

/**
 * Created by ttuo on 26/04/15.
 */
public class NetworkRequestStatus {
    private static final String NETWORK_STATUS_ONGOING = "networkStatusOngoing";
    private static final String NETWORK_STATUS_ERROR = "networkStatusError";
    private static final String NETWORK_STATUS_COMPLETED = "networkStatusCompleted";

    private final String uri;
    private final String status;

    private NetworkRequestStatus(String uri,
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

    public static NetworkRequestStatus ongoing(String uri) {
        return new NetworkRequestStatus(uri, NETWORK_STATUS_ONGOING);
    }

    public static NetworkRequestStatus error(String uri) {
        return new NetworkRequestStatus(uri, NETWORK_STATUS_ERROR);
    }

    public static NetworkRequestStatus completed(String uri) {
        return new NetworkRequestStatus(uri, NETWORK_STATUS_COMPLETED);
    }

    public boolean isOngoing() {
        return status.equals(NETWORK_STATUS_ONGOING);
    }

    public boolean isError() {
        return status.equals(NETWORK_STATUS_ERROR);
    }

    public boolean isCompleted() {
        return status.equals(NETWORK_STATUS_COMPLETED);
    }
}
