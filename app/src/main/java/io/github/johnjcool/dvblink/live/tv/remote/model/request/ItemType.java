package io.github.johnjcool.dvblink.live.tv.remote.model.request;

public enum ItemType {
    ITEM_UNKNOWN(-1),
    ITEM_RECORDED_TV(0),
    ITEM_VIDEO(1),
    ITEM_AUDIO(2),
    ITEM_IMAGE(3);

    private int value;

    ItemType(int value) {
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