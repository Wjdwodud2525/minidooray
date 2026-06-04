package com.nhnacademy.minidooray.task.dto;

import java.util.List;

public record TaskSummaryResponse(
        Long taskId,
        String title,
        String milestoneName,
        List<String> tags,
        long commentCount
) {}
