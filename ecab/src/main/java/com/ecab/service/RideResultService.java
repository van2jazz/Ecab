package com.ecab.service;

import com.ecab.model.RideResult;
import com.ecab.repository.RideResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class RideResultService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private RideResultRepository rideResultRepository;

    public void saveResult(RideResult result) {
        mongoTemplate.save(result);
    }

    @Cacheable(value = "rideResults", key = "#id")
    public RideResult getRideResult(@PathVariable String id) {
        return rideResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ride result not found"));
    }
}
