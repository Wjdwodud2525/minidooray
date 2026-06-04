package com.nhnacademy.frontgateway.project.client;

import com.nhnacademy.frontgateway.project.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectClient {
    private final RestTemplate restTemplate;

    @Value("${api.task.url}")
    private String taskUrl;

    public List<ProjectResponse> getProjects(){
        return restTemplate.exchange(
                taskUrl + "/projects",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProjectResponse>>() {
                }
        ).getBody();
    }

    public ProjectResponse getProject(String projectId){
        return restTemplate.getForObject(taskUrl + "/projects/" + projectId, ProjectResponse.class);
    }

    public ProjectResponse createProject(ProjectCreateRequest projectCreateRequest){
        return restTemplate.postForObject(taskUrl + "/projects", projectCreateRequest, ProjectResponse.class);
    }

    public ProjectResponse updateProject(String projectId, ProjectUpdateRequest projectUpdateRequest){
        return restTemplate.postForObject(taskUrl + "/projects/" + projectId, projectUpdateRequest, ProjectResponse.class);
    }

    public void deleteProject(String projectId){
        restTemplate.delete(taskUrl + "/projects/" + projectId);
    }

    public List<ProjectMemberResponse> getProjectMembers(String projectId){
        return restTemplate.exchange(
                taskUrl + "/projects/" + projectId + "/members",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProjectMemberResponse>>() {}
        ).getBody();
    }

    public ProjectMemberResponse addProjectMember(String projectId, ProjectMemberAddRequest projectMemberAddRequest){
        return restTemplate.postForObject(
                taskUrl + "/projects/" + projectId + "/members", projectMemberAddRequest, ProjectMemberResponse.class
        );
    }

    public void deleteProjectMember(String projectId, String userId){
        restTemplate.delete(taskUrl + "/projects/" + projectId + "/members/" + userId);
    }
}
