
package com.ecab.service;

import com.ecab.model.Driver;
import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.ecab.repository.RideResultRepository;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Service
public class DispatchService {

    private final MongoTemplate mongoTemplate;
    private final RideResultRepository rideResultRepository;
    private static final double MAX_DISTANCE_IN_RADIANS = 10 / 6378.1;
    private static final Logger logger = LoggerFactory.getLogger(DispatchService.class);

    public DispatchService(MongoTemplate mongoTemplate, RideResultRepository rideResultRepository) {
        this.mongoTemplate = mongoTemplate;
        this.rideResultRepository = rideResultRepository;
    }


    @RabbitListener(queues = "rideRequests")
    public void receiveRideRequest(RideRequest rideRequest) {
        // Find the nearest driver
        Driver nearestDriver = findNearestDriver(rideRequest.getLatitude(), rideRequest.getLongitude());

        // Save the ride result in MongoDB
        saveRideResult(rideRequest, nearestDriver);
    }

    ////    @Cacheable(value = "nearestDriverCache", key = "#lat + ',' + #lon")
    public Driver findNearestDriver(double lat, double lon) {
        GeoJsonPoint passengerLocation = new GeoJsonPoint(lon, lat);

        Query query = new Query()
                .addCriteria(Criteria
                        .where("location").nearSphere(passengerLocation)
                        .maxDistance(MAX_DISTANCE_IN_RADIANS))
                .addCriteria(Criteria.where("available").is(true));

        logger.info("Executing query to find nearest driver: {}", query);

        try {
            List<Driver> drivers = mongoTemplate.find(query, Driver.class);

            if (drivers.isEmpty()) {
                logger.info("No drivers found.");
                return null;
            } else {
                drivers.forEach(driver -> logger.info("Driver found: {}", driver.getDriverId()));
                return drivers.get(0);
            }
        } catch (Exception e) {
            logger.error("Error finding nearest driver: {}", e.getMessage(), e);
            return null;
        }
    }

    public RideResult saveRideResult(RideRequest rideRequest, Driver nearestDriver) {
        RideResult rideResult = new RideResult(
                rideRequest.getPassengerId(),
                nearestDriver,
                rideRequest.getLatitude(),
                rideRequest.getLongitude()
        );

        try {
            return rideResultRepository.save(rideResult);
        } catch (Exception e) {
            logger.error("Error saving RideResult: {}", e.getMessage(), e);
            // Optionally, you might want to rethrow or handle the exception differently
            return null;
        }
    }

    @Cacheable(value = "rideResultCache", key = "#passengerId")
    public RideResult getRideResult(String passengerId) {
        try {
            return rideResultRepository.findById(passengerId)
                    .orElseThrow(() -> new RuntimeException("Ride result not found"));
        } catch (Exception e) {
            logger.error("Error fetching ride result for passenger {}: {}", passengerId, e.getMessage(), e);
            return null;  // Consider throwing a custom exception or handling it as needed
        }
    }
}


