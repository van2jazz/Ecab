package com.ecab.repository;

import com.ecab.model.RideResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideResultRepository extends MongoRepository<RideResult, String> {
}
