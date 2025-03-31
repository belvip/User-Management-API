package com.belvinard.userManagement.controllers;


import com.belvinard.userManagement.dtos.Response;
import com.belvinard.userManagement.dtos.UserDTO;
import com.belvinard.userManagement.dtos.UserRoleDTO;
import com.belvinard.userManagement.exceptions.ResourceNotFoundException;
import com.belvinard.userManagement.model.Role;
import com.belvinard.userManagement.model.User;
import com.belvinard.userManagement.repositories.RoleRepository;
import com.belvinard.userManagement.security.request.UpdateUserRequest;
import com.belvinard.userManagement.services.RoleService;
import com.belvinard.userManagement.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Endpoints pour l'administration")
public class AdminController {
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    @GetMapping("/getusers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer tous les utilisateurs",
            description = "Retourne la liste de tous les utilisateurs enregistrés.")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Met à jour un utilisateur",
            description = "Modifie les informations d'un utilisateur existant.")
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {

        User userToUpdate = new User();
        userToUpdate.setUserName(request.getUserName());
        userToUpdate.setEmail(request.getEmail());
        if (request.getPassword() != null) {
            userToUpdate.setPassword(request.getPassword()); // Le hash sera fait dans le service
        }

        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID: " + request.getRoleId()));
            userToUpdate.setRole(role);
        }

        User updatedUser = userService.updateUser(userId, userToUpdate);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Supprime un utilisateur",
            description = "Supprime un utilisateur par son ID.")
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("Utilisateur supprimé avec succès.");
    }

//    @Operation(summary = "Récupérer un utilisateur",
//            description = "Récupère les détails d'un utilisateur par son ID.")
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
//        User user = userService.getUserById(userId);
//        return ResponseEntity.ok(user);
//    }
        @Operation(summary = "Récupérer un utilisateur",
                description = "Récupère les détails d'un utilisateur par son ID.")
        @GetMapping("/user/{userId}")
        public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
            UserDTO userDTO = userService.getUserById(userId);
            return ResponseEntity.ok(userDTO);
        }

    // Gestion de l'exception ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response> handleResourceNotFoundException(ResourceNotFoundException ex) {
        // Créer l'instance d'ErrorResponse
        Response errorResponse = new Response("NOT_FOUND", ex.getMessage());

        // Retourner la réponse avec le code HTTP 404
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    @Operation(summary = "Récupérer tous les rôles et les utilisateurs associés",
            description = "Retourne la liste des utilisateurs avec leurs rôles.")
    @GetMapping("/roles")
    public ResponseEntity<List<UserRoleDTO>> getAllUserRoles() {
        List<UserRoleDTO> userRoles = roleService.getAllUserRoles();
        return ResponseEntity.ok(userRoles);
    }


}
