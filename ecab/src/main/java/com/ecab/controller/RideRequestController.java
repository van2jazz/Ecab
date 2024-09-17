package com.ecab.controller;

import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import com.ecab.service.DispatchService;
//import com.ecab.service.RideResultService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
public class RideRequestController {
    private static final Logger logger = LoggerFactory.getLogger(RideRequestController.class);


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DispatchService dispatchService;

//    @Autowired
//    private RideResultService rideResultService;

//    @PostMapping
//    public Map<String, String> requestRide(@RequestBody RideRequest rideRequest) {
//        // Publish ride request to RabbitMQ
//        rabbitTemplate.convertAndSend("rideRequests", rideRequest);
//
//        // Return the passengerId as a reference to fetch the result later
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Ride request submitted!");
//        response.put("passengerId", rideRequest.getPassengerId());
//        return response;
//    }

    //Logger
    @PostMapping
    public ResponseEntity<Map<String, String>> requestRide(@Valid @RequestBody RideRequest rideRequest) {
        Map<String, String> response = new HashMap<>();
        logger.info("Received ride request for passengerId: {}", rideRequest.getPassengerId());

        try {
            // Publish ride request to RabbitMQ
            rabbitTemplate.convertAndSend("rideRequests", rideRequest);

            // Construct the response
            response.put("message", "Ride request submitted successfully.");
            response.put("passengerId", rideRequest.getPassengerId());
            logger.info("Ride request for passengerId {} submitted successfully.", rideRequest.getPassengerId());

            // Return response with 202 ACCEPTED status
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (AmqpException e) {
            logger.error("Failed to submit ride request for passengerId {}: {}", rideRequest.getPassengerId(), e.getMessage(), e);
            // Handle messaging errors (RabbitMQ)
            response.put("message", "Failed to submit ride request. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRideResult(@PathVariable String id) {
        logger.info("Fetching ride result for ID: {}", id);
        try {
            // Fetch the ride result from the dispatch service
            RideResult rideResult = dispatchService.getRideResult(id);

            if (rideResult == null) {
                // Return 404 Not Found if no result is found for the given ID
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Ride result not found for ID: " + id));
            }
            logger.info("Ride result for ID {} found.", id);
            // Return the ride result with 200 OK
            return ResponseEntity.ok(rideResult);

        } catch (Exception e) {
            // Log the exception for debugging purposes
            logger.error("Error fetching ride result for ID: " + id, e);

            // Return 500 Internal Server Error with an error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred while processing the request."));
        }
    }
}
