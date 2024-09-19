package com.ecab.config;

import com.ecab.config.DriverDataInitializer;
import com.ecab.model.Driver;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

public class DriverDataInitializerTest {

    @Test
    void testInit() throws Exception {
        MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
        DriverDataInitializer initializer = new DriverDataInitializer();
        CommandLineRunner runner = initializer.init(mongoTemplate);

        runner.run();

        Mockito.verify(mongoTemplate, Mockito.times(4)).save(Mockito.any(Driver.class));
    }
}
