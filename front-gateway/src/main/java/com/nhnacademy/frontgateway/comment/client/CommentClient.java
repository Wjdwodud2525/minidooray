package com.nhnacademy.frontgateway.comment.client;

import com.nhnacademy.frontgateway.comment.dto.CommentCreateRequest;
import com.nhnacademy.frontgateway.comment.dto.CommentResponse;
import com.nhnacademy.frontgateway.comment.dto.CommentUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentClient {
    private final RestTemplate restTemplate;

    @Value("${api.task.url}")
    private String taskUrl;

    // GET /projects/{projectId}/tasks/{taskId}/comments
    public List<CommentResponse> getComments(String projectId, String taskId){
        return restTemplate.exchange(
                taskUrl + "/projects/" + projectId + "/tasks/" + taskId + "/comments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CommentResponse>>() {
                }
        ).getBody();
    }

    public CommentResponse getComment(String projectId, String taskId, String commentId){
        return restTemplate.getForObject(
                taskUrl + "/projects/" + projectId + "/tasks/" + taskId + "/comments/" + commentId,
                CommentResponse.class);
    }

    public CommentResponse createComment(String projectId, String taskId, CommentCreateRequest commentCreateRequest){
        return restTemplate.postForObject(
                taskUrl + "/projects/" + projectId + "/tasks/" + taskId + "/comments/",
                commentCreateRequest,
                CommentResponse.class
        );
    }
    public CommentResponse updateComment(String projectId, String taskId, String commentId, CommentUpdateRequest commentUpdateRequest){
        return restTemplate.postForObject(
                taskUrl + "/projects/" + projectId + "/tasks/" + taskId + "/comments/" + commentId,
                commentUpdateRequest,
                CommentResponse.class
        );
    }
    public void deleteComment(String projectId, String taskId, String commentId){
        restTemplate.delete(
                taskUrl + "/projects/" + projectId + "/tasks/" + taskId + "/comments/" + commentId
        );
    }

}
