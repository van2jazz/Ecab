package com.ecab.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ecab.model.Driver;
import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import com.ecab.repository.RideResultRepository;
import com.ecab.service.DispatchService;
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

        // When: Mock the behavior of mongoTemplate
        when(mongoTemplate.find(any(Query.class), eq(Driver.class))).thenReturn(driverList);
        when(mongoTemplate.findOne(any(Query.class), eq(Driver.class))).thenReturn(mockDriver);

        // Act: Call the method
        Driver nearestDriver = dispatchService.findNearestDriver(lat, lon);

        // Then: Verify the results
        assertNotNull(nearestDriver);
        assertEquals("driver1", nearestDriver.getDriverId());

        // Verify that findOne was called exactly once
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Driver.class));
    }

    @Test
    public void testFindNearestDriverNoDriverFound() {
        // Given
        double lat = 40.7128;
        double lon = -74.0060;

        // When: Mock the behavior of mongoTemplate to return no drivers
        when(mongoTemplate.find(any(Query.class), eq(Driver.class))).thenReturn(new ArrayList<>());
        when(mongoTemplate.findOne(any(Query.class), eq(Driver.class))).thenReturn(null);

        // Act: Call the method
        Driver nearestDriver = dispatchService.findNearestDriver(lat, lon);

        // Then: Verify that no driver is found
        assertNull(nearestDriver);

        // Verify that findOne was called exactly once
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Driver.class));
    }

    @Test
    public void testFindNearestDriverException() {
        // Given
        double lat = 40.7128;
        double lon = -74.0060;

        // When: Mock the behavior of mongoTemplate to throw an exception
        when(mongoTemplate.find(any(Query.class), eq(Driver.class))).thenThrow(new RuntimeException("Database error"));

        // Act: Call the method
        Driver nearestDriver = dispatchService.findNearestDriver(lat, lon);

        // Then: Verify that the method returns null in case of exception
        assertNull(nearestDriver);

        // Verify that find was called exactly once
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Driver.class));
    }


    //rechecktest
    @Test
    public void testSaveRideResultSuccess() {
        // Arrange
        Driver nearestDriver = new Driver("driver1", new GeoJsonPoint(-73.935242, 40.730610), true);
        RideRequest rideRequest = new RideRequest("passenger123", 40.7128, -74.0060, nearestDriver);
        RideResult rideResult = new RideResult("passenger123", nearestDriver, 40.7128, -74.0060);

        // Mocking the repository behavior to save the RideResult
        when(rideResultRepository.save(any(RideResult.class))).thenReturn(rideResult);

        // Act
        RideResult result = dispatchService.saveRideResult(rideRequest, nearestDriver);

        // Assert
        assertNotNull(result);
        assertEquals("passenger123", result.getPassengerId());
        verify(rideResultRepository, times(1)).save(any(RideResult.class));
    }

    @Test
    public void testSaveRideResultException() {
        // Arrange
        Driver nearestDriver = new Driver("driver1", new GeoJsonPoint(-73.935242, 40.730610), true);  // Add nearestDriver
        RideRequest rideRequest = new RideRequest("passenger123", 40.7128, -74.0060, nearestDriver);

        // Mocking the repository behavior to throw an exception
        when(rideResultRepository.save(any(RideResult.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        RideResult result = dispatchService.saveRideResult(rideRequest, nearestDriver);  // Pass nearestDriver

        // Assert
        assertNull(result);  // Expecting null in case of exception
        verify(rideResultRepository, times(1)).save(any(RideResult.class));
    }

    @Test
    void testGetRideResultSuccess() {
        // Arrange
        String passengerId = "passenger123";
        RideResult mockRideResult = new RideResult();
        mockRideResult.setPassengerId(passengerId);

        // Mocking the repository behavior to return the RideResult
        when(rideResultRepository.findById(passengerId)).thenReturn(Optional.of(mockRideResult));

        // Act
        RideResult result = dispatchService.getRideResult(passengerId);

        // Assert
        assertNotNull(result);
        assertEquals(passengerId, result.getPassengerId());
        verify(rideResultRepository, times(1)).findById(passengerId);
    }



    @Test
    public void testGetRideResultNotFound() {
        // Arrange
        String passengerId = "passenger123";

        // Mocking the repository behavior to throw an exception
        when(rideResultRepository.findById(passengerId)).thenReturn(Optional.empty());

        // Act
        RideResult result = dispatchService.getRideResult(passengerId);

        // Assert
        assertNull(result);  // Expecting null when the ride result is not found
        verify(rideResultRepository, times(1)).findById(passengerId);
    }

    @Test
    public void testGetRideResultException() {
        // Arrange
        String passengerId = "passenger123";

        // Mocking the repository behavior to throw an exception
        when(rideResultRepository.findById(passengerId)).thenThrow(new RuntimeException("Database error"));

        // Act
        RideResult result = dispatchService.getRideResult(passengerId);

        // Assert
        assertNull(result);  // Expecting null in case of exception
        verify(rideResultRepository, times(1)).findById(passengerId);
    }
}


