package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.TestSuiteRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuite;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.TestSuiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestSuiteRepositoryAdapter implements TestSuiteRepositoryPort {

    private final TestSuiteRepository jpaRepository;

    @Override
    public TestSuite save(TestSuite testSuite) {
        return jpaRepository.save(testSuite);
    }

    @Override
    public Optional<TestSuite> findById(UUID testSuiteId) {
        // Use JOIN FETCH to eagerly load project — prevents LazyInitializationException
        return jpaRepository.findByIdWithProject(testSuiteId);
    }

    @Override
    public List<TestSuite> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<TestSuite> findAll(Pageable pageable) {
        // Use JOIN FETCH to eagerly load project
        return jpaRepository.findAllWithProject(pageable);
    }

    @Override
    public List<TestSuite> findByProjectId(UUID projectId) {
        // Use JOIN FETCH to eagerly load project
        return jpaRepository.findByProject_ProjectIdWithProject(projectId);
    }

    @Override
    public Page<TestSuite> findByProjectId(UUID projectId, Pageable pageable) {
        // Use JOIN FETCH to eagerly load project
        return jpaRepository.findByProject_ProjectIdWithProject(projectId, pageable);
    }

    @Override
    public void deleteById(UUID testSuiteId) {
        jpaRepository.deleteById(testSuiteId);
    }

    @Override
    public boolean existsById(UUID testSuiteId) {
        return jpaRepository.existsById(testSuiteId);
    }
}
