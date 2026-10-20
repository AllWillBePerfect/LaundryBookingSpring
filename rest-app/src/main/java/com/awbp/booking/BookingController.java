package com.awbp.booking;

import com.awbp.booking.application.BookingService;
import com.awbp.booking.command.CancelBookingCommand;
import com.awbp.booking.command.CreateBookingCommand;
import com.awbp.booking.command.SubmitPaymentCommand;
import com.awbp.identity.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

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

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        var bookings = bookingService.getUserBookings(currentUser.userId())
                .stream().map(BookingResponse::from).toList();

        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<BookingResponse> pay(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable Long id
    ) {
        var booking = bookingService.submitPayment(
                new SubmitPaymentCommand(id, currentUser.userId()));
        return ResponseEntity.ok(BookingResponse.from(booking));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancel(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable Long id
    ) {
        var booking = bookingService.cancelBooking(
                new CancelBookingCommand(id, currentUser.userId()));
        return ResponseEntity.ok(BookingResponse.from(booking));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable Long id
    ) {
        var booking = bookingService.getBooking(id, currentUser.userId());
        return ResponseEntity.ok(BookingResponse.from(booking));
    }




}
