package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.AcceptanceCriteriaRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.AcceptanceCriteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter for AcceptanceCriteria repository
 */
@Component
@RequiredArgsConstructor
public class AcceptanceCriteriaRepositoryAdapter implements AcceptanceCriteriaRepositoryPort {

    private final AcceptanceCriteriaRepository jpaRepository;

    @Override
    public AcceptanceCriteria save(AcceptanceCriteria acceptanceCriteria) {
        return jpaRepository.save(acceptanceCriteria);
    }

    @Override
    public List<AcceptanceCriteria> saveAll(List<AcceptanceCriteria> acceptanceCriteriaList) {
        return jpaRepository.saveAll(acceptanceCriteriaList);
    }

    @Override
    public Optional<AcceptanceCriteria> findById(UUID acceptanceCriteriaId) {
        return jpaRepository.findById(acceptanceCriteriaId);
    }

    @Override
    public List<AcceptanceCriteria> findByUserStoryId(UUID userStoryId) {
        return jpaRepository.findByUserStory_UserStoryId(userStoryId);
    }

    @Override
    public List<AcceptanceCriteria> findByUserStoryIdOrderByOrderNo(UUID userStoryId) {
        return jpaRepository.findByUserStory_UserStoryIdOrderByOrderNoAsc(userStoryId);
    }

    @Override
    public void deleteById(UUID acceptanceCriteriaId) {
        jpaRepository.deleteById(acceptanceCriteriaId);
    }

    @Override
    public void deleteByUserStoryId(UUID userStoryId) {
        jpaRepository.deleteByUserStory_UserStoryId(userStoryId);
    }

    @Override
    public boolean existsById(UUID acceptanceCriteriaId) {
        return jpaRepository.existsById(acceptanceCriteriaId);
    }
}
