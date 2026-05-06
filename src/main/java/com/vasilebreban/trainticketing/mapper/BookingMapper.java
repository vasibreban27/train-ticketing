package com.vasilebreban.trainticketing.mapper;

import com.vasilebreban.trainticketing.dto.response.BookingResponse;
import com.vasilebreban.trainticketing.model.Booking;

public class BookingMapper {
    private BookingMapper() {
    }

    public static BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .train(TrainMapper.toResponse(booking.getTrain()))
                .customer(CustomerMapper.toResponse(booking.getCustomer()))
                .numberOfTickets(booking.getNumberOfTickets())
                .bookingTime(booking.getBookingTime())
                .build();
    }
}
