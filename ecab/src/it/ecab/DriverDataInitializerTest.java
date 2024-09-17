package ecab;


import com.ecab.EcabApplication;
import com.ecab.config.DriverDataInitializer;
import com.ecab.model.Driver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = EcabApplication.class)
@ExtendWith(MockitoExtension.class)
public class DriverDataInitializerTest {


    @MockBean
    private MongoTemplate mongoTemplate;

    @Autowired
    private DriverDataInitializer driverDataInitializer;

    @Test
    public void shouldInitializeDrivers() throws Exception {
        // Execute the CommandLineRunner bean
        CommandLineRunner runner = driverDataInitializer.init(mongoTemplate);
        runner.run();

        // Verify that the save method was called for each driver
        verify(mongoTemplate, times(1)).save(any(Driver.class));
    }
}
