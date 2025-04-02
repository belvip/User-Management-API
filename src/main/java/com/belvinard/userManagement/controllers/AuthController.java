package com.belvinard.userManagement.controllers;

import com.belvinard.userManagement.dtos.*;
import com.belvinard.userManagement.exceptions.APIException;
import com.belvinard.userManagement.exceptions.CustomAccessDeniedException;
import com.belvinard.userManagement.exceptions.CustomUsernameNotFoundException;
import com.belvinard.userManagement.model.User;
import com.belvinard.userManagement.repositories.UserRepository;
import com.belvinard.userManagement.security.jwt.JwtUtils;
import com.belvinard.userManagement.security.request.LoginRequest;
import com.belvinard.userManagement.security.response.JwtResponse;
import com.belvinard.userManagement.security.services.UserDetailsImpl;
import com.belvinard.userManagement.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    //====================================  SIGNUP METHOD

    @Operation(
            summary = "Inscription d'un nouvel utilisateur",
            description = """
            Permet de créer un nouveau compte utilisateur avec le rôle `USER` par défaut. 
            Les mots de passe doivent respecter les critères suivants :
            - Minimum de 6 caractères
            - Doit contenir au moins une majuscule
            - Doit contenir au moins un chiffre
            - Doit contenir un caractère spécial
        """,
            tags = {"Authentification"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Utilisateur créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "userId": 1,
                                        "username": "admin",
                                        "email": "admin@example.com",
                                        "role": "USER"
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides - Par exemple, email mal formaté ou mot de passe non conforme",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "code": "VALIDATION_ERROR",
                                        "errors": [
                                            "email : Doit être une adresse email valide",
                                            "password : Le mot de passe doit contenir au moins 1 majuscule"
                                        ]
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflit - L'identifiant existe déjà dans la base de données",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "code": "USERNAME_EXISTS",
                                        "message": "Ce nom d'utilisateur est déjà utilisé"
                                    }
                                """
                            )
                    )
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> registerUser(
            @Valid @RequestBody SignupRequest request) {
        UserDTO userDTO = authService.registerUser(request);
        return ResponseEntity.ok(userDTO);
    }

    //==================================== SIGNING METHOD

    @Operation(
            summary = "Authentification utilisateur",
            description = """
            Authentifie un utilisateur et retourne un token JWT valide pour les requêtes suivantes.
            L'utilisateur doit fournir son nom d'utilisateur et son mot de passe pour obtenir un token d'authentification.
        """,
            tags = {"Authentification"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentification réussie - Le token JWT est renvoyé pour les requêtes suivantes",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JwtResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                        "type": "Bearer",
                                        "id": 1,
                                        "username": "user1",
                                        "email": "user1@example.com",
                                        "role": "USER"
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non autorisé - Les informations d'authentification sont incorrectes",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "code": "INVALID_CREDENTIALS",
                                        "message": "Nom d'utilisateur ou mot de passe incorrect"
                                    }
                                """
                            )
                    )
            )
    })

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getEmail()).orElseThrow();

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    "Bearer",
                    user.getUserId(),
                    user.getUserName(),
                    user.getEmail(),
                    user.getRole().getRoleName().name()
            ));
        } catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }




    //==================================== UPDATE USER

    @Operation(
            operationId = "updateUserEndpoint", // ID unique explicite
            summary = "Mettre à jour le profil utilisateur",
            description = """
            Met à jour les informations de l'utilisateur. L'utilisateur doit être authentifié pour effectuer cette action.
            L'endpoint permet de modifier les informations personnelles de l'utilisateur, telles que son nom, son email, etc.
        """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur mis à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "id": 1,
                                        "username": "user1",
                                        "email": "user1@example.com",
                                        "roles": ["ROLE_USER"]
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Entrée invalide - La requête contient des données incorrectes",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "code": "INVALID_INPUT",
                                        "message": "Les données envoyées sont invalides"
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Interdit - L'utilisateur n'a pas les permissions nécessaires pour effectuer cette action",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "code": "FORBIDDEN",
                                        "message": "Accès interdit. Vous n'avez pas les permissions nécessaires."
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur non trouvé - L'utilisateur spécifié n'existe pas",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "code": "USER_NOT_FOUND",
                                        "message": "L'utilisateur n'a pas été trouvé"
                                    }
                                """
                            )
                    )
            )
    })
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ROLE_ADMIN')")
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserDetailsImpl)) {
            throw new CustomAccessDeniedException("Utilisateur non valide ou non authentifié");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        if (!userDetails.getId().equals(userId) && !userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new CustomAccessDeniedException("Vous n'avez pas l'autorisation de modifier cet utilisateur.");
        }

        UserDTO updatedUser = authService.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleCustomAccessDeniedException(CustomAccessDeniedException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", "ACCESS_DENIED");
        errorResponse.put("message", ex.getMessage());

        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(CustomUsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(CustomUsernameNotFoundException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", "USER_NOT_FOUND");
        errorResponse.put("message", ex.getMessage());

        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }


    // Gérer l'exception directement dans le contrôleur
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

    // Gestion de l'exception ResourceNotFoundException
    @ExceptionHandler(APIException.class)
    public ResponseEntity<Response> myAPIException(APIException ex) {
        // Créer l'instance d'ErrorResponse
        Response errorResponse = new Response("BAD_REQUEST", ex.getMessage());

        // Retourner la réponse avec le code HTTP 404
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}