package com.vasilebreban.trainticketing.service;

import com.vasilebreban.trainticketing.model.Route;
import com.vasilebreban.trainticketing.model.RouteStop;
import com.vasilebreban.trainticketing.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteSearchService {

    private final RouteRepository routeRepository;

    public List<Route> findDirectRoutes(String from, String to) {
        List<Route> routes = routeRepository.findAll();
        List<Route> directRoutes = new ArrayList<>();

        for (Route route : routes) {
            List<RouteStop> stops = route.getStops()
                    .stream()
                    .sorted(Comparator.comparing(RouteStop::getStopOrder))
                    .toList();

            RouteStop departureStop = findStopByStationName(stops, from);
            RouteStop arrivalStop = findStopByStationName(stops, to);

            if (departureStop != null
                    && arrivalStop != null
                    && departureStop.getStopOrder() < arrivalStop.getStopOrder()) {
                directRoutes.add(route);
            }
        }

        if (directRoutes.isEmpty()) {
            throw new RuntimeException("No direct route found between " + from + " and " + to);
        }

        return directRoutes;
    }

    private RouteStop findStopByStationName(List<RouteStop> stops, String stationName) {
        return stops.stream()
                .filter(stop -> stop.getStation().getName().equalsIgnoreCase(stationName))
                .findFirst()
                .orElse(null);
    }
}