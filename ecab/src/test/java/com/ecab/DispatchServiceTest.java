//package com.ecab;
//
////import com.ecab.model.Driver;
////import com.ecab.repository.RideResultRepository;
////import com.ecab.service.DispatchService;
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////import org.mockito.InjectMocks;
////import org.mockito.Mock;
////import org.mockito.Mockito;
////import org.mockito.MockitoAnnotations;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.data.geo.Point;
////import org.springframework.data.mongodb.core.MongoTemplate;
////import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
////import org.springframework.data.mongodb.core.query.Criteria;
////import org.springframework.data.mongodb.core.query.Query;
////
////import java.util.Collections;
////
////import static org.mockito.Mockito.when;
//
//import com.ecab.model.Driver;
//import com.ecab.model.RideRequest;
//import com.ecab.model.RideResult;
//import com.ecab.repository.RideResultRepository;
//import com.ecab.service.DispatchService;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//
//import java.util.Collections;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//
//
//
//
//public class DispatchServiceTest {
//
//    @Autowired
//    DispatchService dispatchService;
//    private MongoTemplate mongoTemplate;
//    private RideResultRepository rideResultRepository;
//
//    @BeforeEach
//    void setUp() {
//        // Mocking dependencies
//        mongoTemplate = Mockito.mock(MongoTemplate.class);
//        rideResultRepository = Mockito.mock(RideResultRepository.class);
//
//        // Injecting mocked dependencies into the service
//        dispatchService = new DispatchService();
//        dispatchService.mongoTemplate = mongoTemplate;
//        dispatchService.ri = rideResultRepository;
//    }
//
//    @Test
//    void testFindNearestDriver() {
//        // Arrange
//        Driver driver = new Driver("driver1", new GeoJsonPoint(-73.935242, 40.730610), true);
//        Query query = new Query().addCriteria(Criteria.where("location").nearSphere(new GeoJsonPoint(-73.935242, 40.730610)))
//                .addCriteria(Criteria.where("available").is(true));
//
//        // Mocking the behavior of MongoTemplate to return a driver
//        when(mongoTemplate.find(any(Query.class), eq(Driver.class)))
//                .thenReturn(Collections.singletonList(driver));
//
//        System.out.println("jejdjdlolfffffhhh" + driver + query);
//        // Act
//        Driver nearestDriver = dispatchService.findNearestDriver(40.730610, -73.935242);
//
//        System.out.println("checkinh*****" + nearestDriver);
//        // Assert
////        assertNotNull(nearestDriver);
//        assertEquals("driver1", nearestDriver.getDriverId());
//        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Driver.class));
//    }
//
//    @Test
//    void testSaveRideResult() {
//        // Arrange
//        RideRequest rideRequest = new RideRequest("passenger1", 40.7128, -74.0060, null);
//        Driver driver = new Driver("driver1", new GeoJsonPoint(-73.935242, 40.730610), true);
//
//        RideResult expectedRideResult = new RideResult("passenger1", driver, 40.7128, -74.0060);
//
//        // Mocking the save operation in the RideResultRepository
//        when(rideResultRepository.save(any(RideResult.class))).thenReturn(expectedRideResult);
//
//        // Act
//        RideResult rideResult = dispatchService.saveRideResult(rideRequest, driver);
//
//        // Assert
//        assertNotNull(rideResult);
//        assertEquals("passenger1", rideResult.getPassengerId());
//        verify(rideResultRepository, times(1)).save(any(RideResult.class));
//    }
//
//    @Test
//    void testGetRideResult() {
//        // Arrange
//        RideResult rideResult = new RideResult("passenger1", null, 40.7128, -74.0060);
//
//        // Mocking the findById method in RideResultRepository
//        when(rideResultRepository.findById("passenger1")).thenReturn(Optional.of(rideResult));
//
//        // Act
//        RideResult result = dispatchService.getRideResult("passenger1");
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("passenger1", result.getPassengerId());
//        verify(rideResultRepository, times(1)).findById("passenger1");
//    }
//}
