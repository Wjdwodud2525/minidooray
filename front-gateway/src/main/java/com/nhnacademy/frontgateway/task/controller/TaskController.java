package com.nhnacademy.frontgateway.task.controller;

import com.nhnacademy.frontgateway.task.client.TaskClient;
import com.nhnacademy.frontgateway.task.dto.TaskCreateRequest;
import com.nhnacademy.frontgateway.task.dto.TaskDetailResponse;
import com.nhnacademy.frontgateway.task.dto.TaskSummaryResponse;
import com.nhnacademy.frontgateway.task.dto.TaskUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {
    private final TaskClient taskClient;

    @Autowired
    public TaskController(TaskClient taskClient){
        this.taskClient = taskClient;
    }

    @GetMapping
    public String getTasks(@PathVariable String projectId, Model model){
        List<TaskSummaryResponse> tasks = taskClient.getTasks(projectId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("projectId", projectId);
        return "task/tasks";
    }

    @PostMapping
    public String createTask(@PathVariable String projectId, TaskCreateRequest taskCreateRequest){
        TaskDetailResponse task = taskClient.createTask(projectId, taskCreateRequest);
        return "redirect:/projects/" + projectId + "/tasks";
    }

    @GetMapping("/{taskId}")
    public String getTask(@PathVariable String projectId, @PathVariable String taskId, Model model) {
        TaskDetailResponse task = taskClient.getTask(projectId, taskId);
        model.addAttribute("task", task);
        model.addAttribute("projectId", projectId);
        return "task/task";
    }

    @PostMapping("/{taskId}")
    public String updateTask(@PathVariable String projectId, @PathVariable String taskId, TaskUpdateRequest taskUpdateRequest){
        TaskDetailResponse task = taskClient.updateTask(projectId, taskId, taskUpdateRequest);
        return "redirect:/projects/" + projectId + "/tasks";
    }
    @DeleteMapping("/{taskId}")
    public String deleteTask(@PathVariable String projectId, @PathVariable String taskId){
        taskClient.deleteTask(projectId, taskId);
        return "redirect:/projects/" + projectId + "/tasks";
    }
}
