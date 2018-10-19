package io.github.johnjcool.dvblink.live.tv.remote.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonRootName(value = "schedule", namespace = "http://www.dvblogic.com")
public class Schedule {

    @JsonProperty("user_param")
    private String userParam;

    @JsonProperty("force_add")
    private boolean forceAdd;

    @JsonProperty("margine_before")
    private int margineBefore;

    @JsonProperty("margine_after")
    private int margineAfter;

    @JsonProperty("by_epg")
    private ByEpg byEpg;

    @JsonProperty("manual")
    private Manual manual;

    public Schedule(ByEpg byEpg) {
        this.byEpg = byEpg;
    }

    public Schedule(Manual manual) {
        this.manual = manual;
    }

    public String getUserParam() {
        return userParam;
    }

    public void setUserParam(String userParam) {
        this.userParam = userParam;
    }

    public boolean isForceAdd() {
        return forceAdd;
    }

    public void setForceAdd(boolean forceAdd) {
        this.forceAdd = forceAdd;
    }

    public int getMargineBefore() {
        return margineBefore;
    }

    public void setMargineBefore(int margineBefore) {
        this.margineBefore = margineBefore;
    }

    public int getMargineAfter() {
        return margineAfter;
    }

    public void setMargineAfter(int margineAfter) {
        this.margineAfter = margineAfter;
    }

    public ByEpg getByEpg() {
        return byEpg;
    }

    public void setByEpg(ByEpg byEpg) {
        this.byEpg = byEpg;
    }

    public Manual getManual() {
        return manual;
    }

    public void setManual(Manual manual) {
        this.manual = manual;
    }

    public boolean isByEpg() {
        return byEpg != null;
    }

    public boolean isManual() {
        return manual != null;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "userParam='" + userParam + '\'' +
                ", forceAdd=" + forceAdd +
                ", margineBefore=" + margineBefore +
                ", margineAfter=" + margineAfter +
                ", byEpg=" + byEpg +
                ", manual=" + manual +
                '}';
    }

    public static class ByEpg {
        @JsonProperty("channel_id")
        private String channelId;
        @JsonProperty("program_id")
        private String programId;
        @JsonProperty("repeat")
        private boolean repeat;
        @JsonProperty("new_only")
        private boolean newOnly;
        @JsonProperty("record_series_anytime")
        private boolean recordSeriesAnytime;
        @JsonProperty("recordings_to_keep")
        private int recordingsToKeep;

        public ByEpg(String channelId, String programId) {
            this.channelId = channelId;
            this.programId = programId;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getProgramId() {
            return programId;
        }

        public void setProgramId(String programId) {
            this.programId = programId;
        }

        public boolean isRepeat() {
            return repeat;
        }

        public void setRepeat(boolean repeat) {
            this.repeat = repeat;
        }

        public boolean isNewOnly() {
            return newOnly;
        }

        public void setNewOnly(boolean newOnly) {
            this.newOnly = newOnly;
        }

        public boolean isRecordSeriesAnytime() {
            return recordSeriesAnytime;
        }

        public void setRecordSeriesAnytime(boolean recordSeriesAnytime) {
            this.recordSeriesAnytime = recordSeriesAnytime;
        }

        public int getRecordingsToKeep() {
            return recordingsToKeep;
        }

        public void setRecordingsToKeep(int recordingsToKeep) {
            this.recordingsToKeep = recordingsToKeep;
        }

        @Override
        public String toString() {
            return "ByEpg{" +
                    "channelId='" + channelId + '\'' +
                    ", programId='" + programId + '\'' +
                    ", repeat=" + repeat +
                    ", newOnly=" + newOnly +
                    ", recordSeriesAnytime=" + recordSeriesAnytime +
                    ", recordingsToKeep=" + recordingsToKeep +
                    '}';
        }
    }

    public static class Manual {

        @JsonProperty("channel_id")
        private String channelId;
        @JsonProperty("title")
        private String title;
        @JsonProperty("start_time")
        private long startTime;
        @JsonProperty("duration")
        private long duration;
        @JsonProperty("day_mask")
        private DayMask dayMask;
        @JsonProperty("recordings_to_keep")
        private int recordingsToKeep;

        public Manual(String channelId, long startTime, long duration, DayMask dayMask) {
            this.channelId = channelId;
            this.startTime = startTime;
            this.duration = duration;
            this.dayMask = dayMask;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public DayMask getDayMask() {
            return dayMask;
        }

        public void setDayMask(DayMask dayMask) {
            this.dayMask = dayMask;
        }

        public int getRecordingsToKeep() {
            return recordingsToKeep;
        }

        public void setRecordingsToKeep(int recordingsToKeep) {
            this.recordingsToKeep = recordingsToKeep;
        }

        @Override
        public String toString() {
            return "Manual{" +
                    "channelId='" + channelId + '\'' +
                    ", title='" + title + '\'' +
                    ", startTime=" + startTime +
                    ", duration=" + duration +
                    ", dayMask=" + dayMask +
                    ", recordingsToKeep=" + recordingsToKeep +
                    '}';
        }
    }

    public enum DayMask {
        DAY_MASK_SUN(1),
        DAY_MASK_MON(2),
        DAY_MASK_TUE(4),
        DAY_MASK_WED(8),
        DAY_MASK_THU(16),
        DAY_MASK_FRI(32),
        DAY_MASK_SAT(64),
        DAY_MASK_DAILY(255);

        private final int mValue;

        DayMask(int value) {
            this.mValue = value;
        }

        @JsonValue
        @Override
        public String toString() {
            return String.valueOf(mValue);
        }
    }
}
