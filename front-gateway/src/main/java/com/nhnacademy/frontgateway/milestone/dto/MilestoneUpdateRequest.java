package com.nhnacademy.minidooray.milestone.dto;

public record MilestoneUpdateRequest(
        String name,
        MilestoneStatus status
) {}
