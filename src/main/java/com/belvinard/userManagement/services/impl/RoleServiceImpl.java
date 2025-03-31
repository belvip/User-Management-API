package com.belvinard.userManagement.services.impl;

import com.belvinard.userManagement.dtos.UserRoleDTO;
import com.belvinard.userManagement.model.User;
import com.belvinard.userManagement.repositories.RoleRepository;
import com.belvinard.userManagement.repositories.UserRepository;
import com.belvinard.userManagement.services.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    private final UserRepository userRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserRoleDTO> getAllUserRoles() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserRoleDTO(user.getUserId(), user.getUserName(),
                        user.getRole().getRoleName().name()))
                .collect(Collectors.toList());
    }

}
