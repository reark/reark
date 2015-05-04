package com.tehmou.rxbookapp.pojo;

/**
 * Created by ttuo on 26/04/15.
 */
public class NetworkRequestStatus {
    private final String NETWORK_STATUS_ONGOING = "networkStatusOngoing";
    private final String NETWORK_STATUS_ERROR = "networkStatusError";
    private final String NETWORK_STATUS_COMPLETED = "networkStatusCompleted";

    private final String uri;
    private final String owner;
    private final String status;
    private final String url;

    public NetworkRequestStatus(String uri,
                                String owner,
                                String status,
                                String url) {
        this.uri = uri;
        this.owner = owner;
        this.status = status;
        this.url = url;
    }

    public String getUri() {
        return uri;
    }

    public String getOwner() {
        return owner;
    }

    public String getStatus() {
        return status;
    }

    public String getUrl() {
        return url;
    }

    public static class Key {
        private final String uri;
        private final String owner;

        public Key(String uri, String owner) {
            this.uri = uri;
            this.owner = owner;
        }

        public String getUri() {
            return uri;
        }

        public String getOwner() {
            return owner;
        }
    }
}
