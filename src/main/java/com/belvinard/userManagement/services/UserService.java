package com.belvinard.userManagement.services;

import com.belvinard.userManagement.dtos.UserRoleDTO;
import com.belvinard.userManagement.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User updateUser(Long userId, User userToUpdate);

    void deleteUser(Long userId);

    User getUserById(Long userId);


}
