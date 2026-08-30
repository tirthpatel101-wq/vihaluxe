package com.vihaluxe.service;

import com.vihaluxe.dto.UserRegistrationDto;
import com.vihaluxe.model.User;
import com.vihaluxe.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(UserRegistrationDto registrationDto) {

        User user = new User();

        user.setFullName(registrationDto.getFullName());
        user.setEmail(registrationDto.getEmail());

        // Encrypt password
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        user.setRole("ROLE_USER");

        return userRepository.save(user);
    }
}