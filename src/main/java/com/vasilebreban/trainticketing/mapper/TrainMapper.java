package com.vasilebreban.trainticketing.mapper;

import com.vasilebreban.trainticketing.dto.response.TrainResponse;
import com.vasilebreban.trainticketing.model.Train;

public class TrainMapper {
    private TrainMapper() {
    }

    public static TrainResponse toResponse(Train train) {
        return TrainResponse.builder()
                .id(train.getId())
                .trainNumber(train.getTrainNumber())
                .capacity(train.getCapacity())
                .delayMinutes(train.getDelayMinutes())
                .build();
    }
}
