package com.group05.TC_LLM_Generator.application.port.in.workspace;

import java.util.UUID;

import com.group05.TC_LLM_Generator.domain.model.entity.Workspace;

public interface CreateWorkspaceUseCase {
    Workspace execute(UUID userId, String name, String description);
}
