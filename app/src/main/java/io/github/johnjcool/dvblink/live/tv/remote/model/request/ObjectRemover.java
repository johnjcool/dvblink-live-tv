package io.github.johnjcool.dvblink.live.tv.remote.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * <?xml version="1.0" encoding="utf-8" ?>
 * <object_remover xmlns="http://www.dvblogic.com">
 * <object_id/> - string mandatory
 * </object_remover>
 */
@JsonRootName(value = "object_remover", namespace = "http://www.dvblogic.com")
public class ObjectRemover {

    @JsonProperty("object_id")
    private String objectId;

    public ObjectRemover(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public String toString() {
        return "ObjectRemover{" +
                "objectId='" + objectId + '\'' +
                '}';
    }
}
