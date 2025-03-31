package com.belvinard.userManagement.controllers;


import com.belvinard.userManagement.model.Role;
import com.belvinard.userManagement.model.User;
import com.belvinard.userManagement.repositories.RoleRepository;
import com.belvinard.userManagement.security.request.UpdateUserRequest;
import com.belvinard.userManagement.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/getusers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer tous les utilisateurs",
            description = "Retourne la liste de tous les utilisateurs enregistrés.")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Met à jour un utilisateur", description = "Modifie les informations d'un utilisateur existant.")
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

    @Operation(summary = "Supprime un utilisateur", description = "Supprime un utilisateur par son ID.")
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("Utilisateur supprimé avec succès.");
    }
}
