package com.nhnacademy.minidooray.milestone.dto;

public record MilestoneResponse(
        Long milestoneId,
        String name,
        MilestoneStatus status
) {}