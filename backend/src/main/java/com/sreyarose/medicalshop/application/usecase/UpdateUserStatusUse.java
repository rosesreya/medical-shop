
package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.application.exception.ResourceNotFoundException;
import com.sreyarose.medicalshop.domain.entity.User;
import com.sreyarose.medicalshop.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserStatusUse {

    private final UserRepository userRepository;

    public UpdateUserStatusUse(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(Long userId, boolean active) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + userId
                        )
                );

        if (active) {
            user.activate();
        } else {
            user.deactivate();
        }

        return userRepository.save(user);
    }
}
