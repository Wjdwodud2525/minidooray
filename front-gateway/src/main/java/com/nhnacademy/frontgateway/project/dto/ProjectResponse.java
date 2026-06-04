package com.nhnacademy.frontgateway.project.dto;

public record ProjectResponse(
        Long projectId,
        String name,
        ProjectStatus status,
        boolean admin
) {}
