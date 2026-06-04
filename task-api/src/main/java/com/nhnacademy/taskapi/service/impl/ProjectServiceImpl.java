package com.nhnacademy.taskapi.service.impl;

import com.nhnacademy.taskapi.dto.project.ProjectCreateRequest;
import com.nhnacademy.taskapi.dto.project.ProjectResponse;
import com.nhnacademy.taskapi.dto.project.ProjectUpdateRequest;
import com.nhnacademy.taskapi.entity.Project;
import com.nhnacademy.taskapi.entity.ProjectMember;
import com.nhnacademy.taskapi.exception.allow.ex.ProjectNotAllowException;
import com.nhnacademy.taskapi.exception.notfound.ex.CommentNotFoundException;
import com.nhnacademy.taskapi.exception.notfound.ex.ProjectMemberNotFoundException;
import com.nhnacademy.taskapi.exception.notfound.ex.ProjectNotFoundException;
import com.nhnacademy.taskapi.repository.*;
import com.nhnacademy.taskapi.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TagRepository tagRepository;
    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;
    private final TaskTagRepository taskTagRepository;
    private final CommentRepository commentRepository;
    @Override
    @Transactional
    public ProjectResponse createProject(ProjectCreateRequest req, String adminId) {
        //프로젝트 생성
        Project project=Project.builder()
                .name(req.name())
                .build();

        ProjectMember projectMember = ProjectMember.builder()
                .userId(adminId)
                .admin(true)
                .build();

        project.addMember(projectMember);

        Project resultProject = projectRepository.save(project);

        return ProjectResponse.builder()
                .projectId(resultProject.getId())
                .name(resultProject.getName())
                .status(resultProject.getStatus())
                .admin(projectMember.isAdmin())
                .build();
    }

    @Override
    public ProjectResponse getProject(Long projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new ProjectMemberNotFoundException(projectId+"번 프로젝트를 찾을 수 없습니다."));

        ProjectMember projectMember = projectMemberRepository.findByProject_IdAndUserId(projectId,userId)
                .orElseThrow(() -> new ProjectMemberNotFoundException(projectId+"번 프로젝트는" + userId + "님이 참여하고 있지 않습니다."));

        return ProjectResponse.builder()
                .projectId(project.getId())
                .name(project.getName())
                .status(project.getStatus())
                .admin(projectMember.isAdmin())
                .build();
    }

    @Override
    public List<ProjectResponse> getProjects(String userId) {
        return projectRepository.findProjectResponsesByUserId(userId);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest req, String userId) {
        ProjectMember projectMember = projectMemberRepository.findByProject_IdAndUserId(projectId,userId)
                .orElseThrow(()->new ProjectMemberNotFoundException(projectId+"번 프로젝트는" + userId + "님이 참여하고 있지 않습니다."));
        if(!projectMember.isAdmin()){
            throw new ProjectMemberNotFoundException(projectId+"번 프로젝트는" + userId + "님이 관리자 권한이 없습니다.");
        }
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new ProjectNotFoundException(projectId + "번 프로젝트를 찾을 수 없습니다."));

        project.update(req.name(), req.status());

        return ProjectResponse.builder()
                .projectId(project.getId())
                .name(project.getName())
                .status(project.getStatus())
                .admin(projectMember.isAdmin())
                .build();
    }

    @Override
    @Transactional
    public void deleteProject(Long projectId, String userId) {
        ProjectMember projectMember = projectMemberRepository.findByProject_IdAndUserId(projectId,userId)
                .orElseThrow(()-> new ProjectMemberNotFoundException(projectId+"번 프로젝트는" + userId + "님이 참여하고 있지 않습니다."));
        if(!projectMember.isAdmin()){
            throw new ProjectNotAllowException(projectId+"번 프로젝트는" + userId + "님이 관리자 권한이 없습니다.");
        }
        Project project = projectRepository.findById(projectId)
                        .orElseThrow(()-> new CommentNotFoundException(projectId+"번 프로젝트를 찾을 수 없습니다."));
        taskTagRepository.deleteAllByProjectIdInBulk(projectId);

        commentRepository.deleteAllByProjectIdInBulk(projectId);

        taskRepository.deleteByProject_Id(projectId);

        tagRepository.deleteByProject_Id(projectId);

        milestoneRepository.deleteByProject_Id(projectId);

        projectRepository.delete(project);
    }
}
