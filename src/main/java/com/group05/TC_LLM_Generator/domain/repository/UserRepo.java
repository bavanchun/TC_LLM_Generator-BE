package com.group05.TC_LLM_Generator.domain.repository;

import com.group05.TC_LLM_Generator.domain.model.entity.User;
import java.util.Optional;

public interface UserRepo {
    Optional<User> findByEmail(String email);

    User save(User user);
}
