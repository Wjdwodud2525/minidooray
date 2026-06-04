package com.nhnacademy.frontgateway.task.dto;

import java.util.List;

public record TaskUpdateRequest(
        String title,
        String content,
        Long milestoneId,
        List<Long> tagIds
) {}
