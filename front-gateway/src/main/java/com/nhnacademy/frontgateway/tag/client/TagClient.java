package com.nhnacademy.frontgateway.tag.client;

import com.nhnacademy.frontgateway.tag.dto.TagCreateRequest;
import com.nhnacademy.frontgateway.tag.dto.TagResponse;
import com.nhnacademy.frontgateway.tag.dto.TagUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TagClient {
    private final RestTemplate restTemplate;

    @Value("${api.task.url}")
    private String taskUrl;

    public List<TagResponse> getTags(String projectId){
        return restTemplate.exchange(
                taskUrl + "/projects/" + projectId + "/tags",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TagResponse>>() {
                }
        ).getBody();
    }

    public TagResponse getTag(String projectId, String tagId){
        return restTemplate.getForObject(taskUrl + "/projects/" + projectId + "/tags/" + tagId, TagResponse.class);
    }

    public TagResponse createTag(String projectId, TagCreateRequest tagCreateRequest){
        return restTemplate.postForObject(taskUrl + "/projects/" + projectId + "/tags", tagCreateRequest, TagResponse.class);
    }

    public TagResponse updateTag(String projectId, String tagId, TagUpdateRequest tagUpdateRequest ){
        return restTemplate.postForObject(taskUrl + "/projects/" + projectId + "/tags/" + tagId, tagUpdateRequest, TagResponse.class);
    }
    public void deleteTag(String projectId, String tagId){
        restTemplate.delete(taskUrl + "/projects/" + projectId + "/tags/" + tagId);
    }
}
