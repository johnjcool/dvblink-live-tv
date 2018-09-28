package io.github.johnjcool.dvblink.live.tv.remote.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Request xml data
 * < stream_info xmlns:i="http://www.w3.org/2001/XMLSchema-instance"
 * xmlns="http://www.dvblogic.com">
 * <client_id/> string optional - a unique id of the client that requests a stream. Can be any string of
 * alphanumeric characters without spaces as long as it is unique – a guid for example. If empty or not present,
 * client id will be generated from the client’s IP address
 * <channels_dvblink_ids>
 * <channel_dvblink_id/> - long mandatory – DVBLink channel ID
 * …
 * <channel_dvblink_id/>
 * </channels_dvblink_ids>
 * </ stream_info>
 */
@JsonRootName(value = "stream_info", namespace = "http://www.dvblogic.com")
public class StreamInfo implements Serializable {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("server_address")
    private String serverAddress;

    @JacksonXmlElementWrapper(localName = "channels_dvblink_ids")
    @JacksonXmlProperty(localName = "channel_dvblink_id")
    private List<Long> dvbLinkIds;

    public StreamInfo(String serverAddress, String clientId, List<Long> dvbLinkIds) {
        this.clientId = clientId;
        this.dvbLinkIds = dvbLinkIds;
        this.serverAddress = serverAddress;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<Long> getDvbLinkIds() {
        return dvbLinkIds;
    }

    public void setDvbLinkIds(List<Long> dvbLinkIds) {
        this.dvbLinkIds = dvbLinkIds;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public String toString() {
        return "StreamInfo{" +
                "clientId='" + clientId + '\'' +
                ", serverAddress='" + serverAddress + '\'' +
                ", dvbLinkIds=" + dvbLinkIds +
                '}';
    }
}
