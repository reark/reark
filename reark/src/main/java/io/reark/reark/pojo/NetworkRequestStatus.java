package io.reark.reark.pojo;

/**
 * Created by ttuo on 26/04/15.
 */
public class NetworkRequestStatus {
    private static final String NETWORK_STATUS_ONGOING = "networkStatusOngoing";
    private static final String NETWORK_STATUS_ERROR = "networkStatusError";
    private static final String NETWORK_STATUS_COMPLETED = "networkStatusCompleted";

    private final String uri;
    private final String status;
    private final int errorCode;
    private final String errorMessage;

    private NetworkRequestStatus(String uri,
                                 String status,
                                 int errorCode,
                                 String errorMessage) {
        this.uri = uri;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getUri() {
        return uri;
    }

    public String getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "NetworkRequestStatus(" + uri + ", " + status + ")";
    }

    public static NetworkRequestStatus ongoing(String uri) {
        return new NetworkRequestStatus(uri, NETWORK_STATUS_ONGOING, 0, null);
    }

    public static NetworkRequestStatus error(String uri, int errorCode, String errorMessage) {
        return new NetworkRequestStatus(uri, NETWORK_STATUS_ERROR, 0, errorMessage);
    }

    public static NetworkRequestStatus completed(String uri) {
        return new NetworkRequestStatus(uri, NETWORK_STATUS_COMPLETED, 0, null);
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
