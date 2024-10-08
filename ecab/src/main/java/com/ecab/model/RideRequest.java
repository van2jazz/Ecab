package com.ecab.model;


import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class RideRequest implements Serializable {

    @Id
    private String passengerId;
    private double latitude;
    private double longitude;
    private Driver nearestDriver;

    public RideRequest() {
    }

    public RideRequest(String passengerId, double latitude, double longitude, Driver nearestDriver) {
        this.passengerId = passengerId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nearestDriver = nearestDriver;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
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

    public Driver getNearestDriver() {
        return nearestDriver;
    }

    public void setNearestDriver(Driver nearestDriver) {
        this.nearestDriver = nearestDriver;
    }


}
