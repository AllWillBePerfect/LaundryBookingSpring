package com.awbp.service.booking;

import com.awbp.booking.machine.application.MachineRepository;
import com.awbp.booking.machine.domain.Machine;
import com.awbp.machine.MachineJpaEntity;
import com.awbp.machine.MachineJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class BookingFlowIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    MockMvc mockMvc;
    @Autowired
    MachineRepository machineRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long machineId;

    @BeforeEach
    void setUp() {
        Machine machine = machineRepository.save(
                new Machine(null, "Test Machine", "IT", true, Instant.now()));
        machineId = machine.id();
    }

    @Test
    void fullFlow_registerThenBookThenListMyBookings() throws Exception {
        // 1. register -> access token
        String registerJson = """
                {"username":"alice","displayName":"Alice","password":"password123"}
                """;
        String registerResponse = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        String accessToken = objectMapper.readTree(registerResponse).get("accessToken").asText();

        // 2. создаём бронь под токеном
        LocalDateTime start = LocalDateTime.now().plusDays(1)
                .withHour(10).withMinute(0).withSecond(0).withNano(0);
        String bookingJson = objectMapper.writeValueAsString(
                Map.of("machineId", machineId, "startTime", start.toString()));

        mockMvc.perform(post("/api/v1/bookings")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.machineId").value(machineId.intValue()))
                .andExpect(jsonPath("$.status").value("CREATED"));

        // 3. свои брони
        mockMvc.perform(get("/api/v1/bookings/my")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].machineId").value(machineId.intValue()));
    }
}
