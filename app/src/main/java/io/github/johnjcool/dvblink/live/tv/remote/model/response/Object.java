package io.github.johnjcool.dvblink.live.tv.remote.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.io.Serializable;
import java.util.List;

public class Object implements Serializable {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("containers")
    private List<Container> containers;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("items")
    private List<RecordedTV> recordedTVs;

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
}