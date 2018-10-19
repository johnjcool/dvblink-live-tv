package io.github.johnjcool.dvblink.live.tv.remote.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.johnjcool.dvblink.live.tv.remote.model.request.ItemType;

/**
 * <container>
 * <object_id/> - string mandatory
 * <parent_id/> - string mandatory
 * <name/> - string mandatory
 * <description/> - string optional
 * <logo/> - string optional
 * <container_type/> - int mandatory (CONTAINER_UNKNOWN = -1,CONTAINER_SOURCE =
 * 0, CONTAINER_TYPE = 1, CONTAINER_CATEGORY = 2, CONTAINER_GROUP = 3)
 * <content_type/> - int mandatory, defines type of items in this container (ITEM_UNKNOWN =
 * -1, ITEM_RECORDED_TV = 0, ITEM_VIDEO = 1, ITEM_AUDIO = 2, ITEM_IMAGE = 3)
 * <total_count/> - int optional
 * <source_id/> - string optional, identifies a physical source of this container (8F94B459-
 * EFC0-4D91-9B29-EC3D72E92677 – is the built-in dvblink recorder, e.g. Recorded TV items)
 * </container>
 */
public class Container {

    @JsonProperty("object_id")
    private String objectId;
    @JsonProperty("parent_id")
    private String parentId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("logo")
    private String logo;
    @JsonProperty("container_type")
    private ContainerType containerType;
    @JsonProperty("content_type")
    private ItemType contentType;
    @JsonProperty("total_count")
    private int totalCount;
    @JsonProperty("source_id")
    private String sourceId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public ContainerType getContainerType() {
        return containerType;
    }

    public void setContainerType(ContainerType containerType) {
        this.containerType = containerType;
    }

    public ItemType getContentType() {
        return contentType;
    }

    public void setContentType(ItemType contentType) {
        this.contentType = contentType;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public String toString() {
        return "Container{" +
                "objectId='" + objectId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", logo='" + logo + '\'' +
                ", containerType=" + containerType +
                ", contentType=" + contentType +
                ", totalCount=" + totalCount +
                ", sourceId='" + sourceId + '\'' +
                '}';
    }

    public enum ContainerType {
        CONTAINER_UNKNOWN(-1),
        CONTAINER_SOURCE(0),
        CONTAINER_TYPE(1),
        CONTAINER_CATEGORY(2),
        CONTAINER_GROUP(3);

        private int value;

        ContainerType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}