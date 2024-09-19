
package com.ecab.service;

import com.ecab.model.Driver;
import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.ecab.repository.RideResultRepository;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

@Service
@Slf4j
public class DispatchService {

    private final MongoTemplate mongoTemplate;
    private final RideResultRepository rideResultRepository;
    private static final double MAX_DISTANCE_IN_RADIANS = 10 / 6378.1;

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

    @Cacheable(value = "nearestDriverCache", key = "#lat + ',' + #lon")
    public Driver findNearestDriver(double lat, double lon) {
        GeoJsonPoint passengerLocation = new GeoJsonPoint(lon, lat);
        Query query = new Query()
                .addCriteria(Criteria
                        .where("location").nearSphere(passengerLocation)
                        .maxDistance(MAX_DISTANCE_IN_RADIANS))
                .addCriteria(Criteria.where("available").is(true));

        log.info("Executing query to find nearest driver: {}", query);

        try {
            System.out.println("enter the try block");
            List<Driver> drivers = mongoTemplate.find(query, Driver.class);

            if (drivers.isEmpty()) {
                log.info("No drivers found.");
                return null;
            } else {
                drivers.forEach(driver -> log.info("Driver found: {}", driver.getDriverId()));
                return drivers.get(0);
            }
        } catch (Exception e) {
            log.error("Error finding nearest driver: {}", e.getMessage(), e);
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
            System.out.println("Calling safe endpoint");
            return rideResultRepository.save(rideResult);
        } catch (Exception e) {
            log.error("Error saving RideResult: {}", e.getMessage(), e);
            return null;
        }
    }

    public RideResult getRideResult(String passengerId) {
        try {
            return rideResultRepository.findById(passengerId)
                    .orElseThrow(() -> new RuntimeException("Ride result not found"));
        } catch (Exception e) {
            log.error("Error fetching ride result for passenger {}: {}", passengerId, e.getMessage(), e);
            return null;
        }
    }
}


