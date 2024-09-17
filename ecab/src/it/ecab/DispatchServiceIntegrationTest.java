package ecab;

import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import com.ecab.repository.DriverRepository;
import com.ecab.repository.RideResultRepository;
import com.ecab.service.DispatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.verify;

@SpringBootTest
public class DispatchServiceIntegrationTest {

    @InjectMocks
    private DispatchService dispatchService;

    @Mock
    private RideResultRepository rideResultRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RideRequest rideRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rideRequest = new RideRequest("passenger1", 40.730610, -73.935242, null);
    }

    @Test
    public void testProcessRideRequest() {
        dispatchService.receiveRideRequest(rideRequest);
//        dispatchService.processRideRequest(rideRequest);
        verify(rabbitTemplate).convertAndSend("rideQueue", rideRequest);
    }
}
