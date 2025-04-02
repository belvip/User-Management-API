package com.belvinard.userManagement.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @NotBlank(message = "Le nom d'utilisateur est requis")
    @Size(min = 3, max = 20, message = "Le nom d'utilisateur doit contenir entre 3 et 20 caractères")
    private String userName;

//    @NotBlank(message = "L'email est requis")
//    @Email(message = "L'email doit être valide")
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

//    @Size(min = 6, max = 40, message = "Le mot de passe doit contenir entre 6 et 40 caractères")
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