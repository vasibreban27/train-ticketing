package com.vasilebreban.trainticketing.repository;

import com.vasilebreban.trainticketing.model.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {

    List<RouteStop> findByRouteIdOrderByStopOrderAsc(Long routeId);
    List<RouteStop> findByStationNameIgnoreCase(String stationName);
    List<RouteStop> findByRouteTrainIdOrderByStopOrderAsc(Long trainId);
}