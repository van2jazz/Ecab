package ecab;

import com.ecab.model.RideResult;
import com.ecab.repository.RideResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
public class RideResultRepositoryIntegrationTest {

    @Autowired
    private RideResultRepository rideResultRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private RideResult rideResult;

    @BeforeEach
    public void setUp() {
        rideResult = new RideResult("passenger1", null, 40.730610, -73.935242);
        mongoTemplate.save(rideResult);
    }

    @Test
    public void testFindByPassengerId() {
        Optional<RideResult> result = rideResultRepository.findById("passenger1");
        assertNotNull(result);
    }
}
