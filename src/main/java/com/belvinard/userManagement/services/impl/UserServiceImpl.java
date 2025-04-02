package com.belvinard.userManagement.services.impl;

import com.belvinard.userManagement.dtos.Response;
import com.belvinard.userManagement.dtos.UserDTO;
import com.belvinard.userManagement.dtos.UserResponse;
import com.belvinard.userManagement.exceptions.APIException;
import com.belvinard.userManagement.exceptions.ResourceNotFoundException;
import com.belvinard.userManagement.model.AppRole;
import com.belvinard.userManagement.model.Role;
import com.belvinard.userManagement.model.User;

import com.belvinard.userManagement.repositories.RoleRepository;
import com.belvinard.userManagement.repositories.UserRepository;
import com.belvinard.userManagement.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    private final   ModelMapper modelMapper;
    private final RoleRepository roleRepository;


    @Override
    public UserResponse getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserDTO> userDTOS = users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();

        UserResponse userResponse = new UserResponse();
        userResponse.setContent(userDTOS);
        return userResponse;

        //return userRepository.findAll();
    }


    @Override
    public UserDTO deleteUser(Long userId) {
        // Trouver l'utilisateur dans la base de données
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));

        // Convertir l'entité User en UserDTO (si tu veux retourner UserDTO après la suppression, sinon ce n'est pas nécessaire)
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        // Optionnel : Afficher ou utiliser userDTO si nécessaire (par exemple, pour loguer ou retourner une réponse)
        System.out.println("User to delete: " + userDTO);

        // Supprimer l'utilisateur
        userRepository.delete(user);
        return userDTO;
    }

    @Override
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));

        // Mapper l'entité User vers le DTO UserDTO
        return modelMapper.map(user, UserDTO.class);
    }


    //@Transactional
    @Override
    @Transactional
    public UserDTO updateUserRole(Long userId, String roleName) {
        // Vérifier si l'utilisateur existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));

        // Normaliser le nom du rôle
        String formattedRoleName = "ROLE_" + roleName.toUpperCase();

        // Debugging
        System.out.println("Formatted Role: " + formattedRoleName);

        // Vérifier si le rôle est valide
        Optional<AppRole> appRole = Arrays.stream(AppRole.values())
                .filter(role -> role.name().equals(formattedRoleName))
                .findFirst();

        if (appRole.isEmpty()) {
            throw new APIException("Erreur : Rôle invalide !");
        }

        // Trouver le rôle en base de données
        Role role = roleRepository.findByRoleName(appRole.get())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "RoleName", formattedRoleName));

        // Mettre à jour le rôle de l'utilisateur
        user.setRole(role);
        userRepository.save(user);

        return modelMapper.map(user, UserDTO.class);
    }


    // Gestion de l'exception ResourceNotFoundException
    @ExceptionHandler(APIException.class)
    public ResponseEntity<Response> myAPIException(APIException ex) {
        // Créer l'instance d'ErrorResponse
        Response errorResponse = new Response("BAD_REQUEST", ex.getMessage());

        // Retourner la réponse avec le code HTTP 404
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


}
