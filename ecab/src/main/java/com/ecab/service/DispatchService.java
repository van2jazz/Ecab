package com.ecab.service;

import com.ecab.model.Driver;
import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import com.ecab.repository.RideResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispatchService {

    private static final Logger logger = LoggerFactory.getLogger(DispatchService.class);


    @Autowired
    RideResultRepository rideResultRepository;

    @Autowired
    public MongoTemplate mongoTemplate;

    @RabbitListener(queues = "rideRequests")
    public void receiveRideRequest(RideRequest rideRequest) {
        // Find the nearest driver
        Driver nearestDriver = findNearestDriver(rideRequest.getLatitude(), rideRequest.getLongitude());

        // Save the ride result in MongoDB
        saveRideResult(rideRequest, nearestDriver);
    }


//    public Driver findNearestDriver(double lat, double lon) {
//        GeoJsonPoint passengerLocation = new GeoJsonPoint(lon, lat);
//
//        Query query = new Query();
//        query.addCriteria(Criteria
//                        .where("location").nearSphere(passengerLocation)
//                        .maxDistance(10 / 6378.1))  // Check different maxDistance values
//                .addCriteria(Criteria.where("available").is(true));
//        System.out.println("*********************" + query);
//
//        List<Driver> drivers = mongoTemplate.find(query, Driver.class);
//
//        // Debugging: Print drivers found
//        if (drivers.isEmpty()) {
//            System.out.println("No drivers found.");
//        } else {
//            drivers.forEach(driver -> System.out.println("Driver found: " + driver.getDriverId()));
//        }
//
//        return mongoTemplate.findOne(query, Driver.class);
//    }

    public Driver findNearestDriver(double lat, double lon) {
        try {
            GeoJsonPoint passengerLocation = new GeoJsonPoint(lon, lat);

            Query query = new Query();
            query.addCriteria(Criteria
                            .where("location").nearSphere(passengerLocation)
                            .maxDistance(10 / 6378.1))  // Max distance in radians
                    .addCriteria(Criteria.where("available").is(true));

            logger.info("Executing query to find nearest driver: {}", query);

            List<Driver> drivers = mongoTemplate.find(query, Driver.class);

            if (drivers.isEmpty()) {
                logger.info("No drivers found.");
                return null;
            } else {
                drivers.forEach(driver -> logger.info("Driver found: {}", driver.getDriverId()));
                return drivers.get(0); // Return the first driver found
            }
        } catch (Exception e) {
            logger.error("Error finding nearest driver: {}", e.getMessage(), e);
            return null;  // Return null if any exception occurs
        }
    }



//    public RideResult saveRideResult(RideRequest rideRequest, Driver nearestDriver) {
//        RideResult rideResult = new RideResult(
//                rideRequest.getPassengerId(),
//                nearestDriver,
//                rideRequest.getLatitude(),
//                rideRequest.getLongitude()
//        );
//        return rideResultRepository.save(rideResult);
//    }

    public RideResult saveRideResult(RideRequest rideRequest, Driver nearestDriver) {
        try {
            RideResult rideResult = new RideResult(
                    rideRequest.getPassengerId(),
                    nearestDriver,
                    rideRequest.getLatitude(),
                    rideRequest.getLongitude()
            );
            return rideResultRepository.save(rideResult);
        } catch (Exception e) {
            logger.error("Error saving RideResult: {}", e.getMessage(), e);
            return null;  // Handle error by returning null, or you can throw a custom exception if needed
        }
    }

    //recheck this method //allowed
    public RideResult getRideResult(String passengerId) {
        try {
            return rideResultRepository.findById(passengerId)
                    .orElseThrow(() -> new RuntimeException("Ride result not found"));
        } catch (Exception e) {
            // Log the exception and return null or handle it based on your use case
            logger.error("Error fetching ride result for passenger {}: {}", passengerId, e.getMessage(), e);
            return null;  // Returning null in case of an exception
        }
    }

}
