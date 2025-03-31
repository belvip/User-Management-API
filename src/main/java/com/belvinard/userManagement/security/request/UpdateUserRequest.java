package com.belvinard.userManagement.security.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String userName;
    private String email;
    private String password;
    private Long roleId; // Pour mettre à jour le rôle si nécessaire
}
