package com.belvinard.userManagement.controllers;


import com.belvinard.userManagement.dtos.*;
import com.belvinard.userManagement.exceptions.APIException;
import com.belvinard.userManagement.exceptions.ResourceNotFoundException;
import com.belvinard.userManagement.repositories.RoleRepository;
import com.belvinard.userManagement.services.RoleService;
import com.belvinard.userManagement.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Endpoints pour l'administration")
public class AdminController {
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    //======================= GET ALL USERS
    @Operation(
            summary = "Récupérer tous les utilisateurs",
            description = """
        Retourne la liste complète des utilisateurs enregistrés dans la base de données.
        Seuls les administrateurs authentifiés peuvent accéder à cette ressource.
    """,
            security = @SecurityRequirement(name = "bearerAuth")
            //tags = {"Utilisateurs"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des utilisateurs récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(value = """
            {
                "totalUsers": 3,
                "users": [
                    {
                        "id": 1,
                        "username": "admin",
                        "email": "admin@example.com",
                        "roles": ["ROLE_USER"]
                    },
                    {
                        "id": 2,
                        "username": "jane_smith",
                        "email": "whiskey29@whiskey.com",
                        "roles": ["ROLE_ADMIN"]
                    }
                ]
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès refusé - seuls les administrateurs peuvent voir la liste des utilisateurs",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
            {
                "code": "ACCESS_DENIED",
                "message": "Vous n'avez pas l'autorisation d'accéder à cette ressource"
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
            {
                "code": "INTERNAL_SERVER_ERROR",
                "message": "Une erreur est survenue lors de la récupération des utilisateurs"
            }
            """)
                    )
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getusers")
    public ResponseEntity<UserResponse> getAllUsers() {
        UserResponse userResponse = userService.getAllUsers();
        return ResponseEntity.ok(userResponse);
    }


    //======================= DELETE USER
    @Operation(
            summary = "Supprimer un utilisateur",
            description = """
        Supprime un utilisateur en fonction de son ID.
        Seuls les administrateurs authentifiés peuvent effectuer cette opération.
    """,
            security = @SecurityRequirement(name = "bearerAuth")
            //tags = {"Utilisateurs"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur supprimé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(value = """
            {
                "id": 5,
                "username": "user_to_delete",
                "email": "user@example.com",
                "roles": ["ROLE_USER"]
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès refusé - seuls les administrateurs peuvent supprimer un utilisateur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
            {
                "code": "ACCESS_DENIED",
                "message": "Vous n'avez pas l'autorisation de supprimer cet utilisateur"
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
            {
                "code": "USER_NOT_FOUND",
                "message": "L'utilisateur avec l'ID spécifié n'existe pas"
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
            {
                "code": "INTERNAL_SERVER_ERROR",
                "message": "Une erreur est survenue lors de la suppression de l'utilisateur"
            }
            """)
                    )
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long userId) {
        UserDTO deletedUserDTO = userService.deleteUser(userId);
        return ResponseEntity.ok(deletedUserDTO);
    }


    //======================= GET USER BY ID
    @Operation(
            summary = "Récupérer un utilisateur",
            description = """
        Permet de récupérer les informations d'un utilisateur en fournissant son ID.
        Seuls les administrateurs authentifiés accéder à cette ressource.
    """,
            security = @SecurityRequirement(name = "bearerAuth")
            //tags = {"Utilisateurs"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur trouvé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(value = """
            {
                "id": 1,
                "username": "johndoe",
                "email": "johndoe@example.com",
                "roles": ["ROLE_USER"]
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès refusé - l'utilisateur n'a pas les permissions nécessaires",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
            {
                "code": "ACCESS_DENIED",
                "message": "Vous n'avez pas l'autorisation d'accéder à cet utilisateur"
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
            {
                "code": "USER_NOT_FOUND",
                "message": "Aucun utilisateur trouvé avec l'ID fourni"
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
            {
                "code": "INTERNAL_SERVER_ERROR",
                "message": "Une erreur est survenue lors du traitement de la requête"
            }
            """)
                    )
            )
    })
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ROLE_ADMIN')")
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

    //======================= GET ALL USER ROLE
    @Operation(
            summary = "Récupérer tous les rôles et les utilisateurs associés",
            description = """
        Retourne la liste de tous les utilisateurs avec leurs rôles respectifs.
        Seuls les administrateurs peuvent accéder à cette ressource.
    """,
            security = @SecurityRequirement(name = "bearerAuth")
            //tags = {"Rôles et Utilisateurs"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des utilisateurs et leurs rôles récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRoleDTO.class),
                            examples = @ExampleObject(value = """
            [
                {
                    "id": 1,
                    "username": "admin",
                    "email": "admin@example.com",
                    "roles": ["ROLE_ADMIN", "ROLE_USER"]
                },
                {
                    "id": 2,
                    "username": "johndoe",
                    "email": "johndoe@example.com",
                    "roles": ["ROLE_USER"]
                }
            ]
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès refusé - l'utilisateur n'a pas les permissions nécessaires",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
            {
                "code": "ACCESS_DENIED",
                "message": "Vous n'avez pas l'autorisation d'accéder à cette ressource"
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
            {
                "code": "INTERNAL_SERVER_ERROR",
                "message": "Une erreur est survenue lors du traitement de la requête"
            }
            """)
                    )
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/roles")
    public ResponseEntity<List<UserRoleDTO>> getAllUserRoles() {
        List<UserRoleDTO> userRoles = roleService.getAllUserRoles();
        return ResponseEntity.ok(userRoles);
    }


    //======================= UPDATE USER ROLE

    @Operation(
            summary = "Mettre à jour le rôle d'un utilisateur",
            description = """
        Permet à un administrateur de modifier le rôle d'un utilisateur.
        Seuls les utilisateurs ayant le rôle ADMIN peuvent effectuer cette action.
        L'utilisateur cible doit exister dans la base de données, et le rôle fourni doit être valide.
    """,
            security = @SecurityRequirement(name = "bearerAuth")
            //tags = {"Utilisateurs"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rôle mis à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(value = """
            {
                "id": 1,
                "username": "admin",
                "email": "admine@example.com",
                "roles": ["ROLE_ADMIN"]
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requête invalide (ex : rôle non valide ou champ manquant)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
            {
                "error": "BAD_REQUEST",
                "message": "Le rôle fourni est invalide"
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès refusé - l'utilisateur n'a pas les permissions nécessaires",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
            {
                "code": "ACCESS_DENIED",
                "message": "Vous n'avez pas l'autorisation de modifier ce rôle"
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur ou rôle non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
            {
                "code": "NOT_FOUND",
                "message": "Utilisateur ou rôle introuvable"
            }
            """)
                    )
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/user/{userId}/role")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request
    ) {
        String roleName = request.get("roleName");
        UserDTO updatedUser = userService.updateUserRole(userId, roleName);
        return ResponseEntity.ok(updatedUser);
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
