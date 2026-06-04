package com.nhnacademy.taskapi.service.impl;

import com.nhnacademy.taskapi.dto.milestone.MilestoneResponse;
import com.nhnacademy.taskapi.dto.tag.TagResponse;
import com.nhnacademy.taskapi.dto.task.TaskCreateRequest;
import com.nhnacademy.taskapi.dto.task.TaskDetailResponse;
import com.nhnacademy.taskapi.dto.task.TaskSummaryResponse;
import com.nhnacademy.taskapi.dto.task.TaskUpdateRequest;
import com.nhnacademy.taskapi.entity.*;
import com.nhnacademy.taskapi.exception.notfound.ex.MilestoneNotFoundException;
import com.nhnacademy.taskapi.exception.notfound.ex.ProjectMemberNotFoundException;
import com.nhnacademy.taskapi.exception.notfound.ex.ProjectNotFoundException;
import com.nhnacademy.taskapi.exception.notfound.ex.TaskNotFoundException;
import com.nhnacademy.taskapi.repository.*;
import com.nhnacademy.taskapi.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskTagRepository taskTagRepository;
    private final MilestoneRepository milestoneRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<TaskSummaryResponse> getTasks(Long projectId, String userId) {
        checkUser(projectId,userId);

        List<Task> tasks= taskRepository.findAllByProject_Id(projectId);

        return tasks.stream().map(task -> {
            String milestoneName = task.getMilestone()!=null?task.getMilestone().getName():null;

            List<String> tags = task.getTaskTags().stream().map(
                    taskTag -> taskTag.getTag().getName()
            ).toList();
            long commentCount = task.getComments().size();

            return new TaskSummaryResponse(
                    task.getId(),
                    task.getTitle(),
                    milestoneName,
                    tags,
                    commentCount
            );
        }).toList();
    }

    @Override
    @Transactional
    public TaskDetailResponse createTask(Long projectId, String userId, TaskCreateRequest req) {
        checkUser(projectId,userId);

        Task task = Task.builder()
                .title(req.title()).content(req.content()).userId(userId).build();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new ProjectNotFoundException(projectId+"번 프로젝트를 찾을수없음"));
        task.setProject(project);
        Task savedTask = taskRepository.save(task);

        return TaskDetailResponse.builder()
                .taskId(savedTask.getId()).projectId(projectId).title(savedTask.getTitle()).content(savedTask.getContent()).milestone(null).tags(List.of()).build();
    }

    @Override
    public TaskDetailResponse getTask(Long projectId, Long taskId, String userId) {
        checkUser(projectId,userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(()->new TaskNotFoundException(taskId));
        MilestoneResponse milestone = task.getMilestone()==null?null:MilestoneResponse.builder()
                                                                     .milestoneId(task.getMilestone().getId()).name(task.getMilestone().getName()).status(task.getMilestone().getStatus()).build();
        List<TagResponse> tags = task.getTaskTags().stream()
                .map(taskTag -> {
                    Tag tag = taskTag.getTag();

                    return TagResponse.builder()
                            .tagId(tag.getId()).name(tag.getName()).build();
                }).toList();
        return TaskDetailResponse.builder()
                .taskId(task.getId()).projectId(task.getProject().getId()).title(task.getTitle()).content(task.getContent()).milestone(milestone).tags(tags).build();
    }

    @Override
    @Transactional
    public TaskDetailResponse updateTask(Long projectId, Long taskId, String userId, TaskUpdateRequest req) {
        checkUser(projectId,userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new TaskNotFoundException(taskId));

        Milestone mileStone = null;
        if (req.milestoneId() != null) {
            mileStone = milestoneRepository.findById(req.milestoneId())
                    .orElseThrow(() -> new MilestoneNotFoundException(req.milestoneId() + "번 마일스톤을 찾을 수 없습니다."));
        }
        // 업데이트 대상 태스크 태그 리스트 초기화 -> 기존 매핑된 태그 리스트로 시작
        List<TaskTag> updatedTaskTags = new ArrayList<>(task.getTaskTags());
        // 현재 태스크에 매핑된 태스크 태그 리스트
        List<TaskTag> existingTaskTags = task.getTaskTags();
        //요청으로 들어온 태그 아이디 리스트 널인 경우 빈 리스트로
        List<Long> requestedTagIds = req.tagIds()==null?List.of():req.tagIds();
        //삭제 대상 걸러내기 -> 현재 매핑된 태그 중, 요청 리스트에 없는 아이디를 가진 것들 삭제
        List<TaskTag> tagsToDelete = existingTaskTags.stream()
                .filter(taskTag -> !requestedTagIds.contains(taskTag.getTag().getId()))
                .toList();
        taskTagRepository.deleteAll(tagsToDelete);//삭제 대상 디비 반영
        updatedTaskTags.removeAll(tagsToDelete);// 업데이트 대상 리스트에서도 삭제 대상 제거
        //새로 생성 대상 걸러내기 -> 요청 리스트 중, 기존 매핑에 없는 아이디를 가진것을 생성
        List<Long> existingTagIds = existingTaskTags.stream()
                .filter(taskTag -> requestedTagIds.contains(taskTag.getTag().getId()))
                .map(taskTag -> taskTag.getTag().getId())
                .toList();

        List<Long> tagsToCreateIds = requestedTagIds.stream()
                .filter(tagId -> !existingTagIds.contains(tagId)).toList();
        if(!tagsToCreateIds.isEmpty()) {
            List<Tag> newTags = tagRepository.findAllById(tagsToCreateIds);
            List<TaskTag> newTaskTags = newTags.stream()
                    .map(tag -> TaskTag.builder()
                            .task(task).tag(tag).build()).toList();
            taskTagRepository.saveAll(newTaskTags);
            updatedTaskTags.addAll(newTaskTags);
        }
        task.update(req.title(),req.content(),mileStone,updatedTaskTags);

        MilestoneResponse milestone = mileStone == null ? null:
                MilestoneResponse.builder()
                .milestoneId(mileStone.getId()).name(mileStone.getName()).status(mileStone.getStatus()).build();

        List<TagResponse> tagResponses = updatedTaskTags.stream()
                .map(taskTag -> {
                    Tag tag = taskTag.getTag();
                    return TagResponse.builder()
                            .tagId(tag.getId()).name(tag.getName()).build();
                }).toList();
        return TaskDetailResponse.builder()
                .taskId(task.getId()).projectId(task.getProject().getId()).title(task.getTitle()).content(task.getContent()).milestone(milestone).tags(tagResponses).build();
    }

    @Override
    @Transactional
    public void deleteTask(Long projectId, String userId, Long taskId) {
        checkUser(projectId,userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new TaskNotFoundException(taskId));

        taskTagRepository.deleteByTask_Id(task.getId());

        commentRepository.deleteByTask_Id(task.getId());

        taskRepository.delete(task);
    }

    private void checkUser(Long projectId, String userId){
        if(!projectMemberRepository.existsByProject_IdAndUserId(projectId,userId)){
            throw new ProjectMemberNotFoundException("프로젝트 멤버가 아님");
        }
    }
}
