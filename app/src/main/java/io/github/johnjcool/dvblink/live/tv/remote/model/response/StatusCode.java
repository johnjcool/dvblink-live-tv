package io.github.johnjcool.dvblink.live.tv.remote.model.response;

public enum StatusCode {

    STATUS_OK(0),
    STATUS_ERROR(1000),
    STATUS_INVALID_DATA(1001),
    STATUS_INVALID_PARAM(1002),
    STATUS_NOT_IMPLEMENTED(1003),
    STATUS_MC_NOT_RUNNING(1005),
    STATUS_NO_DEFAULT_RECORDER(1006),
    STATUS_MCE_CONNECTION_ERROR(1008);
    
    private int value;

    StatusCode(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
