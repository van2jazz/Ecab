package com.ecab.config;

import com.ecab.model.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

@Component
public class DriverDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DriverDataInitializer.class);


    @Bean
    public CommandLineRunner init(MongoTemplate mongoTemplate) {
        return args -> {
            mongoTemplate.save(new Driver("driver1", new GeoJsonPoint(-73.935242, 40.730610), true));
            mongoTemplate.save(new Driver("driver2", new GeoJsonPoint(-74.0060, 40.7128), true));
            mongoTemplate.save(new Driver("driver3", new GeoJsonPoint(-73.935242, 40.730614), false));
            mongoTemplate.save(new Driver("driver4", new GeoJsonPoint(-74.0050, 40.7458), false));
            // Add more drivers
        };
    }
}
