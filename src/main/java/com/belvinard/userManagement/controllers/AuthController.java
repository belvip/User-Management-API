package com.belvinard.userManagement.controllers;

import com.belvinard.userManagement.dtos.SignupRequest;
import com.belvinard.userManagement.model.User;
import com.belvinard.userManagement.repositories.UserRepository;
import com.belvinard.userManagement.security.jwt.JwtUtils;
import com.belvinard.userManagement.security.request.LoginRequest;
import com.belvinard.userManagement.security.response.JwtResponse;
import com.belvinard.userManagement.security.services.UserDetailsImpl;
import com.belvinard.userManagement.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints publics liés à l'authentification")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Operation(
        summary = "Créer un nouvel utilisateur",
        description = "Permet de créer un compte utilisateur avec un rôle spécifié (user ou admin)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Erreur de validation des données"),
        @ApiResponse(responseCode = "409", description = "Le nom d'utilisateur ou l'email existent déjà")
    })
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(
            @Parameter(description = "Données de l'utilisateur à créer", required = true)
            @Valid @RequestBody SignupRequest request) {
        User user = authService.registerUser(request);
        return ResponseEntity.ok("Utilisateur créé avec succès : " + user.getUserName());
    }

    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et renvoie un token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    })
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUserName(userDetails.getUsername()).orElseThrow();

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                "Bearer",
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getRole().getRoleName().name()
        ));
    }

    @Operation(summary = "Récupérer les détails de l'utilisateur connecté", security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Utilisateur non authentifié");
        }
        return ResponseEntity.ok(userDetails);
    }
}
