package com.nhnacademy.frontgateway.milestone.controller;

import com.nhnacademy.frontgateway.milestone.client.MilestoneClient;
import com.nhnacademy.frontgateway.milestone.dto.MilestoneCreateRequest;
import com.nhnacademy.frontgateway.milestone.dto.MilestoneResponse;
import com.nhnacademy.frontgateway.milestone.dto.MilestoneUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/projects/{projectId}/milestones")
public class MilestoneController {
    private final MilestoneClient milestoneClient;
    @Autowired
    public MilestoneController(MilestoneClient milestoneClient) {
        this.milestoneClient = milestoneClient;
    }
    @GetMapping
    public String getMilestone(@PathVariable String projectId, Model model){
        List<MilestoneResponse> milestones = milestoneClient.getMilestones(projectId);
        model.addAttribute("milestones", milestones);
        model.addAttribute("projectId", projectId);

        return "milestone/milestones";
    }

    @GetMapping("/{milestoneId}")
    public String getMilestone(@PathVariable String projectId, @PathVariable String milestoneId, Model model){
        MilestoneResponse milestone = milestoneClient.getMilestone(projectId, milestoneId);
        model.addAttribute("milestone", milestone);
        model.addAttribute("projectId", projectId);

        return "milestone/milestone";
    }

    @PostMapping
    public String createMilestone(@PathVariable String projectId,
                                  @ModelAttribute MilestoneCreateRequest request){
        milestoneClient.createMilestone(projectId,request);
        return "redirect:/projects/" + projectId + "/milestones";
    }

    @PostMapping("/{milestoneId}")
    public String updateMilestone(@PathVariable String projectId,
                                  @PathVariable String milestoneId,
                                  @ModelAttribute MilestoneUpdateRequest request){
        milestoneClient.updateMilestone(projectId,milestoneId,request);
        return "redirect:/projects/" + projectId + "/milestones";
    }
    @DeleteMapping("/{milestoneId}")
    public String deleteMilestone(@PathVariable String projectId,
                                  @PathVariable String milestoneId){
        milestoneClient.deleteMilestone(projectId,milestoneId);
        return "redirect:/projects/" + projectId + "/milestones";
    }
}
