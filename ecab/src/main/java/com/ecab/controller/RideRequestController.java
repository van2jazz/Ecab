package com.ecab.controller;

import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import com.ecab.service.DispatchService;
import com.ecab.service.RideResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RideResultService rideResultService;

    @PostMapping
    public Map<String, String> requestRide(@RequestBody RideRequest rideRequest) {
        // Publish ride request to RabbitMQ
        rabbitTemplate.convertAndSend("rideRequests", rideRequest);

        // Return the passengerId as a reference to fetch the result later
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ride request submitted!");
        response.put("passengerId", rideRequest.getPassengerId());
        return response;
    }

    @GetMapping("/{id}")
    public RideResult getRideResult(@PathVariable String id) {
        return dispatchService.getRideResult(id);
    }
}
