package io.github.johnjcool.dvblink.live.tv.remote.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.io.Serializable;
import java.util.List;

public class Channel implements Serializable {

    @JsonProperty("channel_child_lock")
    private boolean channelChildLock;
    @JsonProperty("channel_logo")
    private String channelLogo;
    @JsonProperty("channel_dvblink_id")
    private int dvbLinkId;
    @JsonProperty("channel_encrypted")
    private int encrypted = 0;
    @JsonProperty("channel_id")
    private String id;
    @JsonProperty("channel_name")
    private String name;
    @JsonProperty("channel_number")
    private int number;
    @JsonProperty("channel_subnumber")
    private int subnumber;
    @JsonProperty("channel_type")
    private Type type;

    public boolean isChannelChildLock() {
        return channelChildLock;
    }

    public void setChannelChildLock(boolean channelChildLock) {
        this.channelChildLock = channelChildLock;
    }

    public String getChannelLogo() {
        return channelLogo;
    }

    public void setChannelLogo(String channelLogo) {
        this.channelLogo = channelLogo;
    }

    public int getDvbLinkId() {
        return dvbLinkId;
    }

    public void setDvbLinkId(int dvbLinkId) {
        this.dvbLinkId = dvbLinkId;
    }

    public int getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(int encrypted) {
        this.encrypted = encrypted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSubnumber() {
        return subnumber;
    }

    public void setSubnumber(int subnumber) {
        this.subnumber = subnumber;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelChildLock=" + channelChildLock +
                ", channelLogo='" + channelLogo + '\'' +
                ", dvbLinkId='" + dvbLinkId + '\'' +
                ", encrypted=" + encrypted +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", number=" + number +
                ", subnumber=" + subnumber +
                ", type=" + type +
                '}';
    }

    public enum Type {

        RD_CHANNEL_TV(0),
        RD_CHANNEL_RADIO(1),
        RD_CHANNEL_OTHER(2);

        private int type;

        Type(int type) {
            this.type = type;
        }
    }

    public static class Channels implements Serializable {

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
            return "Channels{" +
                    "channels=" + channels +
                    '}';
        }
    }
}