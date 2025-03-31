package com.belvinard.userManagement.services;

import com.belvinard.userManagement.dtos.UserDTO;
import com.belvinard.userManagement.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User updateUser(Long userId, User userToUpdate);

    void deleteUser(Long userId);

    UserDTO getUserById(Long userId);


}
