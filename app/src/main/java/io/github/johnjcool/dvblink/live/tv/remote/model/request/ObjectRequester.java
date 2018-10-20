package io.github.johnjcool.dvblink.live.tv.remote.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Request xml data
 *<?xml version="1.0" encoding="utf-8" ?>
 * <object_requester xmlns="http://www.dvblogic.com">
 * <objectId/> - string mandatory (empty string – root object)
 * <object_type/> - int optional, requests objects of a certain type – items or containers.
 * (OBJECT_UNKNOWN = -1,OBJECT_CONTAINER = 0,OBJECT_ITEM = 1), by default -
 * OBJECT_UNKNOWN (e.g. all object types)
 * <item_type/> - int optional, requests items of a certain type (ITEM_UNKNOWN = -1,
 * ITEM_RECORDED_TV = 0, ITEM_VIDEO = 1, ITEM_AUDIO = 2, ITEM_IMAGE = 3), by default -
 * ITEM_UNKNOWN (e.g. all item types)
 * <start_position/> - int optional, by default - 0
 * <requested_count/> - int optional, by default “-1” (e.g. all)
 * <children_request/> - bool optional, by default – false (if false – returns information about object itself as
 * specified by its objectId. If true – returns objects’ children objects – containers and items)
 * <server_address/> - string mandatory (ip address or a host name of DVBLink server)
 * </object_requester>
 */
@JsonRootName(value = "object_requester", namespace = "http://www.dvblogic.com")
public class ObjectRequester {

    @JsonProperty("object_id")
    private String objectId;
    @JsonProperty("object_type")
    private ObjectType objectType;
    @JsonProperty("item_type")
    private ItemType itemIype;
    @JsonProperty("start_position")
    private int startPosition;
    @JsonProperty("requested_count")
    private int requestedCount;
    @JsonProperty("children_request")
    private boolean childrenRequest;
    @JsonProperty("server_address")
    private String serverAddress;

    public ObjectRequester(String objectId, String serverAddress) {
        this(objectId, serverAddress, ObjectType.OBJECT_UNKNOWN, ItemType.ITEM_UNKNOWN);
    }

    public ObjectRequester(String objectId, String serverAddress, ObjectType objectType, ItemType itemIype) {
        this.objectId = objectId;
        this.serverAddress = serverAddress;
        this.objectType =objectType;
        this.itemIype = itemIype;

        requestedCount = -1;
        childrenRequest = true;
        startPosition = 0;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public ItemType getItemIype() {
        return itemIype;
    }

    public void setItemIype(ItemType itemIype) {
        this.itemIype = itemIype;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getRequestedCount() {
        return requestedCount;
    }

    public void setRequestedCount(int requestedCount) {
        this.requestedCount = requestedCount;
    }

    public boolean isChildrenRequest() {
        return childrenRequest;
    }

    public void setChildrenRequest(boolean childrenRequest) {
        this.childrenRequest = childrenRequest;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public String toString() {
        return "ObjectRequester{" +
                "objectId='" + objectId + '\'' +
                ", objectType=" + objectType +
                ", itemIype=" + itemIype +
                ", startPosition=" + startPosition +
                ", requestedCount=" + requestedCount +
                ", childrenRequest=" + childrenRequest +
                ", serverAddress='" + serverAddress + '\'' +
                '}';
    }
}
