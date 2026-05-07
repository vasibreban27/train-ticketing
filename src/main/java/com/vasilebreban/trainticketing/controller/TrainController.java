package com.vasilebreban.trainticketing.controller;

import com.vasilebreban.trainticketing.dto.response.TrainResponse;
import com.vasilebreban.trainticketing.mapper.TrainMapper;
import com.vasilebreban.trainticketing.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trains")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @GetMapping
    public ResponseEntity<List<TrainResponse>> getAllTrains() {
        List<TrainResponse> trains = trainService.getAllTrains()
                .stream()
                .map(TrainMapper::toResponse)
                .toList();

        return ResponseEntity.ok(trains);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainResponse> getTrainById(@PathVariable Long id) {
        return ResponseEntity.ok(
                TrainMapper.toResponse(trainService.getTrainById(id))
        );
    }
}