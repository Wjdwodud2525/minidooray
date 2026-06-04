package com.nhnacademy.taskapi.service.impl;

import com.nhnacademy.taskapi.dto.project_member.ProjectMemberResponse;
import com.nhnacademy.taskapi.entity.ProjectMember;
import com.nhnacademy.taskapi.exception.allow.ex.ProjectNotAllowException;
import com.nhnacademy.taskapi.exception.notfound.ex.ProjectMemberNotFoundException;
import com.nhnacademy.taskapi.exception.notfound.ex.UserNotFoundException;
import com.nhnacademy.taskapi.repository.ProjectMemberRepository;
import com.nhnacademy.taskapi.service.AccountClientService;
import com.nhnacademy.taskapi.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectMemberServiceImpl implements ProjectMemberService {
    private final ProjectMemberRepository projectMemberRepository;
    private final AccountClientService accountClientService;
    @Override
    public List<ProjectMemberResponse> getMembers(Long projectId, String userId) {

        List<ProjectMember> members = projectMemberRepository.findAllByProject_Id(projectId);

        if(members.stream().noneMatch(member -> member.getUserId().equals(userId))){
            throw new ProjectNotAllowException("프로젝트 멤버가 아님");
        }
        return members.stream().map(member -> ProjectMemberResponse.builder()
                .userId(member.getUserId()).admin(member.isAdmin()).build()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectMemberResponse addMember(Long projectId, String userId, String newMemberId) {
        ProjectMember projectMember = projectMemberRepository.findByProject_IdAndUserId(projectId,userId)
                .orElseThrow(()-> new ProjectMemberNotFoundException("프로젝트 멤버가 아님"));
        if(!projectMember.isAdmin()){
            throw new ProjectNotAllowException("프로젝트 관리자만 멤버를 추가할 수 있습니다.");
        }
        if(!accountClientService.checkUserExists(newMemberId)){
            throw new UserNotFoundException(newMemberId+"추가할 멤버가 없음");
        }
        ProjectMember member = projectMemberRepository.save(
                ProjectMember.builder()
                        .project(projectMember.getProject()).userId(newMemberId).admin(false).build()
        );
        return ProjectMemberResponse.builder()
                .userId(member.getUserId()).admin(member.isAdmin()).build();
    }

    @Override
    public void deleteMember(Long projectId, String userId, String memberId) {
        ProjectMember member = projectMemberRepository.findByProject_IdAndUserId(projectId,userId)
                .orElseThrow(()-> new ProjectMemberNotFoundException("프로젝트 멤버가 아님"));
        if(!member.isAdmin()&& !member.getUserId().equals(memberId)){
            throw new ProjectNotAllowException("프로젝트 관리자나 본인만 삭제 가능");
        }
        ProjectMember member1 = projectMemberRepository.findByProject_IdAndUserId(projectId,memberId)
                .orElseThrow(()->new ProjectMemberNotFoundException("삭제할 멤버가 프로젝트 멤버가 아닙니다."));
        projectMemberRepository.delete(member1);
    }
}
