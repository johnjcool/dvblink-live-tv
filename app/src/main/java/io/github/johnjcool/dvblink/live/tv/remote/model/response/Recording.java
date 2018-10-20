package io.github.johnjcool.dvblink.live.tv.remote.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.io.Serializable;
import java.util.List;

public class Recording {

    @JsonProperty("recording_id")
    private String recordingId;
    @JsonProperty("channel_id")
    private String channelId;
    @JsonProperty("schedule_id")
    private String scheduleId;
    @JsonProperty("is_active")
    private boolean active;
    @JsonProperty("program")
    private Program program;

    public String getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(String recordingId) {
        this.recordingId = recordingId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public String toString() {
        return "Recording{" +
                "recordingId='" + recordingId + '\'' +
                ", channelId='" + channelId + '\'' +
                ", scheduleId='" + scheduleId + '\'' +
                ", active=" + active +
                ", program=" + program +
                '}';
    }

    public static class Recordings implements Serializable {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonProperty("recording")
        private List<Recording> recordings;

        public List<Recording> getRecordings() {
            return recordings;
        }

        public void setRecordings(List<Recording> recordings) {
            this.recordings = recordings;
        }

        @Override
        public String toString() {
            return "Recordings{" +
                    "recordings=" + recordings +
                    '}';
        }
    }
}
