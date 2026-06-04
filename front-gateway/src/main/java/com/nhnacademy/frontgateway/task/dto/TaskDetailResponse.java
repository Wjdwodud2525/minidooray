package com.nhnacademy.frontgateway.task.dto;

import com.nhnacademy.frontgateway.milestone.dto.MilestoneResponse;
import com.nhnacademy.frontgateway.tag.dto.TagResponse;

import java.util.List;

public record TaskDetailResponse(
        Long taskId,
        Long projectId,
        String title,
        String content,
        MilestoneResponse milestone,
        List<TagResponse> tags
) {}
