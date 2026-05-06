package com.vasilebreban.trainticketing.repository;

import com.vasilebreban.trainticketing.model.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {

    List<RouteStop> findByRoute_IdOrderByStopOrderAsc(Long routeId);
    List<RouteStop> findByStation_NameIgnoreCase(String stationName);
    List<RouteStop> findByRoute_Train_IdOrderByStopOrderAsc(Long trainId);
    void deleteByRoute_Id(Long routeId);
}