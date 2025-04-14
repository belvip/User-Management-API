# API de gestion des utilisateurs

Ce projet implémente une API de gestion des utilisateurs avec des fonctionnalités d'authentification et de gestion des utilisateurs. L'API permet de créer, mettre à jour, et authentifier des utilisateurs, ainsi que de gérer leurs rôles.

## Prérequis

Avant de commencer, assurez-vous que vous avez installé les éléments suivants sur votre machine :

- **Java 21 ou supérieur** - pour compiler et exécuter l'application.
- **Maven** -
- pour gérer les dépendances et construire le projet.
- **Base de données** - Postgres ou autre base de données relationnelle configurée pour l'application.

## Lancer l'API

### Étape 1 : Cloner le dépôt

Cloner le projet depuis GitHub en utilisant la commande suivante :

```bash
git clone git@github.com:belvip/User-Management-API.git
cd ton-repository
```
# Guide de Configuration et d'Utilisation de l'API

## Étape 2 : Configurer la base de données
Avant de démarrer l'API, configurez votre base de données en modifiant le fichier `application.properties` ou `application.yml` dans le dossier `src/main/resources`.

### Exemple pour MySQL :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/user_db
spring.datasource.username=postgres
spring.datasource.password=belvi
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

## Étape 3 : Construire et démarrer l'application
Exécutez les commandes suivantes pour construire le projet et démarrer l'application :

```bash
mvn clean install
mvn spring-boot:run
```

Cela démarrera l'API sur le port par défaut `8083`. Vous pouvez le modifier dans le fichier `application.properties` :

```properties
server.port=8083
```

## Étape 4 : Vérifier que l'API fonctionne
Une fois l'API lancée, vous pouvez tester les points de terminaison (endpoints) en accédant à http://localhost:8083/swagger-ui.html (assurez-vous que Swagger est activé pour explorer l'API).

## Tester l'authentification

### Étape 1 : S'inscrire en tant que nouvel utilisateur

Utilisez l'endpoint **POST /api/auth/register** pour créer un nouvel utilisateur avec les informations suivantes :

```json
{
  "username": "user1",
  "email": "user1@example.com",
  "password": "password1"
}
```
### Réponse attendue

Si l'inscription est réussie, vous recevrez une réponse avec le message suivant :

```json
{
  "message": "Utilisateur créé avec succès"
}
```

### Étape 2 : Authentification de l'utilisateur

Une fois inscrit, vous pouvez vous authentifier en envoyant une requête **POST /api/auth/login** avec les informations suivantes :

```json
{
  "username": "user1",
  "password": "password1"
}
```

### Étape 2 : Authentification de l'utilisateur

Si l'authentification est réussie, vous recevrez un JWT Token dans la réponse :

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "user1",
  "email": "user1@example.com",
  "role": "USER"
}
```
### Étape 3 : Utiliser le token JWT pour l'accès aux endpoints protégés

Après avoir obtenu le token JWT, vous pouvez l'utiliser pour accéder aux endpoints protégés de l'API. Incluez le token dans l'en-tête `Authorization` de vos requêtes comme suit :

```bash
Authorization: Bearer <votre_token_jwt>
```

Par exemple, pour mettre à jour le profil de l'utilisateur, envoyez une requête `PUT /api/users/{id}` avec les informations suivantes :

```json
{
  "username": "user1_updated",
  "email": "user1_updated@example.com"
}
```

### Réponse attendue

Si l'utilisateur est mis à jour avec succès, vous recevrez une réponse avec les nouvelles informations de l'utilisateur :

```json
{
  "id": 1,
  "username": "user1_updated",
  "email": "user1_updated@example.com",
  "roles": ["ROLE_USER"]
}
```

## Points de terminaison de l'API

### Authentification

- **POST /api/auth/register** : Inscription d'un nouvel utilisateur.
- **POST /api/auth/login** : Authentification d'un utilisateur et obtention du token JWT.

### Utilisateurs

- **GET /api/users/{id}** : Obtenir les informations d'un utilisateur par son ID.
- **PUT /api/users/{id}** : Mettre à jour les informations d'un utilisateur.
- **DELETE /api/users/{id}** : Supprimer un utilisateur.

## Sécurisation de l'API

L'API utilise **JWT** (JSON Web Tokens) pour sécuriser les endpoints. Vous devez inclure le token JWT dans l'en-tête `Authorization` de toutes les requêtes qui nécessitent une authentification.

## Conclusion

Cette API est conçue pour être utilisée dans un système de gestion des utilisateurs avec des fonctionnalités de création, mise à jour et authentification. Elle est sécurisée à l'aide de JWT et permet de gérer les utilisateurs de manière efficace.
