package com.belvinard.userManagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Management API")
                        .version("1.0")
                        .description("""
                                Cette API permet de gérer les utilisateurs d'un système de gestion. Elle offre diverses fonctionnalités pour administrer les utilisateurs, leur rôle et leur sécurité.
                                
                                ### Fonctionnalités :
                                - Créer, récupérer, mettre à jour et supprimer des utilisateurs
                                - Assigner des rôles aux utilisateurs
                                - Gestion de la sécurité via JWT pour l'authentification
                                - Réponses d'erreur détaillées et explicites
                                
                                ### Utilisateurs par défaut :
                                Lors du lancement de l'application, deux utilisateurs par défaut sont créés pour faciliter les tests :
                                1. **admin** (Rôle : ADMIN)
                                    - **Username** : admin
                                    - **Email** : admin@example.com
                                    - **Role** : ROLE_ADMIN
                                    - **Password** : P@ssword123
                                2. **user1** (Rôle : USER)
                                    - **Username** : user1
                                    - **Email** : user1@example.com
                                    - **Role** : ROLE_USER
                                    - **Password** : password1
                                
                                ### Technologies utilisées :
                                - **Spring Boot** pour le développement du backend
                                - **JPA & Hibernate** pour l'interaction avec la base de données
                                - **Swagger/OpenAPI** pour la documentation de l'API
                                - **Spring Security** pour la gestion des rôles et de la sécurité
                                - **Lombok** pour réduire le code boilerplate
                                
                                Cette API est conçue pour être utilisée par des développeurs souhaitant gérer les utilisateurs dans leurs applications, tout en garantissant une intégration facile grâce à des mécanismes de sécurité avancés.
                                """))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
