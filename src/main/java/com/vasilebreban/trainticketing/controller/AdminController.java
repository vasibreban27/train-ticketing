package com.vasilebreban.trainticketing.controller;

import com.vasilebreban.trainticketing.dto.request.DelayRequest;
import com.vasilebreban.trainticketing.dto.request.RouteRequest;
import com.vasilebreban.trainticketing.dto.request.TrainRequest;
import com.vasilebreban.trainticketing.dto.response.BookingResponse;
import com.vasilebreban.trainticketing.dto.response.MessageResponse;
import com.vasilebreban.trainticketing.dto.response.RouteResponse;
import com.vasilebreban.trainticketing.dto.response.TrainResponse;
import com.vasilebreban.trainticketing.mapper.BookingMapper;
import com.vasilebreban.trainticketing.mapper.TrainMapper;
import com.vasilebreban.trainticketing.model.Booking;
import com.vasilebreban.trainticketing.model.Train;
import com.vasilebreban.trainticketing.service.AdminService;
import com.vasilebreban.trainticketing.service.RouteService;
import com.vasilebreban.trainticketing.service.TrainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TrainService trainService;
    private final RouteService routeService;
    private final AdminService adminService;

    @PostMapping("/trains")
    public ResponseEntity<TrainResponse> createTrain(@Valid @RequestBody TrainRequest request) {
        Train train = trainService.createTrain(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TrainMapper.toResponse(train));
    }

    @PutMapping("/trains/{id}")
    public ResponseEntity<TrainResponse> updateTrain(
            @PathVariable Long id,
            @Valid @RequestBody TrainRequest request
    ) {
        Train train = trainService.updateTrain(id, request);

        return ResponseEntity.ok(TrainMapper.toResponse(train));
    }

    @DeleteMapping("/trains/{id}")
    public ResponseEntity<MessageResponse> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);

        return ResponseEntity.ok(new MessageResponse("Train deleted successfully."));
    }

    @GetMapping("/routes")
    public ResponseEntity<List<RouteResponse>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/routes/{id}")
    public ResponseEntity<RouteResponse> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @PostMapping("/routes")
    public ResponseEntity<RouteResponse> createRoute(@Valid @RequestBody RouteRequest request) {
        RouteResponse route = routeService.createRoute(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(route);
    }

    @PutMapping("/routes/{id}")
    public ResponseEntity<RouteResponse> updateRoute(
            @PathVariable Long id,
            @Valid @RequestBody RouteRequest request
    ) {
        return ResponseEntity.ok(routeService.updateRoute(id, request));
    }

    @DeleteMapping("/routes/{id}")
    public ResponseEntity<MessageResponse> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);

        return ResponseEntity.ok(new MessageResponse("Route deleted successfully."));
    }

    @GetMapping("/trains/{trainId}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsForTrain(@PathVariable Long trainId) {
        List<Booking> bookings = adminService.getBookingsForTrain(trainId);

        List<BookingResponse> response = bookings.stream()
                .map(BookingMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/trains/{trainId}/delay")
    public ResponseEntity<TrainResponse> markTrainAsDelayed(
            @PathVariable Long trainId,
            @Valid @RequestBody DelayRequest request
    ) {
        Train train = adminService.markTrainAsDelayed(trainId, request);

        return ResponseEntity.ok(TrainMapper.toResponse(train));
    }
}