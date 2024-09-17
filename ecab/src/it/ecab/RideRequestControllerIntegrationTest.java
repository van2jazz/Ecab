package com.ecab;

import com.ecab.model.RideRequest;
import com.ecab.model.RideResult;
import com.ecab.repository.DriverRepository;
import com.ecab.repository.RideResultRepository;
import com.ecab.service.DispatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RideRequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private DispatchService dispatchService;

    @MockBean
    private RideResultRepository rideResultRepository;

    @MockBean
    private DriverRepository driverRepository;

    @MockBean
    private MongoTemplate mongoTemplate;

    private RideRequest rideRequest;

    @BeforeEach
    void setUp() {
        rideRequest = new RideRequest("passenger1", 40.730610, -73.935242, null);
    }

    @Test
    public void testRequestRide() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequest)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Ride request submitted successfully."))
                .andExpect(jsonPath("$.passengerId").value("passenger1"));
    }

    @Test
    public void testGetRideResult() throws Exception {
        RideResult rideResult = new RideResult("passenger1", null, 40.730610, -73.935242);

        Mockito.when(dispatchService.getRideResult("passenger1")).thenReturn(rideResult);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/rides/passenger1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengerId").value("passenger1"));
    }
}
