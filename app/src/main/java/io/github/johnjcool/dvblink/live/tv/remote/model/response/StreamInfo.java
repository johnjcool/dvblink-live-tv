package io.github.johnjcool.dvblink.live.tv.remote.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.io.Serializable;
import java.util.List;

/**
 * <stream_info xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://www.dvblogic.com">
 <channel>
 <channel_dvblink_id/> - long mandatory – DVBLink channel ID
 <url/> - string mandatory – direct streaming url
 </channel>
 …
 <channel>
 </channel>
 </ stream_info>
 */
public class StreamInfo implements Serializable {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("channel")
    private List<Channel> channels;

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public String toString() {
        return "StreamInfo{" +
                "channels=" + channels +
                '}';
    }

    public static class Channel {
        @JsonProperty("channel_dvblink_id")
        private Long dvblinkId;

        @JsonProperty("url")
        private String url;

        public Long getDvblinkId() {
            return dvblinkId;
        }

        public void setDvblinkId(Long dvblinkId) {
            this.dvblinkId = dvblinkId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "Channel{" +
                    "dvblinkId=" + dvblinkId +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
}
