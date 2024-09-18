package com.ecab.controller;

import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import com.ecab.service.DispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
@Tag(name = "Ride Requests", description = "Operations related to ride requests")public class RideRequestController {

    private final RabbitTemplate rabbitTemplate;
    private final DispatchService dispatchService;
    private final Logger logger = LoggerFactory.getLogger(RideRequestController.class);

    public RideRequestController(RabbitTemplate rabbitTemplate, DispatchService dispatchService) {
        this.rabbitTemplate = rabbitTemplate;
        this.dispatchService = dispatchService;
    }

    @Operation(summary = "Submit a ride request", description = "Submit a new ride request for a passenger")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Ride request submitted successfully"),
            @ApiResponse(responseCode = "500", description = "Error submitting ride request")
    })
    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestRide(
            @Parameter(description = "Ride request details", required = true)
            @Valid @RequestBody RideRequest rideRequest) {
        logger.info("Received ride request for passengerId: {}", rideRequest.getPassengerId());

        try {
            // Publish ride request to RabbitMQ
            sendRideRequest(rideRequest);
            return buildResponse(HttpStatus.ACCEPTED, "Ride request submitted successfully.", rideRequest.getPassengerId());
        } catch (AmqpException e) {
            return handleException(e, "Failed to submit ride request. Please try again.", rideRequest.getPassengerId());
        }
    }

    @Operation(summary = "Get ride result", description = "Fetch the ride result for a given passenger ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ride result found"),
            @ApiResponse(responseCode = "404", description = "Ride result not found"),
            @ApiResponse(responseCode = "500", description = "Error fetching ride result")
    })
    @GetMapping("/{passengerId}")
    public ResponseEntity<?> getRideResult(
            @Parameter(description = "Passenger ID", required = true)
            @PathVariable String passengerId) {
        logger.info("Fetching ride result for passengerId: {}", passengerId);

        try {
            RideResult rideResult = dispatchService.getRideResult(passengerId);

            if (rideResult == null) {
                return buildResponse(HttpStatus.NOT_FOUND, "Ride result not found for ID: " + passengerId);
            }
            logger.info("Ride result for passengerId {} found.", passengerId);
            return ResponseEntity.ok(rideResult);

        } catch (Exception e) {
            return handleException(e, "An error occurred while processing the request.", passengerId);
        }
    }

    private void sendRideRequest(RideRequest rideRequest) throws AmqpException {
        rabbitTemplate.convertAndSend("rideRequests", rideRequest);
    }

    private ResponseEntity<Map<String, String>> buildResponse(HttpStatus status, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    private ResponseEntity<Map<String, String>> buildResponse(HttpStatus status, String message, String passengerId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("passengerId", passengerId);
        return ResponseEntity.status(status).body(response);
    }

    private ResponseEntity<Map<String, String>> handleException(Exception e, String errorMessage, String passengerId) {
        logger.error(errorMessage + " PassengerId: {}", passengerId, e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }
}

