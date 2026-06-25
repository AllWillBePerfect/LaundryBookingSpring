package com.awbp.booking;

import com.example.booking.application.BookingService;
import com.example.booking.command.CreateBookingCommand;
import com.example.common.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<String> start() {
        return ResponseEntity.ok("bookings!!!");
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestBody @Valid CreateBookingRequest request
    ) {
        var booking = bookingService.createBooking(
                new CreateBookingCommand(
                        currentUser.userId(), request.machineId(), request.startTime())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(BookingResponse.from(booking));
    }
}
