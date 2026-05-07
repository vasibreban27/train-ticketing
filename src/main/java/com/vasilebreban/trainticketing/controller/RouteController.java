package com.vasilebreban.trainticketing.controller;

import com.vasilebreban.trainticketing.dto.response.RouteSearchResponse;
import com.vasilebreban.trainticketing.service.RouteSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteSearchService routeSearchService;

    @GetMapping("/search")
    public ResponseEntity<List<RouteSearchResponse>> searchRoutes(
            @RequestParam String from,
            @RequestParam String to
    ) {
        return ResponseEntity.ok(routeSearchService.searchRoutes(from, to));
    }
}