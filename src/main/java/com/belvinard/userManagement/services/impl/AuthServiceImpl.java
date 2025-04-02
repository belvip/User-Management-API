package com.belvinard.userManagement.services.impl;

import com.belvinard.userManagement.dtos.SignupRequest;
import com.belvinard.userManagement.dtos.UpdateUserRequest;
import com.belvinard.userManagement.dtos.UserDTO;
import com.belvinard.userManagement.exceptions.APIException;
import com.belvinard.userManagement.exceptions.ResourceNotFoundException;
import com.belvinard.userManagement.model.AppRole;
import com.belvinard.userManagement.model.User;
import com.belvinard.userManagement.repositories.RoleRepository;
import com.belvinard.userManagement.repositories.UserRepository;
import com.belvinard.userManagement.security.services.UserDetailsImpl;
import com.belvinard.userManagement.services.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;


    public UserDetailsImpl getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }

    @Transactional
    @Override
    public UserDTO registerUser(SignupRequest request) {
        // Vérifier si le nom d'utilisateur existe déjà
        if (userRepository.existsByUserName(request.getUsername())) {
            throw new APIException("Erreur : Ce nom d'utilisateur est déjà pris !");
        }

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new APIException("Erreur : Cet email est déjà utilisé !");
        }

        // Création de l'utilisateur
        User user = new User();
        user.setUserName(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new APIException("Erreur : Rôle USER introuvable !")));

        // Sauvegarde de l'utilisateur
        User savedUser = userRepository.save(user);

        // Conversion de `User` vers `UserDTO`
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Transactional
    @Override
    public UserDTO updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));

        // Utilisez directement les champs validés de la request
        if (request.getUserName() != null) {
            user.setUserName(request.getUserName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }



}