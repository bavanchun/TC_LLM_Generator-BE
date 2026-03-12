package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AcceptanceCriteriaRepositoryPort {

    AcceptanceCriteria save(AcceptanceCriteria acceptanceCriteria);

    List<AcceptanceCriteria> saveAll(List<AcceptanceCriteria> acceptanceCriteriaList);

    Optional<AcceptanceCriteria> findById(UUID acceptanceCriteriaId);

    List<AcceptanceCriteria> findByUserStoryId(UUID userStoryId);

    List<AcceptanceCriteria> findByUserStoryIdOrderByOrderNo(UUID userStoryId);

    void deleteById(UUID acceptanceCriteriaId);

    void deleteByUserStoryId(UUID userStoryId);

    boolean existsById(UUID acceptanceCriteriaId);
}
