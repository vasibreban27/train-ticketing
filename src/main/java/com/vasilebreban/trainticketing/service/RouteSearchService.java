package com.vasilebreban.trainticketing.service;

import com.vasilebreban.trainticketing.dto.response.RouteSearchResponse;
import com.vasilebreban.trainticketing.dto.response.RouteSegmentResponse;
import com.vasilebreban.trainticketing.exception.ResourceNotFoundException;
import com.vasilebreban.trainticketing.model.Route;
import com.vasilebreban.trainticketing.model.RouteStop;
import com.vasilebreban.trainticketing.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteSearchService {

    private static final String DIRECT = "DIRECT";
    private static final String CHANGEOVER = "CHANGEOVER";

    private final RouteRepository routeRepository;

    @Transactional(readOnly = true)
    public List<RouteSearchResponse> searchRoutes(String from, String to) {
        List<Route> routes = routeRepository.findAll();
        List<RouteSearchResponse> results = new ArrayList<>();

        results.addAll(findDirectRoutes(routes, from, to));
        results.addAll(findRoutesWithOneChange(routes, from, to));

        if (results.isEmpty()) {
            throw new ResourceNotFoundException("No route found between " + from + " and " + to);
        }

        return results;
    }

    private List<RouteSearchResponse> findDirectRoutes(List<Route> routes, String from, String to) {
        List<RouteSearchResponse> directRoutes = new ArrayList<>();

        for (Route route : routes) {
            List<RouteStop> stops = getSortedStops(route);

            RouteStop departureStop = findStopByStationName(stops, from);
            RouteStop arrivalStop = findStopByStationName(stops, to);

            if (isValidTravelDirection(departureStop, arrivalStop)) {
                RouteSegmentResponse segment = buildSegment(route, departureStop, arrivalStop);

                RouteSearchResponse response = RouteSearchResponse.builder()
                        .type(DIRECT)
                        .segments(List.of(segment))
                        .build();

                directRoutes.add(response);
            }
        }

        return directRoutes;
    }

    private List<RouteSearchResponse> findRoutesWithOneChange(List<Route> routes, String from, String to) {
        List<RouteSearchResponse> changeoverRoutes = new ArrayList<>();

        for (Route firstRoute : routes) {
            List<RouteStop> firstRouteStops = getSortedStops(firstRoute);
            RouteStop departureStop = findStopByStationName(firstRouteStops, from);

            if (departureStop == null) {
                continue;
            }

            for (Route secondRoute : routes) {
                if (firstRoute.getId().equals(secondRoute.getId())) {
                    continue;
                }

                List<RouteStop> secondRouteStops = getSortedStops(secondRoute);
                RouteStop finalArrivalStop = findStopByStationName(secondRouteStops, to);

                if (finalArrivalStop == null) {
                    continue;
                }

                for (RouteStop changeoverStopFirstRoute : firstRouteStops) {
                    RouteStop changeoverStopSecondRoute = findStopByStationName(
                            secondRouteStops,
                            changeoverStopFirstRoute.getStation().getName()
                    );

                    if (changeoverStopSecondRoute == null) {
                        continue;
                    }

                    boolean validFirstSegment = isValidTravelDirection(departureStop, changeoverStopFirstRoute);
                    boolean validSecondSegment = isValidTravelDirection(changeoverStopSecondRoute, finalArrivalStop);
                    boolean validChangeoverTime = isValidChangeoverTime(changeoverStopFirstRoute, changeoverStopSecondRoute);

                    if (validFirstSegment && validSecondSegment && validChangeoverTime) {
                        RouteSegmentResponse firstSegment = buildSegment(
                                firstRoute,
                                departureStop,
                                changeoverStopFirstRoute
                        );

                        RouteSegmentResponse secondSegment = buildSegment(
                                secondRoute,
                                changeoverStopSecondRoute,
                                finalArrivalStop
                        );

                        RouteSearchResponse response = RouteSearchResponse.builder()
                                .type(CHANGEOVER)
                                .segments(List.of(firstSegment, secondSegment))
                                .build();

                        changeoverRoutes.add(response);
                    }
                }
            }
        }

        return changeoverRoutes;
    }

    private List<RouteStop> getSortedStops(Route route) {
        return route.getStops()
                .stream()
                .sorted(Comparator.comparing(RouteStop::getStopOrder))
                .toList();
    }

    private RouteStop findStopByStationName(List<RouteStop> stops, String stationName) {
        return stops.stream()
                .filter(stop -> stop.getStation().getName().equalsIgnoreCase(stationName))
                .findFirst()
                .orElse(null);
    }

    private boolean isValidTravelDirection(RouteStop departureStop, RouteStop arrivalStop) {
        return departureStop != null
                && arrivalStop != null
                && departureStop.getStopOrder() < arrivalStop.getStopOrder();
    }

    private boolean isValidChangeoverTime(RouteStop firstRouteArrivalStop, RouteStop secondRouteDepartureStop) {
        if (firstRouteArrivalStop.getArrivalTime() == null || secondRouteDepartureStop.getDepartureTime() == null) {
            return false;
        }

        return !secondRouteDepartureStop.getDepartureTime().isBefore(firstRouteArrivalStop.getArrivalTime());
    }

    private RouteSegmentResponse buildSegment(Route route, RouteStop departureStop, RouteStop arrivalStop) {
        return RouteSegmentResponse.builder()
                .trainId(route.getTrain().getId())
                .trainNumber(route.getTrain().getTrainNumber())
                .departureStation(departureStop.getStation().getName())
                .arrivalStation(arrivalStop.getStation().getName())
                .departureTime(departureStop.getDepartureTime())
                .arrivalTime(arrivalStop.getArrivalTime())
                .build();
    }
}