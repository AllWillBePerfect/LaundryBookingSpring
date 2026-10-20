package com.awbp.service.booking;

import com.awbp.AbstractE2ETest;
import com.awbp.scheduling.application.MachineRepository;
import com.awbp.scheduling.domain.Machine;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingE2ETest extends AbstractE2ETest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MachineRepository machineRepository;

    private Long machineId;

    @BeforeEach
    void createMachine() {
        machineId = machineRepository.save(
                new Machine(null, "Washer", "e2e", true, Instant.now())).id();
    }

    @Test
    void userBooksMachineAndSeesItInHisBookings() throws Exception {
        String token = registerUser("alice");

        book(token, tomorrowAt(10))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.machineId").value(machineId.intValue()))
                .andExpect(jsonPath("$.status").value("CREATED"));

        mockMvc.perform(get("/api/v1/bookings/my")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].machineId").value(machineId.intValue()));
    }

    @Test
    void secondBookingOnSameSlotIsRejected() throws Exception {
        String token = registerUser("bob");
        LocalDateTime slot = tomorrowAt(12);

        book(token, slot).andExpect(status().isCreated());
        book(token, slot).andExpect(status().isConflict());
    }

    private ResultActions book(String token, LocalDateTime start) throws Exception {
        String body = """
                {
                  "machineId": %d,
                  "startTime": "%s"
                }
                """.formatted(machineId, start);

        return mockMvc.perform(post("/api/v1/bookings")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(body));
    }

    private String registerUser(String username) throws Exception {
        String body = """
                {
                  "username": "%s",
                  "displayName": "%s",
                  "password": "password123"
                }
                """.formatted(username, username);

        String response = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(response, "$.accessToken");
    }

    private LocalDateTime tomorrowAt(int hour) {
        return LocalDateTime.now().plusDays(1).withHour(hour).withMinute(0).withSecond(0).withNano(0);
    }
}
