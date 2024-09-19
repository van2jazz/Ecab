package com.ecab.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ecab.model.Driver;
import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import com.ecab.repository.RideResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DispatchServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private RideResultRepository rideResultRepository;

    @InjectMocks
    private DispatchService dispatchService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindNearestDriverReturnsDriver() {
        // Given
        double lat = 40.7128;
        double lon = -74.0060;
        Driver mockDriver = new Driver("driver1", new GeoJsonPoint(-73.935242, 40.730610), true);
        List<Driver> driverList = new ArrayList<>();
        driverList.add(mockDriver);
        when(mongoTemplate.find(any(Query.class), eq(Driver.class))).thenReturn(driverList);
        when(mongoTemplate.findOne(any(Query.class), eq(Driver.class))).thenReturn(mockDriver);
        Driver nearestDriver = dispatchService.findNearestDriver(lat, lon);
        assertNotNull(nearestDriver);
        assertEquals("driver1", nearestDriver.getDriverId());
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Driver.class));
    }

    @Test
    public void testFindNearestDriverNoDriverFound() {
        // Given
        double lat = 40.7128;
        double lon = -74.0060;
        when(mongoTemplate.find(any(Query.class), eq(Driver.class))).thenReturn(new ArrayList<>());
        when(mongoTemplate.findOne(any(Query.class), eq(Driver.class))).thenReturn(null);
        Driver nearestDriver = dispatchService.findNearestDriver(lat, lon);
        assertNull(nearestDriver);
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Driver.class));
    }

    @Test
    public void testFindNearestDriverException() {
        // Given
        double lat = 40.7128;
        double lon = -74.0060;
        when(mongoTemplate.find(any(Query.class), eq(Driver.class))).thenThrow(new RuntimeException("Database error"));
        Driver nearestDriver = dispatchService.findNearestDriver(lat, lon);
        assertNull(nearestDriver);
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Driver.class));
    }

    @Test
    public void testSaveRideResultSuccess() {
        Driver nearestDriver = new Driver("driver1", new GeoJsonPoint(-73.935242, 40.730610), true);
        RideRequest rideRequest = new RideRequest("passenger123", 40.7128, -74.0060, nearestDriver);
        RideResult rideResult = new RideResult("passenger123", nearestDriver, 40.7128, -74.0060);
        when(rideResultRepository.save(any(RideResult.class))).thenReturn(rideResult);
        RideResult result = dispatchService.saveRideResult(rideRequest, nearestDriver);
        assertNotNull(result);
        assertEquals("passenger123", result.getPassengerId());
        verify(rideResultRepository, times(1)).save(any(RideResult.class));
    }

    @Test
    public void testSaveRideResultException() {
        Driver nearestDriver = new Driver("driver1", new GeoJsonPoint(-73.935242, 40.730610), true);  // Add nearestDriver
        RideRequest rideRequest = new RideRequest("passenger123", 40.7128, -74.0060, nearestDriver);
        when(rideResultRepository.save(any(RideResult.class))).thenThrow(new RuntimeException("Database error"));
        RideResult result = dispatchService.saveRideResult(rideRequest, nearestDriver);  // Pass nearestDriver
        assertNull(result);  // Expecting null in case of exception
        verify(rideResultRepository, times(1)).save(any(RideResult.class));
    }

    @Test
    void testGetRideResultSuccess() {
        String passengerId = "passenger123";
        RideResult mockRideResult = new RideResult();
        mockRideResult.setPassengerId(passengerId);
        when(rideResultRepository.findById(passengerId)).thenReturn(Optional.of(mockRideResult));
        RideResult result = dispatchService.getRideResult(passengerId);
        assertNotNull(result);
        assertEquals(passengerId, result.getPassengerId());
        verify(rideResultRepository, times(1)).findById(passengerId);
    }

    @Test
    public void testGetRideResultNotFound() {
        String passengerId = "passenger123";
        when(rideResultRepository.findById(passengerId)).thenReturn(Optional.empty());
        RideResult result = dispatchService.getRideResult(passengerId);
        assertNull(result);
        verify(rideResultRepository, times(1)).findById(passengerId);
    }

    @Test
    public void testGetRideResultException() {
        String passengerId = "passenger123";
        when(rideResultRepository.findById(passengerId)).thenThrow(new RuntimeException("Database error"));
        RideResult result = dispatchService.getRideResult(passengerId);
        assertNull(result);
        verify(rideResultRepository, times(1)).findById(passengerId);
    }
}


