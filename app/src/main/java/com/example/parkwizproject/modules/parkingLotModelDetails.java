package com.example.parkwizproject.modules;

public class parkingLotModelDetails extends parkingLotModel {
    private String id;
    private boolean isOpen;
    private boolean isFull;

    public parkingLotModelDetails() {
    }

    public parkingLotModelDetails(String id, boolean isOpen, boolean isFull) {
        this.id = id;
        this.isOpen = isOpen;
        this.isFull = isFull;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }
}
