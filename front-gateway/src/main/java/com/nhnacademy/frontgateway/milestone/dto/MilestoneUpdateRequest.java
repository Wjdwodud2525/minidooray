package com.nhnacademy.frontgateway.milestone.dto;

public record MilestoneUpdateRequest(
        String name,
        MilestoneStatus status
) {}
