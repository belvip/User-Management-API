package com.belvinard.userManagement.controllers;

import com.belvinard.userManagement.dtos.*;
import com.belvinard.userManagement.exceptions.APIException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    // Gestion de l'exception ResourceNotFoundException
    @ExceptionHandler(APIException.class)
    public ResponseEntity<Response> myAPIException(APIException ex) {
        // Créer l'instance d'ErrorResponse
        Response errorResponse = new Response("BAD_REQUEST", ex.getMessage());

        // Retourner la réponse avec le code HTTP 404
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Récupérer les détails de l'utilisateur connecté", security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Utilisateur non authentifié");
        }
        return ResponseEntity.ok(userDetails);
    }

//    @Operation(summary = "Met à jour un utilisateur",
//          description = "Modifie les informations d'un utilisateur existant sans modifier son rôle.")
//    @PutMapping("/update-user/{userId}")
//    public ResponseEntity<UserDTO> updateUser(
//            @PathVariable Long userId,
//            @Valid @RequestBody UpdateUserRequest request) {  // Valide directement la Request
//
//        UserDTO updatedUser = authService.updateUser(userId, request); // Passe la request directement
//        return ResponseEntity.ok(updatedUser);
//    }


    @Operation(
            summary = "Met à jour un utilisateur",
            description = "Modifie les informations d'un utilisateur existant sans modifier son rôle."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide ou données de mise à jour incorrectes"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {

        UserDTO updatedUser = authService.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }


    // ✅ Gérer l'exception directement dans le contrôleur
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> ((FieldError) error).getField() + " : "
                        + error.getDefaultMessage())
                .collect(Collectors.toList());

        ValidationErrorResponse response = new ValidationErrorResponse("VALIDATION_ERROR", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


}
