package com.ecab;

import com.ecab.model.Driver;
import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import com.ecab.repository.RideResultRepository;
import com.ecab.service.DispatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DispatchTest {
    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private DispatchService dispatchService;

    @Mock
    private RideResultRepository rideResultRepository;

    @InjectMocks
    private DispatchService driverService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindNearestDriver_DriverFound() {
        // Arrange
        double lat = 37.7749;
        double lon = -122.4194;

        Driver mockDriver = new Driver();
        mockDriver.setDriverId("123");

        List<Driver> drivers = Arrays.asList(mockDriver);

        // Mocking the MongoTemplate behavior
        when(mongoTemplate.find(any(Query.class), eq(Driver.class))).thenReturn(drivers);

        // Act
        Driver result = driverService.findNearestDriver(lat, lon);

        // Assert
        assertNotNull(result);
        assertEquals("123", result.getDriverId());
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Driver.class));
    }

    @Test
    void testSaveRideResult_Success() {
        // Arrange
        RideRequest mockRideRequest = new RideRequest();
        mockRideRequest.setPassengerId("passenger123");
        mockRideRequest.setLatitude(37.7749);
        mockRideRequest.setLongitude(-122.4194);

        Driver mockDriver = new Driver();
        mockDriver.setDriverId("driver123");

        RideResult mockRideResult = new RideResult(
                mockRideRequest.getPassengerId(),
                mockDriver,
                mockRideRequest.getLatitude(),
                mockRideRequest.getLongitude()
        );

        // Mock the repository save behavior
        when(rideResultRepository.save(any(RideResult.class))).thenReturn(mockRideResult);

        // Act
        RideResult result = dispatchService.saveRideResult(mockRideRequest, mockDriver);

        // Assert
        assertNotNull(result);
        assertEquals("passenger123", result.getPassengerId());
        assertEquals("driver123", result.getNearestDriver().getDriverId());
        assertEquals(37.7749, result.getPickupLatitude());
        assertEquals(-122.4194, result.getPickupLongitude());
        verify(rideResultRepository, times(1)).save(any(RideResult.class));
    }
}
