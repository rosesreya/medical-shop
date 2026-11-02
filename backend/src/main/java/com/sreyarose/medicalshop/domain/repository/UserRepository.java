
package com.sreyarose.medicalshop.domain.repository;

import com.sreyarose.medicalshop.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    boolean existsByUsername(String username);
}
