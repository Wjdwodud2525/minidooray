package com.nhnacademy.frontgateway.task.client;

import com.nhnacademy.frontgateway.task.dto.TaskCreateRequest;
import com.nhnacademy.frontgateway.task.dto.TaskDetailResponse;
import com.nhnacademy.frontgateway.task.dto.TaskSummaryResponse;
import com.nhnacademy.frontgateway.task.dto.TaskUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskClient {
    private final RestTemplate restTemplate;

    @Value("${api.task.url}")
    private String taskUrl;

    public List<TaskSummaryResponse> getTasks(String projectId){
        return restTemplate.exchange(
                taskUrl + "/projects/" + projectId + "/tasks",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TaskSummaryResponse>>() {
                }
        ).getBody();
    }
    public TaskDetailResponse getTask(String projectId, String taskId){
        return restTemplate.getForObject(taskUrl + "/projects/" + projectId + "/tasks/" + taskId,  TaskDetailResponse.class );
    }

    public TaskDetailResponse createTask(String projectId, TaskCreateRequest taskCreateRequest){
        return restTemplate.postForObject(taskUrl + "/projects/" + projectId + "/tasks", taskCreateRequest, TaskDetailResponse.class);
    }

    public TaskDetailResponse updateTask(String projectId, String taskId, TaskUpdateRequest taskUpdateRequest){
        return restTemplate.postForObject(taskUrl + "/projects/" + projectId + "/tasks/" + taskId, taskUpdateRequest, TaskDetailResponse.class);
    }

    public void deleteTask(String projectId, String taskId){
        restTemplate.delete(taskUrl + "/projects/" + projectId + "/tasks/" + taskId);
    }
}
