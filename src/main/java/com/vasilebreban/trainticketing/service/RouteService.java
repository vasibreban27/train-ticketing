package com.vasilebreban.trainticketing.service;

import com.vasilebreban.trainticketing.dto.request.RouteRequest;
import com.vasilebreban.trainticketing.dto.request.RouteStopRequest;
import com.vasilebreban.trainticketing.dto.response.RouteResponse;
import com.vasilebreban.trainticketing.exception.DuplicateResourceException;
import com.vasilebreban.trainticketing.exception.InvalidRouteException;
import com.vasilebreban.trainticketing.exception.ResourceNotFoundException;
import com.vasilebreban.trainticketing.mapper.RouteMapper;
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

import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;

    @Transactional(readOnly = true)
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll()
                .stream()
                .map(RouteMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RouteResponse getRouteById(Long routeId) {
        Route route = findRouteById(routeId);

        return RouteMapper.toResponse(route);
    }

    @Transactional
    public RouteResponse createRoute(RouteRequest request) {
        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + request.getTrainId()));

        validateRouteStops(request.getStops());

        if (routeRepository.existsByTrain_Id(train.getId())) {
            throw new DuplicateResourceException("Route already exists for train id: " + train.getId());
        }

        Route route = Route.builder()
                .train(train)
                .build();

        Route savedRoute = routeRepository.save(route);

        List<RouteStop> stops = createRouteStops(savedRoute, request.getStops());

        routeStopRepository.saveAll(stops);

        savedRoute.getStops().clear();
        savedRoute.getStops().addAll(stops);

        return RouteMapper.toResponse(savedRoute);
    }

    @Transactional
    public RouteResponse updateRoute(Long routeId, RouteRequest request) {
        Route route = findRouteById(routeId);

        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + request.getTrainId()));

        validateRouteStops(request.getStops());

        route.setTrain(train);

        routeStopRepository.deleteByRoute_Id(routeId);
        route.getStops().clear();

        List<RouteStop> newStops = createRouteStops(route, request.getStops());

        routeStopRepository.saveAll(newStops);

        route.getStops().addAll(newStops);

        Route savedRoute = routeRepository.save(route);

        return RouteMapper.toResponse(savedRoute);
    }

    @Transactional
    public void deleteRoute(Long routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new ResourceNotFoundException("Route not found with id: " + routeId);
        }

        routeRepository.deleteById(routeId);
    }

    private Route findRouteById(Long routeId) {
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + routeId));
    }

    private List<RouteStop> createRouteStops(Route route, List<RouteStopRequest> stopRequests) {
        return stopRequests.stream()
                .sorted(Comparator.comparing(RouteStopRequest::getStopOrder))
                .map(stopRequest -> createRouteStop(route, stopRequest))
                .toList();
    }

    private RouteStop createRouteStop(Route route, RouteStopRequest stopRequest) {
        Station station = stationRepository.findByNameIgnoreCase(stopRequest.getStationName())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + stopRequest.getStationName()));

        return RouteStop.builder()
                .route(route)
                .station(station)
                .stopOrder(stopRequest.getStopOrder())
                .arrivalTime(stopRequest.getArrivalTime())
                .departureTime(stopRequest.getDepartureTime())
                .build();
    }

    private void validateRouteStops(List<RouteStopRequest> stops) {
        if (stops == null || stops.size() < 2) {
            throw new InvalidRouteException("A route must contain at least two stops.");
        }

        validateUniqueStopOrders(stops);
        validateUniqueStations(stops);
        validateFirstAndLastStopTimes(stops);
    }

    private void validateUniqueStopOrders(List<RouteStopRequest> stops) {
        Set<Integer> stopOrders = new HashSet<>();

        for (RouteStopRequest stop : stops) {
            if (!stopOrders.add(stop.getStopOrder())) {
                throw new InvalidRouteException("Duplicate stop order: " + stop.getStopOrder());
            }
        }
    }

    private void validateUniqueStations(List<RouteStopRequest> stops) {
        Set<String> stationNames = new HashSet<>();

        for (RouteStopRequest stop : stops) {
            String normalizedStationName = stop.getStationName().trim().toLowerCase();

            if (!stationNames.add(normalizedStationName)) {
                throw new InvalidRouteException("Duplicate station in route: " + stop.getStationName());
            }
        }
    }

    private void validateFirstAndLastStopTimes(List<RouteStopRequest> stops) {
        List<RouteStopRequest> sortedStops = stops.stream()
                .sorted(Comparator.comparing(RouteStopRequest::getStopOrder))
                .toList();

        RouteStopRequest firstStop = sortedStops.get(0);
        RouteStopRequest lastStop = sortedStops.get(sortedStops.size() - 1);

        if (firstStop.getDepartureTime() == null) {
            throw new InvalidRouteException("First stop must have a departure time.");
        }

        if (lastStop.getArrivalTime() == null) {
            throw new InvalidRouteException("Last stop must have an arrival time.");
        }

        for (int i = 1; i < sortedStops.size() - 1; i++) {
            RouteStopRequest currentStop = sortedStops.get(i);

            if (currentStop.getArrivalTime() == null || currentStop.getDepartureTime() == null) {
                throw new InvalidRouteException(
                        "Intermediate stop must have both arrival and departure time: "
                                + currentStop.getStationName()
                );
            }

            validateTimeOrder(currentStop.getArrivalTime(), currentStop.getDepartureTime(), currentStop.getStationName());
        }
    }

    private void validateTimeOrder(LocalTime arrivalTime, LocalTime departureTime, String stationName) {
        if (arrivalTime != null && departureTime != null && departureTime.isBefore(arrivalTime)) {
            throw new InvalidRouteException("Departure time cannot be before arrival time for station: " + stationName);
        }
    }
}