package io.github.johnjcool.dvblink.live.tv.remote.model.request;

public enum ObjectType {
    OBJECT_UNKNOWN(-1),
    OBJECT_CONTAINER(0),
    OBJECT_ITEM(1);

    private int value;

    ObjectType(int value) {
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