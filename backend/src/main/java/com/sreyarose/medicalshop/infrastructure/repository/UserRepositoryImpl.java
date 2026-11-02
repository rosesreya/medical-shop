
package com.sreyarose.medicalshop.infrastructure.repository;

import com.sreyarose.medicalshop.domain.entity.User;
import com.sreyarose.medicalshop.domain.repository.UserRepository;
import com.sreyarose.medicalshop.infrastructure.mapper.UserMapper;
import com.sreyarose.medicalshop.infrastructure.persistence.UserJpaEntity;
import com.sreyarose.medicalshop.infrastructure.persistence.UserJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity;

        if (user.getId() == null) {
            // Create a new user.
            entity = UserMapper.toJpaEntity(user);
        } else {
            // Update an existing user.
            entity = jpaRepository.findById(user.getId())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "User not found during update"));

            entity.setFullName(user.getFullName());
            entity.setUsername(user.getUsername());
            entity.setPasswordHash(user.getPasswordHash());
            entity.setRole(user.getRole());
            entity.setActive(user.isActive());
        }

        UserJpaEntity savedEntity = jpaRepository.save(entity);

        return UserMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }

        return jpaRepository.findByUsername(
                        username.trim().toLowerCase())
                .map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(UserMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null) {
            return false;
        }

        return jpaRepository.existsByUsername(
                username.trim().toLowerCase());
    }
}
