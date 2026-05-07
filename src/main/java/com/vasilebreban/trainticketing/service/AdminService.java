package com.vasilebreban.trainticketing.service;

import com.vasilebreban.trainticketing.dto.request.DelayRequest;
import com.vasilebreban.trainticketing.exception.ResourceNotFoundException;
import com.vasilebreban.trainticketing.model.Booking;
import com.vasilebreban.trainticketing.model.Train;
import com.vasilebreban.trainticketing.repository.BookingRepository;
import com.vasilebreban.trainticketing.repository.TrainRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TrainRepository trainRepository;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    public List<Booking> getBookingsForTrain(Long trainId) {
        if (!trainRepository.existsById(trainId)) {
            throw new ResourceNotFoundException("Train not found with id: " + trainId);
        }

        return bookingRepository.findByTrain_Id(trainId);
    }

    @Transactional
    public Train markTrainAsDelayed(Long trainId, DelayRequest request) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + trainId));

        train.setDelayMinutes(request.getDelayMinutes());

        Train savedTrain = trainRepository.save(train);

        List<Booking> affectedBookings = bookingRepository.findByTrain_Id(trainId);

        for (Booking booking : affectedBookings) {
            emailService.sendDelayNotification(booking, savedTrain);
        }

        return savedTrain;
    }
}