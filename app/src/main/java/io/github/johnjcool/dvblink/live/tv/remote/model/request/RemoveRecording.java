package io.github.johnjcool.dvblink.live.tv.remote.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;

/**
 * Request xml data
 * <remove_recording xmlns:i="http://www.w3.org/2001/XMLSchema-instance"
 * xmlns="http://www.dvblogic.com">
 * <recording_id/> - string mandatory
 * </remove_recording>
 */
@JsonRootName(value = "remove_recording", namespace = "http://www.dvblogic.com")
public class RemoveRecording implements Serializable {

    @JsonProperty("recording_id")
    private String recordingId;

    public RemoveRecording(String recordingId) {
        this.recordingId = recordingId;
    }

    public String getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(String recordingId) {
        this.recordingId = recordingId;
    }

    @Override
    public String toString() {
        return "RemoveRecording{" +
                "recordingId='" + recordingId + '\'' +
                '}';
    }
}
