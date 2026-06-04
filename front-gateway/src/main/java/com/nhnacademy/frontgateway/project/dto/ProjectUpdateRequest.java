package com.nhnacademy.frontgateway.project.dto;

public record ProjectUpdateRequest(
        String name,
        ProjectStatus status
) {}
