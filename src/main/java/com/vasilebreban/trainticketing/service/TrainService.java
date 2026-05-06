package com.vasilebreban.trainticketing.service;

import com.vasilebreban.trainticketing.dto.request.TrainRequest;
import com.vasilebreban.trainticketing.model.Train;
import com.vasilebreban.trainticketing.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainService {

    private final TrainRepository trainRepository;

    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    public Train getTrainById(Long id) {
        return trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train not found with id: " + id));
    }

    public Train createTrain(TrainRequest request) {
        if (trainRepository.existsByTrainNumberIgnoreCase(request.getTrainNumber())) {
            throw new RuntimeException("Train already exists with number: " + request.getTrainNumber());
        }

        Train train = Train.builder()
                .trainNumber(request.getTrainNumber())
                .capacity(request.getCapacity())
                .delayMinutes(0)
                .build();

        return trainRepository.save(train);
    }

    public Train updateTrain(Long id, TrainRequest request) {
        Train train = getTrainById(id);

        train.setTrainNumber(request.getTrainNumber());
        train.setCapacity(request.getCapacity());

        return trainRepository.save(train);
    }

    public void deleteTrain(Long id) {
        if (!trainRepository.existsById(id)) {
            throw new RuntimeException("Train not found with id: " + id);
        }

        trainRepository.deleteById(id);
    }
}