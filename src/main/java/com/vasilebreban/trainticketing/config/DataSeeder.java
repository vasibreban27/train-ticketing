package com.vasilebreban.trainticketing.config;

import com.vasilebreban.trainticketing.model.Route;
import com.vasilebreban.trainticketing.model.RouteStop;
import com.vasilebreban.trainticketing.model.Station;
import com.vasilebreban.trainticketing.model.Train;
import com.vasilebreban.trainticketing.repository.RouteRepository;
import com.vasilebreban.trainticketing.repository.RouteStopRepository;
import com.vasilebreban.trainticketing.repository.StationRepository;
import com.vasilebreban.trainticketing.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;

    @Override
    public void run(String... args) {
        if (stationRepository.count() > 0) {
            return;
        }

        Station cluj = stationRepository.save(Station.builder().name("Cluj-Napoca").build());
        Station sibiu = stationRepository.save(Station.builder().name("Sibiu").build());
        Station brasov = stationRepository.save(Station.builder().name("Brasov").build());
        Station bucuresti = stationRepository.save(Station.builder().name("Bucuresti").build());
        Station iasi = stationRepository.save(Station.builder().name("Iasi").build());

        Train ir101 = trainRepository.save(
                Train.builder()
                        .trainNumber("IR-101")
                        .capacity(100)
                        .delayMinutes(0)
                        .build()
        );

        Train ir205 = trainRepository.save(
                Train.builder()
                        .trainNumber("IR-205")
                        .capacity(80)
                        .delayMinutes(0)
                        .build()
        );

        Route route1 = routeRepository.save(
                Route.builder()
                        .train(ir101)
                        .build()
        );

        routeStopRepository.save(RouteStop.builder()
                .route(route1)
                .station(cluj)
                .stopOrder(1)
                .arrivalTime(null)
                .departureTime(LocalTime.of(8, 30))
                .build());

        routeStopRepository.save(RouteStop.builder()
                .route(route1)
                .station(sibiu)
                .stopOrder(2)
                .arrivalTime(LocalTime.of(11, 0))
                .departureTime(LocalTime.of(11, 10))
                .build());

        routeStopRepository.save(RouteStop.builder()
                .route(route1)
                .station(brasov)
                .stopOrder(3)
                .arrivalTime(LocalTime.of(14, 10))
                .departureTime(null)
                .build());

        Route route2 = routeRepository.save(
                Route.builder()
                        .train(ir205)
                        .build()
        );

        routeStopRepository.save(RouteStop.builder()
                .route(route2)
                .station(brasov)
                .stopOrder(1)
                .arrivalTime(null)
                .departureTime(LocalTime.of(15, 0))
                .build());

        routeStopRepository.save(RouteStop.builder()
                .route(route2)
                .station(bucuresti)
                .stopOrder(2)
                .arrivalTime(LocalTime.of(18, 0))
                .departureTime(LocalTime.of(18, 20))
                .build());

        routeStopRepository.save(RouteStop.builder()
                .route(route2)
                .station(iasi)
                .stopOrder(3)
                .arrivalTime(LocalTime.of(22, 30))
                .departureTime(null)
                .build());
    }
}