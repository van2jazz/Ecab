package com.ecab;

import com.ecab.controller.RideRequestController;
import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import com.ecab.service.DispatchService;
import com.ecab.service.RideResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RideRequestControllerTest {

    @InjectMocks
    private RideRequestController rideRequestController;

    @Mock
    private DispatchService dispatchService;

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private RideResultService rideResultService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRequestRide() {
        RideRequest rideRequest = new RideRequest("passenger1", 40.730610, -73.935242, null);
        Map<String, String> response = rideRequestController.requestRide(rideRequest);

        verify(rabbitTemplate).convertAndSend("rideRequests", rideRequest);
        assertEquals("Ride request submitted!", response.get("message"));
        assertEquals("passenger1", response.get("passengerId"));
    }

    @Test
    void testGetRideResult() {
        RideResult rideResult = new RideResult("passenger1", null, 40.730610, -73.935242);
        when(dispatchService.getRideResult("passenger1")).thenReturn(rideResult);

        RideResult result = rideRequestController.getRideResult("passenger1");

        assertEquals(rideResult, result);
        verify(dispatchService).getRideResult("passenger1");
    }

}
