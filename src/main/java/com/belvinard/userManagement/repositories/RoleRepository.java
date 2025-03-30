package com.belvinard.userManagement.repositories;

import com.belvinard.userManagement.model.AppRole;
import com.belvinard.userManagement.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);

}
