
package com.sreyarose.medicalshop.infrastructure.mapper;

import com.sreyarose.medicalshop.domain.entity.User;
import com.sreyarose.medicalshop.infrastructure.persistence.UserJpaEntity;

public class UserMapper {

    private UserMapper() {
    }

    public static User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                entity.getFullName(),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.isActive()
        );
    }

    public static UserJpaEntity toJpaEntity(User user) {
        return new UserJpaEntity(
                user.getFullName(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getRole(),
                user.isActive()
        );
    }
}
