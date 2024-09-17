//package ecab;
//
//import com.ecab.model.RideRequest;
//import com.ecab.service.DispatchService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.mockito.Mockito.verify;
//
//@SpringBootTest
//public class RabbitMQListenerTest {
//
//    @InjectMocks
//    private RabbitMQListener rabbitMQListener;
//    //RabbitTemplate
//
//    @Mock
//    private DispatchService dispatchService;
//
//    private RideRequest rideRequest;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        rideRequest = new RideRequest("passenger1", 40.730610, -73.935242, null);
//    }
//
//    @Test
//    public void testReceiveRideRequest() {
//        rabbitMQListener.receiveRideRequest(rideRequest);
//        verify(dispatchService).processRideRequest(rideRequest);
//    }
//}
