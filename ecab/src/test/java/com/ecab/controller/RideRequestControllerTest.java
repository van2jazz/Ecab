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

//    @InjectMocks
//    private RideRequestController rideRequestController;
//
//    @Mock
//    private DispatchService dispatchService;
//
//    @Mock
//    private RabbitTemplate rabbitTemplate;
//    @Mock
//    private RideResultService rideResultService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
////    @Test
////    void testRequestRide() {
////        RideRequest rideRequest = new RideRequest("passenger1", 40.730610, -73.935242, null);
////        Map<String, String> response = rideRequestController.requestRide(rideRequest);
////
////        verify(rabbitTemplate).convertAndSend("rideRequests", rideRequest);
////        assertEquals("Ride request submitted!", response.get("message"));
////        assertEquals("passenger1", response.get("passengerId"));
////    }
//
//
//
//    @Test
//    void testGetRideResult() {
//        RideResult rideResult = new RideResult("passenger1", null, 40.730610, -73.935242);
//        when(dispatchService.getRideResult("passenger1")).thenReturn(rideResult);
//
//        RideResult result = rideRequestController.getRideResult("passenger1");
//
//        assertEquals(rideResult, result);
//        verify(dispatchService).getRideResult("passenger1");
//    }

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

        // Define behavior for the RabbitTemplate
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), any(RideRequest.class));

        // Convert RideRequest to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String rideRequestJson = objectMapper.writeValueAsString(rideRequest);

        // Perform the POST request and verify response
        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideRequestJson))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Ride request submitted successfully."))
                .andExpect(jsonPath("$.passengerId").value("Passenger123"));
    }

    @Test
    void testRequestRideFailure() throws Exception {
        // Create a mock RideRequest
        RideRequest rideRequest = new RideRequest();
        rideRequest.setPassengerId("Passenger123");

        // Simulate an exception being thrown by RabbitTemplate
        doThrow(new AmqpException("Error")).when(rabbitTemplate).convertAndSend(anyString(), any(RideRequest.class));

        // Convert RideRequest to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String rideRequestJson = objectMapper.writeValueAsString(rideRequest);

        // Perform the POST request and verify failure response
        mockMvc.perform(post("/api/rides")
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

        // Create a mock RideResult
        RideResult rideResult = new RideResult();
        rideResult.setPassengerId("Passenger123");
        rideResult.setPickupLatitude(40.7128);
        rideResult.setPickupLongitude(-74.0060);
        rideResult.setNearestDriver(driver);

        // Define behavior for dispatchService
        when(dispatchService.getRideResult("12345")).thenReturn(rideResult);

        // Convert RideResult to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String rideResultJson = objectMapper.writeValueAsString(rideResult);

        // Perform the GET request and verify response
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
    void testGetRideResultNotFound() throws Exception {
        // Define behavior for dispatchService
        when(dispatchService.getRideResult("Passenger234")).thenReturn(null);

        // Perform the GET request and verify response
        mockMvc.perform(get("/api/rides/Passenger234")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ride result not found for ID: Passenger234"));
    }

    @Test
    void testGetRideResultError() throws Exception {
        // Define behavior for dispatchService to throw an exception
        when(dispatchService.getRideResult("error")).thenThrow(new RuntimeException("Unexpected error"));

        // Perform the GET request and verify response
        mockMvc.perform(get("/api/rides/error")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An error occurred while processing the request."));
    }

}
