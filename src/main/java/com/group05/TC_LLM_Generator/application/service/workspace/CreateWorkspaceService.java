package com.group05.TC_LLM_Generator.application.service.workspace;

import java.util.UUID;

import com.group05.TC_LLM_Generator.application.port.in.workspace.CreateWorkspaceUseCase;
import com.group05.TC_LLM_Generator.domain.model.entity.Workspace;

import org.springframework.stereotype.Service;

@Service
public class CreateWorkspaceService implements CreateWorkspaceUseCase {

    @Override
    public Workspace execute(UUID userId, String name, String description) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

}
