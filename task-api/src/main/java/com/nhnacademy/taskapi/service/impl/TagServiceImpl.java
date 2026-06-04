package com.nhnacademy.taskapi.service.impl;

import com.nhnacademy.taskapi.dto.tag.TagCreateRequest;
import com.nhnacademy.taskapi.dto.tag.TagResponse;
import com.nhnacademy.taskapi.dto.tag.TagUpdateRequest;
import com.nhnacademy.taskapi.entity.Project;
import com.nhnacademy.taskapi.entity.Tag;
import com.nhnacademy.taskapi.exception.notfound.ex.ProjectMemberNotFoundException;
import com.nhnacademy.taskapi.exception.notfound.ex.ProjectNotFoundException;
import com.nhnacademy.taskapi.exception.notfound.ex.TagNotFoundException;
import com.nhnacademy.taskapi.repository.ProjectMemberRepository;
import com.nhnacademy.taskapi.repository.ProjectRepository;
import com.nhnacademy.taskapi.repository.TagRepository;
import com.nhnacademy.taskapi.repository.TaskTagRepository;
import com.nhnacademy.taskapi.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final TaskTagRepository taskTagRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    @Override
    public List<TagResponse> getTagsByProjectId(Long projectId, String userId) {
        checkUser(projectId,userId);

        List<Tag> tags = tagRepository.findAllByProject_Id(projectId);

        return tags.stream().map(
                tag -> TagResponse.builder()
                        .tagId(tag.getId()).name(tag.getName()).build()
        ).toList();
    }

    @Override
    @Transactional
    public TagResponse createTag(Long projectId, String userId, TagCreateRequest req) {
        checkUser(projectId,userId);

        Tag tag = Tag.builder()
                .name(req.name())
                .build();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new ProjectNotFoundException("프로젝트가 없음"));

        tag.setProject(project);

        Tag savedTag = tagRepository.save(tag);

        return TagResponse.builder()
                .tagId(savedTag.getId()).name(savedTag.getName()).build();

    }

    @Override
    @Transactional
    public TagResponse updateTag(Long projectId, String userId, Long tagId, TagUpdateRequest req) {
        checkUser(projectId,userId);

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(()-> new TagNotFoundException("태그를 찾을수 없습니다."));

        tag.setName(req.name());

        return TagResponse.builder()
                .tagId(tag.getId()).name(tag.getName()).build();
    }

    @Override
    @Transactional
    public void deleteTag(Long projectId, String userId, Long tagId) {
        checkUser(projectId,userId);

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(()-> new TagNotFoundException("태그를 찾을 수 없음"));
        taskTagRepository.deleteByTag_Id(tagId);
        tagRepository.delete(tag);
    }

    private void checkUser(Long projectId, String userId){
        if(!projectMemberRepository.existsByProject_IdAndUserId(projectId,userId)){
            throw new ProjectMemberNotFoundException("프로젝트 멤버가 아님");
        }
    }
}
