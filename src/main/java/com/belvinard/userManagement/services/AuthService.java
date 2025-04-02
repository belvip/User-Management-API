package com.belvinard.userManagement.services;

import com.belvinard.userManagement.dtos.SignupRequest;
import com.belvinard.userManagement.dtos.UpdateUserRequest;
import com.belvinard.userManagement.dtos.UserDTO;
import jakarta.validation.Valid;

public interface AuthService {
    UserDTO registerUser(@Valid SignupRequest request);

    UserDTO updateUser(Long userId, UpdateUserRequest request);
}
