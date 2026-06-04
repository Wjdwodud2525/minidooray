package com.nhnacademy.frontgateway.milestone.dto;

public record MilestoneResponse(
        Long milestoneId,
        String name,
        MilestoneStatus status
) {}