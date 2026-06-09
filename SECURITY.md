# Solutions techniques - Sécurité

Ce document présente les solutions techniques de sécurité mises en place dans le projet DataShare : 

* Gestion des mots de passe.

* Contrôle des fichiers uploadés.

* Authentification JWT.

* Externalisation des données sensibles.

---

#### Sommaire

- [1/ Mots de passe](#1-mots-de-passe)
  - [1.1 Mot de passe utilisateur](#11-mot-de-passe-utilisateur)
  - [1.2 Mot de passe de fichier](#12-mot-de-passe-de-fichier)
  - [1.3 Implémentation — BCrypt](#13-implémentation--bcrypt)
- [2/ Fichiers](#2-fichiers)
  - [2.1 Validation front-end (upload)](#21-validation-front-end-upload)
  - [2.2 Validation back-end](#22-validation-back-end)
- [3/ Authentification JWT](#3-authentification-jwt)
  - [3.1 Fonctionnement](#31-fonctionnement)
  - [3.2 Génération du token](#32-generation-du-token)
  - [3.3 Validation du token](#33-validation-du-token)
  - [3.4 Configuration des routes](#34-configuration-des-routes)
- [4/ Données sensibles](#4-donnees-sensibles)

---

3.2 Génération du token

## 1 Mots de passe

### 1.1 Mot de passe utilisateur

Le mot de passe est encodé à l'inscription via la classe `PasswordEncoder` (Spring Security).

### 1.2 Mot de passe de fichier

Le mot de passe est encodé à l'upload et vérifié par le back-end au moment du téléchargement.

### 1.3 Implémentation — BCrypt

- Interface utilisée : `PasswordEncoder` → implémentation `BCryptPasswordEncoder`.
- Algorithme : Blowfish (hachage + salage aléatoire).
- Propriété : impossible à décrypter, on peut seulement vérifier qu'il correspond au hash en base de données.

```java
// SpringSecurityConfig.java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

## 2/ Fichiers

Protection contre les attaques de type **Remote Code Execution (RCE)** : les fichiers exécutables (`.jar`, `.exe`, `.sh`, etc.) ne sont pas autorisés.

> Un attaquant ne peut pas exécuter de code malveillant sur le serveur via un fichier uploadé.

### 2.1 Validation front-end (upload)

Vérification de l'extension côté client avant envoi.

### 2.2 Validation back-end

1. **Apache Tika** — détecte le type MIME réel du fichier (ex : vérifie qu'un `.pdf` est bien un PDF et non un exécutable renommé par exemple).
2. **Filtre de type entrant** — liste blanche des types MIME autorisés :

```java
private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
    "image/jpeg", "image/png", "image/gif", "image/svg+xml", "image/webp",
    "video/mp4", "video/x-matroska",
    "audio/mpeg", "audio/wav", "audio/ogg", "audio/flac", "audio/aac", "audio/mp4",
    "application/pdf", "text/plain",
    "application/zip", "application/x-tar"
);
```

---

## 3/ Authentification JWT

Le projet utilise une authentification **sans état (stateless)** basée sur des tokens JWT (JSON Web Token).

### 3.1 Fonctionnement

1. L'utilisateur s'authentifie (login) → le back-end génère un token signé.
2. Le token est envoyé dans le header `Authorization: Bearer <token>` à chaque requête qui à besoin d'être authentifiées.
3. Un filtre Spring (`JwtAuthenticationFilter`) intercepte chaque requête et valide le token.

### 3.2 Generation du token

Service JwtService.java

- Le token contient : le nom d'utilisateur (`subject`), la date d'émission, la date d'expiration.
- Signé avec HMAC-SHA via une clé secrète encodée en Base64

```java
Jwts.builder()
    .subject(userDetails.getUsername())
    .issuedAt(new Date())
    .expiration(new Date(System.currentTimeMillis() + expiration))
    .signWith(this.getSigningKey())
    .compact();
```

### 3.3 Validation du token

- La signature est vérifiée avec la clé secrète (`.verifyWith(getSigningKey())`).
  Une `JwtException` est levée si elle est invalide ou falsifiée.

- Le nom d'utilisateur dans le token correspond à l'utilisateur en base

- Le token n'est pas expiré

### 3.4 Configuration des routes

SpringSecurityConfig.java

| Route                        | Accès                                  |
| ---------------------------- | -------------------------------------- |
| `POST /api/users/**`         | Public (inscription / login)           |
| `POST /api/files`            | Public (upload anonyme possible)       |
| `GET /api/files/*`           | Public (Voir métadonnées d'un fichier) |
| `POST /api/files/download/*` | Public (téléchargement d'un fichier)   |
| Toutes les autres routes     | Authentifié (JWT requis)               |

---

## 4 Donnees sensibles

Les credentials (BDD, clé JWT) sont externalisés dans un fichier `.env` non commité, chargé au démarrage via `PropertySourcesPlaceholderConfigurer`.

Un fichier `.env.example` est versionné dans le dépôt comme documentation des variables attendues.

**Variables concernées**

```
jwt.secret=<clé Base64>
jwt.expiration=<durée en ms>
DB_URL=...
DB_USERNAME=...
DB_PASSWORD=...
```

**En production**

Le fichier `.env` sera remplacé par des variables d'environnement injectées directement par l'infrastructure (Docker, Kubernetes, plateforme CI/CD).
