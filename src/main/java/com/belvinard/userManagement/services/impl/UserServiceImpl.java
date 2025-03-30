package com.belvinard.userManagement.services.impl;

import com.belvinard.userManagement.model.User;

import com.belvinard.userManagement.repositories.UserRepository;
import com.belvinard.userManagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
