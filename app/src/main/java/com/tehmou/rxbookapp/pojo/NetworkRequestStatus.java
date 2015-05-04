package com.tehmou.rxbookapp.pojo;

/**
 * Created by ttuo on 26/04/15.
 */
public class NetworkRequestStatus {
    public static final String NETWORK_STATUS_ONGOING = "networkStatusOngoing";
    public static final String NETWORK_STATUS_ERROR = "networkStatusError";
    public static final String NETWORK_STATUS_COMPLETED = "networkStatusCompleted";

    private final String uri;
    private final String owner;
    private final String status;

    public NetworkRequestStatus(String uri,
                                String owner,
                                String status) {
        this.uri = uri;
        this.owner = owner;
        this.status = status;
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

    public static class Key {
        private final String uri;
        private final String owner;

        public Key() {
            this(null, null);
        }

        public Key(String uri) {
            this(uri, null);
        }

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
