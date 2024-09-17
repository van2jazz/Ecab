package com.ecab.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rideResults")
public class RideResult {

    private static final Logger logger = LoggerFactory.getLogger(RideResult.class);


    @Id
    private String passengerId;
    private Driver nearestDriver;
    private double pickupLatitude;
    private double pickupLongitude;

    public RideResult() {
    }

    public RideResult(String passengerId, Driver nearestDriver, double pickupLatitude, double pickupLongitude) {
        this.passengerId = passengerId;
        this.nearestDriver = nearestDriver;
        this.pickupLatitude = pickupLatitude;
        this.pickupLongitude = pickupLongitude;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public Driver getNearestDriver() {
        return nearestDriver;
    }

    public void setNearestDriver(Driver nearestDriver) {
        this.nearestDriver = nearestDriver;
    }

    public double getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public double getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }
}
