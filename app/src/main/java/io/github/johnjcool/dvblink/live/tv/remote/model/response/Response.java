package io.github.johnjcool.dvblink.live.tv.remote.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {

    @JsonProperty("xml_result")
    private String xmlResult;

    @JsonProperty("status_code")
    private StatusCode statusCode;

    public void setXmlResult(String xmlResult) {
        this.xmlResult = xmlResult;
    }

    public String getXmlResult() {
        return xmlResult;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return "Response{" +
                "xmlResult='" + xmlResult + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
