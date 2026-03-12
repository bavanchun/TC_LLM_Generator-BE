package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.AcceptanceCriteriaRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application Service for AcceptanceCriteria entity
 * Handles CRUD operations and acceptance criteria-related use cases
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcceptanceCriteriaService {

    private final AcceptanceCriteriaRepositoryPort acceptanceCriteriaRepository;

    /**
     * Create a new acceptance criteria
     */
    @Transactional
    public AcceptanceCriteria createAcceptanceCriteria(AcceptanceCriteria acceptanceCriteria) {
        return acceptanceCriteriaRepository.save(acceptanceCriteria);
    }

    /**
     * Save all acceptance criteria (batch)
     */
    @Transactional
    public List<AcceptanceCriteria> saveAll(List<AcceptanceCriteria> acceptanceCriteriaList) {
        return acceptanceCriteriaRepository.saveAll(acceptanceCriteriaList);
    }

    /**
     * Get acceptance criteria by ID
     */
    public Optional<AcceptanceCriteria> getAcceptanceCriteriaById(UUID acceptanceCriteriaId) {
        return acceptanceCriteriaRepository.findById(acceptanceCriteriaId);
    }

    /**
     * Get acceptance criteria by user story ID
     */
    public List<AcceptanceCriteria> getByUserStoryId(UUID userStoryId) {
        return acceptanceCriteriaRepository.findByUserStoryId(userStoryId);
    }

    /**
     * Get acceptance criteria by user story ID ordered by orderNo
     */
    public List<AcceptanceCriteria> getByUserStoryIdOrdered(UUID userStoryId) {
        return acceptanceCriteriaRepository.findByUserStoryIdOrderByOrderNo(userStoryId);
    }

    /**
     * Update acceptance criteria
     */
    @Transactional
    public AcceptanceCriteria updateAcceptanceCriteria(UUID acceptanceCriteriaId, AcceptanceCriteria updated) {
        AcceptanceCriteria existing = acceptanceCriteriaRepository.findById(acceptanceCriteriaId)
                .orElseThrow(() -> new IllegalArgumentException("Acceptance criteria not found: " + acceptanceCriteriaId));

        if (updated.getContent() != null) {
            existing.setContent(updated.getContent());
        }
        if (updated.getOrderNo() != null) {
            existing.setOrderNo(updated.getOrderNo());
        }
        if (updated.getCompleted() != null) {
            existing.setCompleted(updated.getCompleted());
        }

        return acceptanceCriteriaRepository.save(existing);
    }

    /**
     * Delete acceptance criteria by ID
     */
    @Transactional
    public void deleteAcceptanceCriteria(UUID acceptanceCriteriaId) {
        if (!acceptanceCriteriaRepository.existsById(acceptanceCriteriaId)) {
            throw new IllegalArgumentException("Acceptance criteria not found: " + acceptanceCriteriaId);
        }
        acceptanceCriteriaRepository.deleteById(acceptanceCriteriaId);
    }

    /**
     * Delete all acceptance criteria for a user story
     */
    @Transactional
    public void deleteByUserStoryId(UUID userStoryId) {
        acceptanceCriteriaRepository.deleteByUserStoryId(userStoryId);
    }

    /**
     * Check if acceptance criteria exists
     */
    public boolean existsById(UUID acceptanceCriteriaId) {
        return acceptanceCriteriaRepository.existsById(acceptanceCriteriaId);
    }
}
