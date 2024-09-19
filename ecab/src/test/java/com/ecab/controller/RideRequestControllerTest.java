package com.ecab.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import com.ecab.model.Driver;
import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import com.ecab.service.DispatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RideRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private DispatchService dispatchService;

    @InjectMocks
    private RideRequestController rideRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(rideRequestController).build();
    }

    @Test
    void testRequestRideSuccess() throws Exception {
        // Create a mock RideRequest
        RideRequest rideRequest = new RideRequest();
        rideRequest.setPassengerId("Passenger123");
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), any(RideRequest.class));
        ObjectMapper objectMapper = new ObjectMapper();
        String rideRequestJson = objectMapper.writeValueAsString(rideRequest);
        mockMvc.perform(post("/api/rides/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideRequestJson))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Ride request submitted successfully."))
                .andExpect(jsonPath("$.passengerId").value("Passenger123"));
    }

    @Test
    void testRequestRideFailure() throws Exception {
        RideRequest rideRequest = new RideRequest();
        rideRequest.setPassengerId("Passenger123");
        doThrow(new AmqpException("Error")).when(rabbitTemplate).convertAndSend(anyString(), any(RideRequest.class));
        ObjectMapper objectMapper = new ObjectMapper();
        String rideRequestJson = objectMapper.writeValueAsString(rideRequest);
        mockMvc.perform(post("/api/rides/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideRequestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to submit ride request. Please try again."));
    }

    @Test
    void testGetRideResultSuccess() throws Exception {
        // Create a mock Driver
        Driver driver = new Driver();
        driver.setId("driver1");
        driver.setDriverId("driver123");
        driver.setLocation(new GeoJsonPoint(-74.0060, 40.7128)); // GeoJsonPoint
        driver.setAvailable(true);

        RideResult rideResult = new RideResult();
        rideResult.setPassengerId("Passenger123");
        rideResult.setPickupLatitude(40.7128);
        rideResult.setPickupLongitude(-74.0060);
        rideResult.setNearestDriver(driver);

        when(dispatchService.getRideResult("12345")).thenReturn(rideResult);

        ObjectMapper objectMapper = new ObjectMapper();
        String rideResultJson = objectMapper.writeValueAsString(rideResult);
        mockMvc.perform(get("/api/rides/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengerId").value("Passenger123"))
                .andExpect(jsonPath("$.pickupLatitude").value(40.7128))
                .andExpect(jsonPath("$.pickupLongitude").value(-74.0060))
                .andExpect(jsonPath("$.nearestDriver.id").value("driver1"))
                .andExpect(jsonPath("$.nearestDriver.driverId").value("driver123"))
                .andExpect(jsonPath("$.nearestDriver.location.x").value(-74.0060))
                .andExpect(jsonPath("$.nearestDriver.location.y").value(40.7128))
                .andExpect(jsonPath("$.nearestDriver.available").value(true));
    }

    @Test
    void testGetRideResultError() throws Exception {
        when(dispatchService.getRideResult("error")).thenThrow(new RuntimeException("Unexpected error"));
        mockMvc.perform(get("/api/rides/error")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An error occurred while processing the request."));
    }

}
