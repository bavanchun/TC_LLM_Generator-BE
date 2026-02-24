package com.group05.TC_LLM_Generator.application.service.workspace;

import com.group05.TC_LLM_Generator.application.port.in.workspace.GetWorkspaceUseCase;
import com.group05.TC_LLM_Generator.domain.model.entity.Workspace;
import com.group05.TC_LLM_Generator.domain.repository.WorkspaceRepo;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetWorkspaceService implements GetWorkspaceUseCase {

    private final WorkspaceRepo workspaceRepo;

    @Override
    public List<Workspace> execute(UUID userId) {
        List<Workspace> workspaces = workspaceRepo.findAllByOwnerId(userId);

        if (workspaces.isEmpty()) {
            Workspace defaultWorkspace = Workspace.builder()
                    .ownerId(userId)
                    .name("Default Workspace")
                    .description("Auto-generated default workspace")
                    .build();
            workspaceRepo.save(defaultWorkspace);
            workspaces.add(defaultWorkspace);
        }

        return workspaces;
    }

}
