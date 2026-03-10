package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.ProjectMemberRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectMemberService {

    private final ProjectMemberRepositoryPort projectMemberRepository;

    @Transactional
    public ProjectMember addMember(Project project, UserEntity user, String role) {
        ProjectMember member = ProjectMember.builder()
                .project(project)
                .user(user)
                .role(role)
                .joinedAt(Instant.now())
                .build();
        return projectMemberRepository.save(member);
    }

    public Optional<ProjectMember> getProjectMemberById(UUID projectMemberId) {
        return projectMemberRepository.findById(projectMemberId);
    }

    public List<ProjectMember> getAllProjectMembers() {
        return projectMemberRepository.findAll();
    }

    public Page<ProjectMember> getAllProjectMembers(Pageable pageable) {
        return projectMemberRepository.findAll(pageable);
    }

    public List<ProjectMember> getProjectMembersByProject(UUID projectId) {
        return projectMemberRepository.findByProjectId(projectId);
    }

    public Page<ProjectMember> getProjectMembersByProject(UUID projectId, Pageable pageable) {
        return projectMemberRepository.findByProjectId(projectId, pageable);
    }

    public Optional<ProjectMember> getByProjectIdAndUserId(UUID projectId, UUID userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
    }

    @Transactional
    public ProjectMember updateProjectMember(UUID projectMemberId, ProjectMember updatedMember) {
        ProjectMember existing = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Project member not found: " + projectMemberId));

        if (updatedMember.getRole() != null) {
            existing.setRole(updatedMember.getRole());
        }

        return projectMemberRepository.save(existing);
    }

    @Transactional
    public void removeMember(UUID projectMemberId) {
        if (!projectMemberRepository.existsById(projectMemberId)) {
            throw new IllegalArgumentException("Project member not found: " + projectMemberId);
        }
        projectMemberRepository.deleteById(projectMemberId);
    }

    public boolean projectMemberExists(UUID projectMemberId) {
        return projectMemberRepository.existsById(projectMemberId);
    }
}
