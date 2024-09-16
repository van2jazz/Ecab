package com.ecab;

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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DispatchTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private RideResultRepository rideResultRepository;

    @InjectMocks
    private DispatchService dispatchService;

    @BeforeEach
    void setUp() {
        // Initialize mocks and inject them into the service
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindNearestDriver() {
        // Mocking the expected behavior of MongoTemplate
        Driver driver = new Driver("driver1", new GeoJsonPoint(-73.935242, 40.730610), true);
//        when(mongoTemplate.find(any(Query.class), eq(Driver.class)))
//                .thenReturn(Collections.singletonList(driver));

        // Call the method under test
        Driver nearestDriver = dispatchService.findNearestDriver(40.730610, -73.935242);
        when(mongoTemplate.find(any(Query.class), eq(Driver.class)))
                .thenReturn(Collections.singletonList(driver));

        // Verify that the MongoTemplate was used and results are as expected
//        assertNotNull(nearestDriver);
        assertNotNull(nearestDriver);
        assertEquals("driver1", nearestDriver.getDriverId());
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Driver.class));
    }

    @Test
    void testSaveRideResult() {
        // Given a ride request and a driver
        RideRequest rideRequest = new RideRequest("passenger1", 40.7128, -74.0060, null);
        Driver driver = new Driver("driver1", new GeoJsonPoint(-73.935242, 40.730610), true);

        // Mocking repository save method
        RideResult savedRideResult = new RideResult("passenger1", driver, 40.7128, -74.0060);
        when(rideResultRepository.save(any(RideResult.class))).thenReturn(savedRideResult);

        // Call the method under test
        RideResult rideResult = dispatchService.saveRideResult(rideRequest, driver);

        // Verify that the ride result was saved correctly
        assertNotNull(rideResult);
        assertEquals("passenger1", rideResult.getPassengerId());
        assertEquals(driver, rideResult.getNearestDriver());
        verify(rideResultRepository, times(1)).save(any(RideResult.class));
    }
}
