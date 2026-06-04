package com.nhnacademy.frontgateway.comment.controller;

import com.nhnacademy.frontgateway.comment.client.CommentClient;
import com.nhnacademy.frontgateway.comment.dto.CommentCreateRequest;
import com.nhnacademy.frontgateway.comment.dto.CommentResponse;
import com.nhnacademy.frontgateway.comment.dto.CommentUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/projects/{projectId}/tasks/{taskId}/comments")
public class CommentController {
    private CommentClient commentClient;

    @Autowired
    public CommentController(CommentClient commentClient) {
        this.commentClient = commentClient;
    }

    @GetMapping
    public String getComments(
            @PathVariable String projectId,
            @PathVariable String taskId,
            Model model
    ){
        List<CommentResponse> comments = commentClient.getComments(projectId, taskId);

        model.addAttribute("comments", comments);
        model.addAttribute("projectId", projectId);
        model.addAttribute("taskId", taskId);
        return "comment/comments";
    }

    @GetMapping("/{commentId}")
    public String getComments(
            @PathVariable String projectId,
            @PathVariable String taskId,
            @PathVariable String commentId,
            Model model
    ){
        CommentResponse comment = commentClient.getComment(projectId, taskId, commentId);

        model.addAttribute("comment", comment);
        model.addAttribute("projectId", projectId);
        model.addAttribute("taskId", taskId);
        return "comment/comment";
    }
    @PostMapping
    public String createComment(
            @PathVariable String projectId,
            @PathVariable String taskId,
            @ModelAttribute CommentCreateRequest commentCreateRequest
            ){
        commentClient.createComment(projectId,taskId,commentCreateRequest);
        return "redirect:/projects/" + projectId + "/tasks/" + taskId + "/comments";
    }
    @PostMapping("/{commentId}")
    public String updateComment(
            @PathVariable String projectId,
            @PathVariable String taskId,
            @PathVariable String commentId,
            @ModelAttribute CommentUpdateRequest commentUpdate
    ){
        commentClient.updateComment(projectId, taskId, commentId, commentUpdate);
        return "redirect:/projects/" + projectId + "/tasks/" + taskId + "/comments";
    }

    @DeleteMapping("/{commentId}")
    public String deleteComment(
            @PathVariable String projectId,
            @PathVariable String taskId,
            @PathVariable String commentId
    ){
        commentClient.deleteComment(projectId, taskId, commentId);
        return "redirect:/projects/" + projectId + "/tasks/" + taskId + "/comments";
    }
}
