package com.belvinard.userManagement.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête d'inscription d'un nouvel utilisateur")
public class SignupRequest {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 20, message = "Le nom d'utilisateur doit contenir entre 3 et 20 caractères")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Le nom d'utilisateur ne peut contenir que des lettres, chiffres et underscores")
    @Schema(
            description = "Nom d'utilisateur unique",
            example = "john_doe",
            minLength = 3,
            maxLength = 20,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Size(max = 50, message = "L'email ne peut excéder 50 caractères")
    @Email(message = "Doit être une adresse email valide")
    @Schema(
            description = "Adresse email valide",
            example = "john.doe@example.com",
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, max = 40, message = "Le mot de passe doit contenir entre 6 et 40 caractères")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$",
            message = "Le mot de passe doit contenir au moins 1 majuscule, 1 minuscule, 1 chiffre et 1 caractère spécial"
    )
    @Schema(
            description = "Mot de passe sécurisé",
            example = "P@ssword123",
            minLength = 6,
            maxLength = 40,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}