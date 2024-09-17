//package com.ecab.service;
//
//import com.ecab.model.RideResult;
//import com.ecab.repository.RideResultRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.PathVariable;
//
//@Service
//public class RideResultService {
//
//    private static final Logger logger = LoggerFactory.getLogger(RideResultService.class);
//
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    private RideResultRepository rideResultRepository;
//
//    public void saveResult(RideResult result) {
//        mongoTemplate.save(result);
//    }
//    @Cacheable(value = "rideResults", key = "#id")
//    public RideResult getRideResult(@PathVariable String id) {
//        return rideResultRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Ride result not found"));
//    }
//}
