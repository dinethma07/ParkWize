package com.example.parkwizproject.modules;

public class userModel {
    private String username;
    private String email;
    private String image;
    private UserType user_type;
    private String user_id;
    private String parkingLot_id;

    public userModel() {
    }

    public userModel(String email, String username, String image, UserType user_type, String user_id, String parkingLot_id) {
        this.email = email;
        this.username = username;
        this.image = image;
        this.user_type = user_type;
        this.user_id = user_id;
        this.parkingLot_id = parkingLot_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public UserType getUser_type() {
        return user_type;
    }

    public void setUser_type(UserType user_type) {
        this.user_type = user_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getParkingLot_id() {
        return parkingLot_id;
    }

    public void setParkingLot_id(String parkingLot_id) {
        this.parkingLot_id = parkingLot_id;
    }
}
