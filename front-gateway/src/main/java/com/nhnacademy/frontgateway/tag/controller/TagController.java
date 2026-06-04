package com.nhnacademy.frontgateway.tag.controller;

import com.nhnacademy.frontgateway.tag.client.TagClient;
import com.nhnacademy.frontgateway.tag.dto.TagCreateRequest;
import com.nhnacademy.frontgateway.tag.dto.TagResponse;
import com.nhnacademy.frontgateway.tag.dto.TagUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/projects/{projectId}/tags")
public class TagController {
    private final TagClient tagClient;

    @Autowired
    public TagController(TagClient tagClient){
        this.tagClient = tagClient;
    }

    @GetMapping
    public String getTags(@PathVariable String projectId, Model model){
        List<TagResponse> tags = tagClient.getTags(projectId);
        model.addAttribute("tags", tags);
        model.addAttribute("projectId", projectId);
        return "tag/tags";
    }

    @PostMapping
    public String createTag(@PathVariable String projectId, TagCreateRequest tagCreateRequest){
        tagClient.createTag(projectId, tagCreateRequest);
        return "redirect:/projects/" + projectId + "/tags";
    }

    @PostMapping("/{tagId}")
    public String updateTag(@PathVariable String projectId, @PathVariable String tagId, TagUpdateRequest tagUpdateRequest){
        tagClient.updateTag(projectId, tagId, tagUpdateRequest);
        return "redirect:/projects/" + projectId + "/tags";
    }
    @DeleteMapping("/{tagId}")
    public String deleteTag(@PathVariable String projectId, @PathVariable String tagId){
        tagClient.deleteTag(projectId, tagId);
        return "redirect:/projects/" + projectId + "/tags";
    }
}
