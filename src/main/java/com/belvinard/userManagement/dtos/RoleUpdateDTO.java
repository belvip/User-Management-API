package com.belvinard.userManagement.dtos;

import jakarta.validation.constraints.NotBlank;

public class RoleUpdateDTO {
    @NotBlank(message = "Le nom du rôle est requis")
    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
