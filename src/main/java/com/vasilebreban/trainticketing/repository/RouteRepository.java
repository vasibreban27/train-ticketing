package com.vasilebreban.trainticketing.repository;

import com.vasilebreban.trainticketing.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByTrainId(Long trainId);
    boolean existsByTrain_Id(Long trainId);
}
