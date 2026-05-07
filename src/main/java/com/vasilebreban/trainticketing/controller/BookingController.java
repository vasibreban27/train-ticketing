package com.vasilebreban.trainticketing.controller;

import com.vasilebreban.trainticketing.dto.request.BookingRequest;
import com.vasilebreban.trainticketing.dto.response.BookingResponse;
import com.vasilebreban.trainticketing.mapper.BookingMapper;
import com.vasilebreban.trainticketing.model.Booking;
import com.vasilebreban.trainticketing.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BookingMapper.toResponse(booking));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);

        return ResponseEntity.ok(BookingMapper.toResponse(booking));
    }
}