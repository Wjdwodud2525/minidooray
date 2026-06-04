package com.nhnacademy.frontgateway.task.dto;

import java.util.List;

public record TaskCreateRequest(
        String title,
        String content,
        Long milestoneId,
        List<Long> tagIds
) {}
