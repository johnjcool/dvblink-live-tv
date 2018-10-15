package io.github.johnjcool.dvblink.live.tv.remote.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;
import java.util.List;

public class Object implements Serializable {

    @JacksonXmlElementWrapper(localName = "containers")
    @JacksonXmlProperty(localName = "container")
    private List<Container> containers;

    @JacksonXmlElementWrapper(localName = "items")
    @JacksonXmlProperty(localName = "recorded_tv")
    private List<RecordedTV> recordedTVs;

    @JsonProperty("actual_count")
    private int actualCount;

    @JsonProperty("total_count")
    private int totalCount;

    public List<Container> getContainers() {
        return containers;
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
    }

    public List<RecordedTV> getRecordedTVs() {
        return recordedTVs;
    }

    public void setRecordedTVs(List<RecordedTV> recordedTVs) {
        this.recordedTVs = recordedTVs;
    }

    public int getActualCount() {
        return actualCount;
    }

    public void setActualCount(int actualCount) {
        this.actualCount = actualCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "Object{" +
                "containers=" + containers +
                ", recordedTVs=" + recordedTVs +
                ", actualCount=" + actualCount +
                ", totalCount=" + totalCount +
                '}';
    }
}