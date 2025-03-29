package com.belvinard.userManagement.services;

import com.belvinard.userManagement.dtos.UserDTO;
import com.belvinard.userManagement.model.User;

import java.util.List;

public interface UserService {
    void updateUserRole(Long userId, String roleName);

    List<User> getAllUsers();

    UserDTO getUserById(Long id);

    User findByUsername(String username);
}
