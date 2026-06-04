package com.nhnacademy.frontgateway.milestone.client;

import com.nhnacademy.frontgateway.milestone.dto.MilestoneCreateRequest;
import com.nhnacademy.frontgateway.milestone.dto.MilestoneResponse;
import com.nhnacademy.frontgateway.milestone.dto.MilestoneUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MilestoneClient {
    private final RestTemplate restTemplate;

    @Value("${api.task.url}")
    private String taskUrl;

    public List<MilestoneResponse> getMilestones(String projectId){
        return restTemplate.exchange(
                taskUrl + "/projects/" + projectId + "/milestones",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MilestoneResponse>>() {
                }
        ).getBody();
    }

    public MilestoneResponse getMilestone(String projectId, String milestoneId){
        return restTemplate.getForObject(taskUrl + "/projects/" + projectId + "/milestones/" + milestoneId, MilestoneResponse.class);
    }
    // POST /projects/{projectId}/milestones
    public MilestoneResponse createMilestone(String projectId, MilestoneCreateRequest MilestoneCreateRequest) {
        return restTemplate.postForObject(taskUrl + "/projects/" + projectId + "/milestones", MilestoneCreateRequest, MilestoneResponse.class);
    }

    // POST /projects/{projectId}/milestones/{milestoneId}
    public MilestoneResponse updateMilestone(String projectId, String milestoneId, MilestoneUpdateRequest MilestoneUpdateRequest) {
        return restTemplate.postForObject(taskUrl + "/projects/" + projectId + "/milestones/" + milestoneId, MilestoneUpdateRequest, MilestoneResponse.class);
    }

    // DELETE /projects/{projectId}/milestones/{milestoneId}
    public void deleteMilestone(String projectId, String milestoneId) {
        restTemplate.delete(taskUrl + "/projects/" + projectId + "/milestones/" + milestoneId);
    }
}
