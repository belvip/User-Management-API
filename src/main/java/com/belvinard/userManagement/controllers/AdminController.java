package com.belvinard.userManagement.controllers;

import com.belvinard.userManagement.dtos.UserDTO;
import com.belvinard.userManagement.model.User;
import com.belvinard.userManagement.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Admin Management", description = "Endpoints for administrative operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all registered users. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user list"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - valid JWT token missing"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - requires ADMIN role"
                    )
            }
    )
    @GetMapping("/getusers")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @Operation(
            summary = "Update user role",
            description = "Updates the role of a specific user. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated user role"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request - invalid role name or user ID"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - valid JWT token missing"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - requires ADMIN role"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    @PutMapping("/update-role")
    public ResponseEntity<String> updateUserRole(
            @Parameter(description = "ID of the user to update", required = true, example = "1")
            @RequestParam Long userId,

            @Parameter(description = "New role name (ROLE_USER or ROLE_ADMIN)", required = true, example = "ROLE_ADMIN")
            @RequestParam String roleName) {

        userService.updateUserRole(userId, roleName);
        return ResponseEntity.ok("User role updated");
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves details of a specific user. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user details"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - valid JWT token missing"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - requires ADMIN role"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUser(
            @Parameter(description = "ID of the user to retrieve", required = true, example = "1")
            @PathVariable Long id) {

        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }
}