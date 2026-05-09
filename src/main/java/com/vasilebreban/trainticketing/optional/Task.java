package com.vasilebreban.trainticketing.optional;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Task {

    private String title;

    private int urgency;

    private int importance;

    private int estimatedHours;

    private int deadlineDays;
}