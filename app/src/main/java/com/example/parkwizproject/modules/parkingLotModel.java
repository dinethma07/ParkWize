package com.example.parkwizproject.modules;

public class parkingLotModel {

    private String id;
    private String name;
    private String attendant_email;
    private double latitude;
    private double longitude;
    private String city;




    public parkingLotModel() {
    }

    public parkingLotModel(String id, String name, String attendant_email, double latitude, double longitude, String city) {
        this.id = id;
        this.name = name;
        this.attendant_email = attendant_email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttendant_email() {
        return attendant_email;
    }

    public void setAttendant_email(String attendant_email) {
        this.attendant_email = attendant_email;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


}
