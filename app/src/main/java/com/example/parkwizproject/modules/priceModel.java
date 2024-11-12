package com.example.parkwizproject.modules;

public class priceModel {
    private double firstHour;
    private double additionalHour;

    public priceModel() {
    }

    public priceModel(double firstHour, double additionalHour) {
        this.firstHour = firstHour;
        this.additionalHour = additionalHour;
    }

    public double getFirstHour() {
        return firstHour;
    }

    public void setFirstHour(double firstHour) {
        this.firstHour = firstHour;
    }

    public double getAdditionalHour() {
        return additionalHour;
    }

    public void setAdditionalHour(double additionalHour) {
        this.additionalHour = additionalHour;
    }
}


