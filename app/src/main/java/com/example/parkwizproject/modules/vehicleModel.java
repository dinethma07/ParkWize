package com.example.parkwizproject.modules;

public class vehicleModel {
    private String id;
    private VehicleType type;
    private String vehicleNumber;
    private String nickName;

    public vehicleModel() {
    }

    public vehicleModel(String id, VehicleType type, String vehicleNumber, String nickName) {
        this.id = id;
        this.type = type;
        this.vehicleNumber = vehicleNumber;
        this.nickName = nickName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(String type) {
        this.type = findVehicleType(type);
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private VehicleType findVehicleType(String vt) {
        VehicleType v = VehicleType.Bicycle;
        switch (vt) {
            case "Bicycle":
                v = VehicleType.Bicycle;
                break;
            case "Motorbike":
                v = VehicleType.Motorbike;
                break;
            case "Tuk Tuk":
                v = VehicleType.Tuk_Tuk;
                break;
            case "Bus":
                v = VehicleType.Bus;
                break;
            case "Car":
                v = VehicleType.Car;
                break;
            case "Van":
                v = VehicleType.Van;
                break;
            case "Truck":
                v = VehicleType.Truck;
                break;
        }
        return v;
    }
}
