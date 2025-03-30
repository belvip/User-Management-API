package com.belvinard.userManagement.services;

import com.belvinard.userManagement.dtos.SignupRequest;
import com.belvinard.userManagement.model.AppRole;
import com.belvinard.userManagement.model.Role;
import com.belvinard.userManagement.model.User;
import com.belvinard.userManagement.repositories.RoleRepository;
import com.belvinard.userManagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
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
}
