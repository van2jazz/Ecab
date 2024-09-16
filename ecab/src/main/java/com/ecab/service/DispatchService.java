package com.ecab.service;

import com.ecab.model.Driver;
import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import com.ecab.repository.RideResultRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispatchService {

    @Autowired
    RideResultRepository rideResultRepository;

    @Autowired
    public MongoTemplate mongoTemplate;

//    @RabbitListener(queues = "rideRequests")
//    public void receiveRideRequest(RideRequest rideRequest) {
//        // Find the nearest driver (assuming this is done in another method)
//        Driver nearestDriver = findNearestDriver
//                (rideRequest.getLatitude(), rideRequest.getLongitude());
//
//        // Save the ride result
//        saveRideResult(rideRequest, nearestDriver);
//    }

    @RabbitListener(queues = "rideRequests")
    public void receiveRideRequest(RideRequest rideRequest) {
        // Find the nearest driver
        Driver nearestDriver = findNearestDriver(rideRequest.getLatitude(), rideRequest.getLongitude());

        // Save the ride result in MongoDB
        saveRideResult(rideRequest, nearestDriver);
    }

//    public Driver findNearestDriver(double lat, double lon) {
//        // Create a point with passenger location
//        Point passengerLocation = new Point(lat, lon);
//
//        // Create a query to find the nearest driver who is available
//        Query query = new Query();
//        query.addCriteria(Criteria
//                        .where("location").nearSphere(passengerLocation) // NearSphere for geospatial queries
//                        .maxDistance(5 / 6378.1)) // Maximum distance in radians (5 km converted to radians)
//                .addCriteria(Criteria.where("available").is(true)); // Only look for available drivers
//
//        // Fetch the nearest driver using MongoTemplate
//        return mongoTemplate.findOne(query, Driver.class);
//    }

    public Driver findNearestDriver(double lat, double lon) {
        GeoJsonPoint passengerLocation = new GeoJsonPoint(lon, lat);

        Query query = new Query();
        query.addCriteria(Criteria
                        .where("location").nearSphere(passengerLocation)
                        .maxDistance(10 / 6378.1))  // Check different maxDistance values
                .addCriteria(Criteria.where("available").is(true));
        System.out.println(" for logginggg*********************1" + query);

        List<Driver> drivers = mongoTemplate.find(query, Driver.class);

        // Debugging: Print drivers found
//        if (drivers.isEmpty()) {
//            System.out.println("No drivers found.");
//        } else {
//            drivers.forEach(driver -> System.out.println("Driver found: " + driver.getDriverId()));
//        }

        return mongoTemplate.findOne(query, Driver.class);
    }

//    private void saveRideResult(RideRequest rideRequest, Driver nearestDriver) {
//        // Create a RideResult object with the data
//        RideResult rideResult = new RideResult(
//                rideRequest.getPassengerId(),    // Assuming RideRequest has a passengerId field
//                nearestDriver,
//                rideRequest.getLatitude(),
//                rideRequest.getLongitude()
//        );
//
//        // Save the RideResult object to MongoDB
//        rideResultRepository.save(rideResult);
//    }

    public RideResult saveRideResult(RideRequest rideRequest, Driver nearestDriver) {
        RideResult rideResult = new RideResult(
                rideRequest.getPassengerId(),
                nearestDriver,
                rideRequest.getLatitude(),
                rideRequest.getLongitude()
        );
        return rideResultRepository.save(rideResult);
    }

    public RideResult getRideResult(String passengerId) {
        return rideResultRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Ride result not found"));
    }

}
