package com.vihaluxe.service;

import com.vihaluxe.dto.UserRegistrationDto;
import com.vihaluxe.model.User;

public interface UserService {

    User registerUser(UserRegistrationDto registrationDto);

}