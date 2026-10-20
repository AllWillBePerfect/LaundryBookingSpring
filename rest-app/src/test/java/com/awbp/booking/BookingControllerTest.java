package com.awbp.booking;

import com.awbp.booking.application.BookingService;
import com.awbp.booking.domain.Booking;
import com.awbp.booking.domain.BookingStatus;
import com.awbp.config.SecurityConfig;
import com.awbp.booking.application.AccessDeniedToBookingException;
import com.awbp.booking.application.BookingNotFoundException;
import com.awbp.booking.application.SlotAlreadyBookedException;
import com.awbp.identity.security.AuthenticatedUser;
import com.awbp.identity.security.JwtTokenProvider;
import com.awbp.identity.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@Import(SecurityConfig.class)
class BookingControllerTest {

    private static final Long USER_ID = 1L;
    private static final Long MACHINE_ID = 10L;
    private static final Long BOOKING_ID = 100L;
    private static final Instant NOW = Instant.parse("2026-01-01T09:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void givenValidRequest_whenCreateBooking_thenCreated() throws Exception {
        when(bookingService.createBooking(any())).thenReturn(booking(BookingStatus.CREATED));

        mockMvc.perform(post("/api/v1/bookings")
                        .with(user())
                        .contentType(APPLICATION_JSON)
                        .content(createRequest()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.machineId").value(MACHINE_ID.intValue()))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void givenMissingMachineId_whenCreateBooking_thenBadRequest() throws Exception {
        String body = """
                {
                  "startTime": "%s"
                }
                """.formatted(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/v1/bookings")
                        .with(user())
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenTakenSlot_whenCreateBooking_thenConflict() throws Exception {
        when(bookingService.createBooking(any())).thenThrow(new SlotAlreadyBookedException());

        mockMvc.perform(post("/api/v1/bookings")
                        .with(user())
                        .contentType(APPLICATION_JSON)
                        .content(createRequest()))
                .andExpect(status().isConflict());
    }

    @Test
    void givenNoAuth_whenCreateBooking_thenForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(APPLICATION_JSON)
                        .content(createRequest()))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenBookings_whenGetMyBookings_thenOk() throws Exception {
        when(bookingService.getUserBookings(USER_ID)).thenReturn(List.of(booking(BookingStatus.CREATED)));

        mockMvc.perform(get("/api/v1/bookings/my").with(user()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].machineId").value(MACHINE_ID.intValue()));
    }

    @Test
    void givenOwnBooking_whenCancel_thenOk() throws Exception {
        when(bookingService.cancelBooking(any())).thenReturn(booking(BookingStatus.CANCELLED));

        mockMvc.perform(post("/api/v1/bookings/{id}/cancel", BOOKING_ID).with(user()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void givenMissingBooking_whenCancel_thenNotFound() throws Exception {
        when(bookingService.cancelBooking(any())).thenThrow(new BookingNotFoundException(BOOKING_ID));

        mockMvc.perform(post("/api/v1/bookings/{id}/cancel", BOOKING_ID).with(user()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenForeignBooking_whenGetById_thenForbidden() throws Exception {
        when(bookingService.getBooking(any(), any())).thenThrow(new AccessDeniedToBookingException());

        mockMvc.perform(get("/api/v1/bookings/{id}", BOOKING_ID).with(user()))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenOwnBooking_whenPay_thenOk() throws Exception {
        when(bookingService.submitPayment(any()))
                .thenReturn(booking(BookingStatus.WAITING_PAYMENT_CONFIRMATION));

        mockMvc.perform(post("/api/v1/bookings/{id}/pay", BOOKING_ID).with(user()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING_PAYMENT_CONFIRMATION"));
    }

    private String createRequest() {
        return """
                {
                  "machineId": %d,
                  "startTime": "%s"
                }
                """.formatted(MACHINE_ID, LocalDateTime.now().plusDays(1));
    }

    private Booking booking(BookingStatus status) {
        LocalDateTime start = LocalDateTime.of(2026, 8, 1, 10, 0);
        return new Booking(BOOKING_ID, USER_ID, MACHINE_ID, start, start.plusMinutes(90),
                status, NOW, NOW, null, null);
    }

    private RequestPostProcessor user() {
        var principal = new AuthenticatedUser(USER_ID, UserRole.USER);
        var authentication = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("USER")));
        return authentication(authentication);
    }
}
