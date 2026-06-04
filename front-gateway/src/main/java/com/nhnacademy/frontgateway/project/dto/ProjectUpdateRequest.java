package com.nhnacademy.minidooray.project.dto;

public record ProjectUpdateRequest(
        String name,
        ProjectStatus status
) {}
