package com.belvinard.userManagement.services;

import com.belvinard.userManagement.dtos.UserRoleDTO;

import java.util.List;

public interface RoleService {
    List<UserRoleDTO> getAllUserRoles();
}
