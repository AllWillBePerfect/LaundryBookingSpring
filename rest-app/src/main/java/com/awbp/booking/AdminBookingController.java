package com.awbp.booking;

import com.awbp.booking.application.BookingService;
import com.awbp.booking.command.ReviewBookingCommand;
import com.awbp.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<BookingResponse>> all() {
        var bookings = bookingService.getAllBookings()
                .stream().map(BookingResponse::from).toList();
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<BookingResponse> approve(
            @AuthenticationPrincipal AuthenticatedUser admin,
            @PathVariable Long id
    ) {
        var booking = bookingService.approveBooking(new ReviewBookingCommand(id, admin.userId()));
        return ResponseEntity.ok(BookingResponse.from(booking));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<BookingResponse> reject(
            @AuthenticationPrincipal AuthenticatedUser admin,
            @PathVariable Long id
    ) {
        var booking = bookingService.rejectBooking(new ReviewBookingCommand(id, admin.userId()));
        return ResponseEntity.ok(BookingResponse.from(booking));
    }
}
