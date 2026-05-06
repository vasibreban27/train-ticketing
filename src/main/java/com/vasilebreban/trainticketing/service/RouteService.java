package com.vasilebreban.trainticketing.service;

import com.vasilebreban.trainticketing.dto.request.RouteRequest;
import com.vasilebreban.trainticketing.dto.request.RouteStopRequest;
import com.vasilebreban.trainticketing.model.Route;
import com.vasilebreban.trainticketing.model.RouteStop;
import com.vasilebreban.trainticketing.model.Station;
import com.vasilebreban.trainticketing.model.Train;
import com.vasilebreban.trainticketing.repository.RouteRepository;
import com.vasilebreban.trainticketing.repository.RouteStopRepository;
import com.vasilebreban.trainticketing.repository.StationRepository;
import com.vasilebreban.trainticketing.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));
    }

    @Transactional
    public Route createRoute(RouteRequest request) {
        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new RuntimeException("Train not found with id: " + request.getTrainId()));

        if (routeRepository.existsByTrain_Id(train.getId())) {
            throw new RuntimeException("Route already exists for train id: " + train.getId());
        }

        validateStops(request.getStops());

        Route route = Route.builder()
                .train(train)
                .build();

        Route savedRoute = routeRepository.save(route);

        List<RouteStop> stops = buildRouteStops(savedRoute, request.getStops());
        routeStopRepository.saveAll(stops);

        savedRoute.getStops().addAll(stops);

        return savedRoute;
    }

    @Transactional
    public Route updateRoute(Long routeId, RouteRequest request) {
        Route route = getRouteById(routeId);

        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new RuntimeException("Train not found with id: " + request.getTrainId()));

        validateStops(request.getStops());

        route.setTrain(train);

        routeStopRepository.deleteByRoute_Id(routeId);

        List<RouteStop> newStops = buildRouteStops(route, request.getStops());
        routeStopRepository.saveAll(newStops);

        route.getStops().clear();
        route.getStops().addAll(newStops);

        return routeRepository.save(route);
    }

    @Transactional
    public void deleteRoute(Long routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new RuntimeException("Route not found with id: " + routeId);
        }

        routeRepository.deleteById(routeId);
    }

    private List<RouteStop> buildRouteStops(Route route, List<RouteStopRequest> stopRequests) {
        return stopRequests.stream()
                .sorted(Comparator.comparing(RouteStopRequest::getStopOrder))
                .map(stopRequest -> {
                    Station station = stationRepository.findByNameIgnoreCase(stopRequest.getStationName())
                            .orElseThrow(() -> new RuntimeException(
                                    "Station not found: " + stopRequest.getStationName()
                            ));

                    return RouteStop.builder()
                            .route(route)
                            .station(station)
                            .stopOrder(stopRequest.getStopOrder())
                            .arrivalTime(stopRequest.getArrivalTime())
                            .departureTime(stopRequest.getDepartureTime())
                            .build();
                })
                .toList();
    }

    private void validateStops(List<RouteStopRequest> stops) {
        if (stops.size() < 2) {
            throw new RuntimeException("A route must contain at least two stops.");
        }

        long distinctStopOrders = stops.stream()
                .map(RouteStopRequest::getStopOrder)
                .distinct()
                .count();

        if (distinctStopOrders != stops.size()) {
            throw new RuntimeException("Stop order values must be unique.");
        }
    }
}