package com.example.parkwizproject.modules;

import java.util.Date;

public class vehicleParked {
    private long parkedDate;
    private long releaseDate;
    private double price;
    private String parkinglotID;
    private String vehicleID;
    private String status;

    public vehicleParked() {
    }

    public vehicleParked(long parkedDate, long releaseDate, double price, String parkinglotID, String vehicleID, String status) {
        this.parkedDate = parkedDate;
        this.releaseDate = releaseDate;
        this.price = price;
        this.parkinglotID = parkinglotID;
        this.vehicleID = vehicleID;
        this.status = status;
    }

    public long getParkedDate() {
        return parkedDate;
    }

    public void setParkedDate(long parkedDate) {
        this.parkedDate = parkedDate;
    }

    public long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getParkinglotID() {
        return parkinglotID;
    }

    public void setParkinglotID(String parkinglotID) {
        this.parkinglotID = parkinglotID;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
