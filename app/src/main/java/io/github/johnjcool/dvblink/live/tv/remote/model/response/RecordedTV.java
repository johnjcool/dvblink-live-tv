package io.github.johnjcool.dvblink.live.tv.remote.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <object_id/> - string mandatory
 * <parent_id/> - string mandatory
 * <url/> - string mandatory, url containing http link for stream playback
 * <thumbnail/> - string mandatory, url containing a hyperlink to item’s thumbnail
 * <can_be_deleted/> - bool optional, identifies whether this item can be deleted
 * <size/> - long optional, item file size in bytes
 * <creation_time/> - long optional
 * <channel_name/> - string optional
 * <channel_number/> - int optional
 * <channel_subnumber/>- int optional
 * <channel_id/>- string mandatory
 * <schedule_id/>- string mandatory
 * <schedule_name/>- string mandatory
 * <schedule_series/>- bool mandatory, shows if this recorded tv item was part of series
 * recording
 * <state/> - int optional (RTVS_IN_PROGRESS = 0, RTVS_ERROR = 1,
 * RTVS_FORCED_TO_COMPLETION = 2, RTVS_COMPLETED = 3). State of the recorded TV item:
 * being recorded, not even started because of error, recorded, but may miss certain part at the end
 * because it was cancelled by user, completed successfully.
 * <video_info> - mandatory
 */
public class RecordedTV {

    @JsonProperty("object_id")
    private String objectId;
    @JsonProperty("parent_id")
    private String parentId;
    @JsonProperty("url")
    private String url;
    @JsonProperty("thumbnail")
    private String thumbnail;
    @JsonProperty("can_be_deleted")
    private boolean canBeDeleted;
    @JsonProperty("size")
    private long size;
    @JsonProperty("creation_time")
    private long creationTime;
    @JsonProperty("channel_id")
    private String channelId;
    @JsonProperty("channel_name")
    private String channelName;
    @JsonProperty("channel_number")
    private int channelNumber;
    @JsonProperty("channel_subnumber")
    private int channelSubnumber;
    @JsonProperty("schedule_id")
    private String scheduleId;
    @JsonProperty("schedule_name")
    private String scheduleName;
    @JsonProperty("schedule_series")
    private boolean scheduleSeries;
    @JsonProperty("state")
    private State state;
    @JsonProperty("video_info")
    private VideoInfo videoInfo;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isCanBeDeleted() {
        return canBeDeleted;
    }

    public void setCanBeDeleted(boolean canBeDeleted) {
        this.canBeDeleted = canBeDeleted;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    public int getChannelSubnumber() {
        return channelSubnumber;
    }

    public void setChannelSubnumber(int channelSubnumber) {
        this.channelSubnumber = channelSubnumber;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public boolean isScheduleSeries() {
        return scheduleSeries;
    }

    public void setScheduleSeries(boolean scheduleSeries) {
        this.scheduleSeries = scheduleSeries;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public String toString() {
        return "RecordedTV{" +
                "objectId='" + objectId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", url='" + url + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", canBeDeleted=" + canBeDeleted +
                ", size=" + size +
                ", creationTime=" + creationTime +
                ", channelId='" + channelId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", channelNumber=" + channelNumber +
                ", channelSubnumber=" + channelSubnumber +
                ", scheduleId='" + scheduleId + '\'' +
                ", scheduleName='" + scheduleName + '\'' +
                ", scheduleSeries=" + scheduleSeries +
                ", state=" + state +
                ", videoInfo=" + videoInfo +
                '}';
    }

    public enum State {
        RTVS_IN_PROGRESS(0),
        RTVS_ERROR(1),
        RTVS_FORCED_TO_COMPLETION(2),
        RTVS_COMPLETED(3);

        private int value;

        State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}