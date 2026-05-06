package com.vasilebreban.trainticketing.repository;

import com.vasilebreban.trainticketing.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainRepository extends JpaRepository<Train, Long> {

    Optional<Train> findByTrainNumberIgnoreCase(String trainNumber);
    boolean existsByTrainNumberIgnoreCase(String trainNumber);
}