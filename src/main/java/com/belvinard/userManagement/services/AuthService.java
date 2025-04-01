package com.belvinard.userManagement.services;

import com.belvinard.userManagement.dtos.SignupRequest;
import com.belvinard.userManagement.dtos.UserDTO;
import com.belvinard.userManagement.model.User;
import jakarta.validation.Valid;

public interface AuthService {
    User registerUser(@Valid SignupRequest request);


    UserDTO updateUser(Long userId, UserDTO updatedUserDTO);
}
