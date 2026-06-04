package com.nhnacademy.minidooray.task.dto;

import com.nhnacademy.minidooray.milestone.dto.MilestoneResponse;
import com.nhnacademy.minidooray.tag.dto.TagResponse;

import java.util.List;

public record TaskDetailResponse(
        Long taskId,
        Long projectId,
        String title,
        String content,
        MilestoneResponse milestone,
        List<TagResponse> tags
) {}
