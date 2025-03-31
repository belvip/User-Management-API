package com.belvinard.userManagement.services.impl;

import com.belvinard.userManagement.dtos.SignupRequest;
import com.belvinard.userManagement.model.AppRole;
import com.belvinard.userManagement.model.Role;
import com.belvinard.userManagement.model.User;
import com.belvinard.userManagement.repositories.RoleRepository;
import com.belvinard.userManagement.repositories.UserRepository;
import com.belvinard.userManagement.security.services.UserDetailsImpl;
import com.belvinard.userManagement.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(SignupRequest request) {
        // Vérifier si l'utilisateur ou l'email existent déjà
        if (userRepository.existsByUserName(request.getUsername())) {
            throw new RuntimeException("Erreur : Ce nom d'utilisateur est déjà pris !");
        }

        // Trouver le rôle
        Role role = roleRepository.findByRoleName(
                request.getRole().equalsIgnoreCase("admin") ? AppRole.ROLE_ADMIN : AppRole.ROLE_USER
        ).orElseThrow(() -> new RuntimeException("Erreur : Rôle introuvable !"));

        // Créer un nouvel utilisateur
        User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);

        // Sauvegarder l'utilisateur
        return userRepository.save(user);
    }

    public UserDetailsImpl getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }
}
