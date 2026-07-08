package com.awbp.booking;

import com.awbp.booking.application.BookingService;
import com.awbp.booking.domain.Booking;
import com.awbp.booking.domain.BookingStatus;
import com.awbp.config.SecurityConfig;
import com.awbp.security.AuthenticatedUser;
import com.awbp.security.JwtTokenProvider;
import com.awbp.user.UserRole;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBookingController.class)
@Import(SecurityConfig.class)
class AdminBookingControllerTest {

    private static final Long ADMIN_ID = 7L;
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
    void givenAdmin_whenGetAllBookings_thenOk() throws Exception {
        when(bookingService.getAllBookings()).thenReturn(List.of(booking(BookingStatus.CREATED)));

        mockMvc.perform(get("/api/v1/admin/bookings").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void givenAdmin_whenApprove_thenOk() throws Exception {
        when(bookingService.approveBooking(any())).thenReturn(booking(BookingStatus.APPROVED));

        mockMvc.perform(post("/api/v1/admin/bookings/{id}/approve", BOOKING_ID).with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void givenAdmin_whenReject_thenOk() throws Exception {
        when(bookingService.rejectBooking(any())).thenReturn(booking(BookingStatus.REJECTED));

        mockMvc.perform(post("/api/v1/admin/bookings/{id}/reject", BOOKING_ID).with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void givenRegularUser_whenGetAllBookings_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/bookings").with(regularUser()))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenNoAuth_whenGetAllBookings_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/bookings"))
                .andExpect(status().isForbidden());
    }

    private Booking booking(BookingStatus status) {
        LocalDateTime start = LocalDateTime.of(2026, 8, 1, 10, 0);
        return new Booking(BOOKING_ID, USER_ID, MACHINE_ID, start, start.plusMinutes(90),
                status, NOW, NOW, null, null);
    }

    private RequestPostProcessor admin() {
        var principal = new AuthenticatedUser(ADMIN_ID, UserRole.ADMIN);
        var authentication = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ADMIN")));
        return authentication(authentication);
    }

    private RequestPostProcessor regularUser() {
        var principal = new AuthenticatedUser(USER_ID, UserRole.USER);
        var authentication = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("USER")));
        return authentication(authentication);
    }
}
