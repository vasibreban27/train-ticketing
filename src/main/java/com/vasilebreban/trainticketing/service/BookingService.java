package com.vasilebreban.trainticketing.service;

import com.vasilebreban.trainticketing.dto.request.BookingRequest;
import com.vasilebreban.trainticketing.model.Booking;
import com.vasilebreban.trainticketing.model.Customer;
import com.vasilebreban.trainticketing.model.Train;
import com.vasilebreban.trainticketing.repository.BookingRepository;
import com.vasilebreban.trainticketing.repository.CustomerRepository;
import com.vasilebreban.trainticketing.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TrainRepository trainRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    @Transactional
    public Booking createBooking(BookingRequest booking) {
        Train train = trainRepository.findById(booking.getTrainId()).orElseThrow(() -> new RuntimeException("Train not found with id: " + booking.getTrainId()));

        Integer bookedTickets = bookingRepository.countBookedSeatsByTrainId(train.getId());
        Integer availableSeats = train.getCapacity() - bookedTickets;

        if(booking.getNumberOfTickets() > availableSeats) {
            throw new RuntimeException("Not enough seats availabe : Number of tickets exceeds available seats!");
        }

        Customer customer = customerRepository.findByEmailIgnoreCase(booking.getCustomerEmail())
                .orElseGet(() -> createCustomer(booking));

        Booking newBooking = Booking.builder()
                .train(train)
                .customer(customer)
                .numberOfTickets(booking.getNumberOfTickets())
                .bookingTime(LocalDateTime.now())
                .build();

        Booking savedBooking = bookingRepository.save(newBooking);

        emailService.sendBookingConfirmation(savedBooking);

        return savedBooking;
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    private Customer createCustomer(BookingRequest request) {
        Customer customer = Customer.builder()
                .fullName(request.getCustomerName())
                .email(request.getCustomerEmail())
                .build();

        return customerRepository.save(customer);
    }
}
