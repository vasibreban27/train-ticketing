package com.vasilebreban.trainticketing.mapper;

import com.vasilebreban.trainticketing.dto.response.RouteResponse;
import com.vasilebreban.trainticketing.dto.response.RouteStopResponse;
import com.vasilebreban.trainticketing.model.Route;
import com.vasilebreban.trainticketing.model.RouteStop;

import java.util.Comparator;
import java.util.List;

public class RouteMapper {

    private RouteMapper() {
    }

    public static RouteResponse toResponse(Route route) {
        return RouteResponse.builder()
                .id(route.getId())
                .train(TrainMapper.toResponse(route.getTrain()))
                .stops(toStopResponses(route.getStops()))
                .build();
    }

    public static List<RouteResponse> toResponseList(List<Route> routes) {
        return routes.stream()
                .map(RouteMapper::toResponse)
                .toList();
    }

    private static List<RouteStopResponse> toStopResponses(List<RouteStop> stops) {
        return stops.stream()
                .sorted(Comparator.comparing(RouteStop::getStopOrder))
                .map(RouteMapper::toStopResponse)
                .toList();
    }

    private static RouteStopResponse toStopResponse(RouteStop stop) {
        return RouteStopResponse.builder()
                .id(stop.getId())
                .stationName(stop.getStation().getName())
                .stopOrder(stop.getStopOrder())
                .arrivalTime(stop.getArrivalTime())
                .departureTime(stop.getDepartureTime())
                .build();
    }
}