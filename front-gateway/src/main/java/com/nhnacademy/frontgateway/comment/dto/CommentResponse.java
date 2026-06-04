package com.nhnacademy.minidooray.comment.dto;

public record CommentResponse(
        Long commentId,
        Long taskId,
        String writerUserId,
        String content
) {}
