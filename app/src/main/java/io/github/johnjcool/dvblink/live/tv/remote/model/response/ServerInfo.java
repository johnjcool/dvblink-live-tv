package io.github.johnjcool.dvblink.live.tv.remote.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerInfo {

    @JsonProperty("install_id")
    private String installId;
    @JsonProperty("server_id")
    private String serverId;
    @JsonProperty("version")
    private String version;
    @JsonProperty("build")
    private int build;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getBuild() {
        return build;
    }

    public void setBuild(int build) {
        this.build = build;
    }

    public String getInstallId() {
        return installId;
    }

    public void setInstallId(String installId) {
        this.installId = installId;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "installId='" + installId + '\'' +
                ", serverId='" + serverId + '\'' +
                ", version='" + version + '\'' +
                ", build=" + build +
                '}';
    }
}
