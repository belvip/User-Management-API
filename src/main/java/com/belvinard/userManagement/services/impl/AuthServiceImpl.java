package com.belvinard.userManagement.services.impl;

import com.belvinard.userManagement.dtos.SignupRequest;
import com.belvinard.userManagement.dtos.UpdateUserRequest;
import com.belvinard.userManagement.dtos.UserDTO;
import com.belvinard.userManagement.exceptions.APIException;
import com.belvinard.userManagement.exceptions.ResourceNotFoundException;
import com.belvinard.userManagement.model.AppRole;
import com.belvinard.userManagement.model.Role;
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

    @Override
    public User registerUser(SignupRequest request) {
        // Vérifier si le nom d'utilisateur existe déjà
        if (userRepository.existsByUserName(request.getUsername())) {
            throw new APIException("Erreur : Ce nom d'utilisateur est déjà pris !");
        }

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new APIException("Erreur : Cet email est déjà utilisé !");
        }

        // Trouver le rôle USER (on ignore la valeur envoyée dans la requête)
        Role role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new APIException("Erreur : Rôle USER introuvable !"));

        // Créer un nouvel utilisateur avec le rôle USER par défaut
        User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);

        // Sauvegarder l'utilisateur
        return userRepository.save(user);
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


//    @Transactional
//    @Override
//    public UserDTO updateUser(Long userId, UserDTO updatedUserDTO) {
//        // Vérifier si l'utilisateur existe
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));
//
//        // Logs pour debugger
//        System.out.println("Utilisateur trouvé : " + user.getUserName());
//
//        // Mettre à jour uniquement les champs autorisés
//        if (updatedUserDTO.getUserName() != null) {
//            System.out.println("Mise à jour du nom d'utilisateur : " + updatedUserDTO.getUserName());
//            user.setUserName(updatedUserDTO.getUserName());
//        }
//        if (updatedUserDTO.getEmail() != null) {
//            System.out.println("Mise à jour de l'email : " + updatedUserDTO.getEmail());
//            user.setEmail(updatedUserDTO.getEmail());
//        }
//        if (updatedUserDTO.getPassword() != null) {
//            System.out.println("Mise à jour du mot de passe");
//            user.setPassword(passwordEncoder.encode(updatedUserDTO.getPassword()));
//        }
//
//        // Sauvegarder les modifications
//        userRepository.save(user);
//
//        // Logs pour debugger
//        System.out.println("Utilisateur mis à jour : " + user.getUserName());
//
//        // Retourner l'utilisateur mis à jour sous forme de DTO
//        return modelMapper.map(user, UserDTO.class);
//    }

//    @Transactional
//    @Override
//    public UserDTO updateUser(Long userId, UserDTO updatedUserDTO) {
//        // Vérifier si l'utilisateur existe
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));
//
//        // Logs pour debugger
//        System.out.println("Utilisateur trouvé : " + user.getUserName());
//
//        // Mettre à jour uniquement les champs autorisés
//        if (updatedUserDTO.getUserName() != null) {
//            System.out.println("Mise à jour du nom d'utilisateur : " + updatedUserDTO.getUserName());
//            user.setUserName(updatedUserDTO.getUserName());
//        }
//        if (updatedUserDTO.getEmail() != null) {
//            System.out.println("Mise à jour de l'email : " + updatedUserDTO.getEmail());
//            user.setEmail(updatedUserDTO.getEmail());
//        }
//        if (updatedUserDTO.getPassword() != null) {
//            System.out.println("Mise à jour du mot de passe");
//            user.setPassword(passwordEncoder.encode(updatedUserDTO.getPassword()));
//        }
//
//        // Sauvegarder les modifications
//        userRepository.save(user);
//
//        // Logs pour debugger
//        System.out.println("Utilisateur mis à jour : " + user.getUserName());
//
//        // Retourner l'utilisateur mis à jour sous forme de DTO
//        return modelMapper.map(user, UserDTO.class);
//    }


//    @Transactional
//    @Override
//    public UserDTO updateUser(Long userId, UpdateUserRequest request) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));
//
//        if (request.getUserName() != null) {
//            user.setUserName(request.getUserName());
//        }
//        if (request.getEmail() != null) {
//            user.setEmail(request.getEmail());
//        }
//        if (request.getPassword() != null) {
//            user.setPassword(passwordEncoder.encode(request.getPassword()));
//        }
//
//        userRepository.save(user);
//        return modelMapper.map(user, UserDTO.class);
//    }



}
