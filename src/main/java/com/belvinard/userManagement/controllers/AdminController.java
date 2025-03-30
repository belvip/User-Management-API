package com.belvinard.userManagement.controllers;


import com.belvinard.userManagement.model.User;
import com.belvinard.userManagement.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Endpoints pour l'administration")
public class AdminController {
    private final UserService userService;

    @GetMapping("/getusers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer tous les utilisateurs",
            description = "Retourne la liste de tous les utilisateurs enregistrés.")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
