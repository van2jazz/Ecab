package ecab;

import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import com.ecab.service.DispatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
public class RideRequestControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private DispatchService dispatchService;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRequestRideSuccess() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RideRequest rideRequest = new RideRequest();
        rideRequest.setPassengerId("12345");
        rideRequest.setLongitude(77.5946);
        rideRequest.setLatitude(12.9716);

        Mockito.doNothing().when(rabbitTemplate).convertAndSend(Mockito.anyString(), Mockito.any(RideRequest.class));

        mockMvc.perform(post("/api/rides/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequest)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Ride request submitted successfully."))
                .andExpect(jsonPath("$.passengerId").value("12345"));
    }

    @Test
    public void testRequestRideFailure() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        RideRequest rideRequest = new RideRequest();
        rideRequest.setPassengerId("12345");

        Mockito.doThrow(new AmqpException("RabbitMQ error")).when(rabbitTemplate).convertAndSend(Mockito.anyString(), Mockito.any(RideRequest.class));

        mockMvc.perform(post("/api/rides/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to submit ride request. Please try again."));
    }


    @Test
    public void testGetRideResultSuccess() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RideResult rideResult = new RideResult();
        rideResult.setPassengerId("12345");
        rideResult.setPickupLongitude(77.5946);
        rideResult.setPickupLatitude(12.9716);

        Mockito.when(dispatchService.getRideResult("12345")).thenReturn(rideResult);

        mockMvc.perform(get("/api/rides/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengerId").value("12345"));
    }

}
