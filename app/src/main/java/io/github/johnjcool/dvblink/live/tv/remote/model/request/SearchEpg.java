package io.github.johnjcool.dvblink.live.tv.remote.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;
import java.util.List;

@JsonRootName(value = "epg_searcher", namespace = "http://www.dvblogic.com")
public class SearchEpg implements Serializable {

    @JacksonXmlElementWrapper(localName = "channels_ids")
    @JacksonXmlProperty(localName = "channel_id")
    private List<String> channelIds;
    @JsonProperty("end_time")
    private long endTime;
    @JsonProperty("epg_short")
    private boolean epgShort;
    @JsonProperty("keywords")
    private String keyword;
    @JsonProperty("program_id")
    private String programId;
    @JsonProperty("start_time")
    private long startTime;
    @JsonProperty("genre_mask")
    private long genreMask;

    public SearchEpg(List<String> channelIds, long startTime, long endTime) {
        this.channelIds = channelIds;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public List<String> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<String> channelIds) {
        this.channelIds = channelIds;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isEpgShort() {
        return epgShort;
    }

    public void setEpgShort(boolean epgShort) {
        this.epgShort = epgShort;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getGenreMask() {
        return genreMask;
    }

    public void setGenreMask(long genreMask) {
        this.genreMask = genreMask;
    }

    @Override
    public String toString() {
        return "SearchEpg{" +
                "channelIds=" + channelIds +
                ", endTime=" + endTime +
                ", epgShort=" + epgShort +
                ", keyword='" + keyword + '\'' +
                ", programId='" + programId + '\'' +
                ", startTime=" + startTime +
                ", genreMask=" + genreMask +
                '}';
    }
}
