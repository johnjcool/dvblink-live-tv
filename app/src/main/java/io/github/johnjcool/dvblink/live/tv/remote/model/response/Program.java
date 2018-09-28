package io.github.johnjcool.dvblink.live.tv.remote.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.io.Serializable;
import java.util.List;

public class Program extends VideoInfo {

    @JsonProperty("program_id")
    private String id;
    @JsonProperty("is_record")
    public boolean record;
    @JsonProperty("is_record_conflict")
    public boolean recordConflict;
    @JsonProperty("is_repeat_record")
    public boolean repeatRecord;
    @JsonProperty("is_series")
    public boolean series;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRecord() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    public boolean isRecordConflict() {
        return recordConflict;
    }

    public void setRecordConflict(boolean recordConflict) {
        this.recordConflict = recordConflict;
    }

    public boolean isRepeatRecord() {
        return repeatRecord;
    }

    public void setRepeatRecord(boolean repeatRecord) {
        this.repeatRecord = repeatRecord;
    }

    public boolean isSeries() {
        return series;
    }

    public void setSeries(boolean series) {
        this.series = series;
    }

    @Override
    public String toString() {
        return "Program{" +
                "id='" + id + '\'' +
                ", record=" + record +
                ", recordConflict=" + recordConflict +
                ", repeatRecord=" + repeatRecord +
                ", series=" + series +
                '}' +
                super.toString();
    }

    public static class EpgSearcher implements Serializable {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonProperty("channel_epg")
        private List<ChannelEpg> channelEpgs;

        public List<ChannelEpg> getChannelEpgs() {
            return channelEpgs;
        }

        public void setChannelEpgs(List<ChannelEpg> channelEpgs) {
            this.channelEpgs = channelEpgs;
        }

        @Override
        public String toString() {
            return "EpgSearcher{" +
                    "channelEpgs=" + channelEpgs +
                    '}';
        }
    }

    public static class ChannelEpg implements Serializable {

        @JsonProperty("channel_id")
        private String channelId;

        @JacksonXmlElementWrapper(localName = "dvblink_epg")
        @JsonProperty("program")
        private List<Program> programs;

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public List<Program> getPrograms() {
            return programs;
        }

        public void setPrograms(List<Program> programs) {
            this.programs = programs;
        }

        @Override
        public String toString() {
            return "ChannelEpg{" +
                    "channelId='" + channelId + '\'' +
                    ", programs=" + programs +
                    '}';
        }
    }
}
