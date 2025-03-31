package com.belvinard.userManagement.services;

import com.belvinard.userManagement.dtos.UserDTO;
import com.belvinard.userManagement.dtos.UserResponse;
import com.belvinard.userManagement.model.User;

public interface UserService {
    UserResponse getAllUsers();

    User updateUser(Long userId, User userToUpdate);

    UserDTO deleteUser(Long userId);

    UserDTO getUserById(Long userId);


}
