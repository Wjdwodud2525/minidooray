package com.nhnacademy.frontgateway.project.controller;

import com.nhnacademy.frontgateway.milestone.client.MilestoneClient;
import com.nhnacademy.frontgateway.milestone.dto.MilestoneResponse;
import com.nhnacademy.frontgateway.project.client.ProjectClient;
import com.nhnacademy.frontgateway.project.dto.*;
import com.nhnacademy.frontgateway.tag.client.TagClient;
import com.nhnacademy.frontgateway.tag.dto.TagResponse;
import com.nhnacademy.frontgateway.task.client.TaskClient;
import com.nhnacademy.frontgateway.task.dto.TaskSummaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectClient projectClient;
    private final TaskClient taskClient;
    private final TagClient tagClient;
    private final MilestoneClient milestoneClient;

    @Autowired
    public ProjectController(ProjectClient projectClient, TaskClient taskClient, TagClient tagClient, MilestoneClient milestoneClient) {
        this.projectClient = projectClient;
        this.taskClient = taskClient;
        this.tagClient = tagClient;
        this.milestoneClient = milestoneClient;
    }

    @GetMapping
    public String getProjects(Model model){
        List<ProjectResponse> projects = projectClient.getProjects();
        model.addAttribute("projects", projects);
        return "project/projects";
    }

    @GetMapping("/{projectId}")
    public String getProject(@PathVariable String projectId, Model model){
        ProjectResponse project = projectClient.getProject(projectId);

        List<TaskSummaryResponse> taskSummaryResponses = taskClient.getTasks(projectId);
        List<TagResponse> tagResponses = tagClient.getTags(projectId);
        List<MilestoneResponse> milestoneResponses = milestoneClient.getMilestones(projectId);
        List<ProjectMemberResponse> memberResponses = projectClient.getProjectMembers(projectId);

        model.addAttribute("project", project);
        model.addAttribute("tasks", taskSummaryResponses);
        model.addAttribute("tags", tagResponses);
        model.addAttribute("milestones", milestoneResponses);
        model.addAttribute("members", memberResponses);
        return "project/project";
    }
    @PostMapping
    public String createProject(@ModelAttribute ProjectCreateRequest request){
        projectClient.createProject(request);
        return "redirect:/projects";
    }
    @PostMapping("/{projectId}")
    public String updateProject(@PathVariable String projectId, @ModelAttribute ProjectUpdateRequest request){
        projectClient.updateProject(projectId, request);
        return "redirect:/projects";
    }
    @DeleteMapping("/{projectId}")
    public String deleteProject(@PathVariable String projectId){
        projectClient.deleteProject(projectId);
        return "redirect:/projects";
    }
    @PostMapping("/{projectId}/members")
    public String addProjectMember(
            @PathVariable String projectId,
            @ModelAttribute ProjectMemberAddRequest request
    ){
        projectClient.addProjectMember(projectId, request);
        return "redirect:/projects/" + projectId;
    }
    @DeleteMapping("/{projectId}/members/{userId}")
    public String deleteProjectMember(
            @PathVariable String projectId,
            @PathVariable String userId
    ){
        projectClient.deleteProjectMember(projectId, userId);
        return "redirect:/projects" + projectId;
    }

}
