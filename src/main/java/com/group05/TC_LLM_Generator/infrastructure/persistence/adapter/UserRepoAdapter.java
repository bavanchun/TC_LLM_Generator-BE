package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.domain.model.entity.User;
import com.group05.TC_LLM_Generator.domain.repository.UserRepo;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.mapper.UserMapper;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepoAdapter implements UserRepo {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity savedEntity = userRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }
}
