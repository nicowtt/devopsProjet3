# Plan de test (DataShare)

Ce document décrit l'ensemble de la stratégie de test de l'application DataShare. 

Il couvre : 

* Les **tests unitaires** du Back-end (JUnit 5 / Mockito) et du Front-end (Jest). 

* Les **tests d'intégration** du Back-end avec une base de données PostgreSQL isolée (Testcontainers). 

* Les **tests end-to-end** (Cypress) simulant les parcours utilisateur complets. 

Pour chaque test, le document précise l'action testée, le cas couvert et le comportement attendu. 

Les procédures de lancement sont détaillées en fin de document.

---

## Sommaire

- [1. Tests unitaires - Back-end](#1-tests-unitaires-back-end)
  - [1.1 UserService](#11-userservice-4-tests)
  - [1.2 FileService](#12-fileservice-4-tests)
- [2. Tests integration - Back-end](#2-tests-integration-back-end)
  - [2.1 UserController](#21-usercontroller-5-tests)
  - [2.2 FileController](#22-filecontroller-9-tests)
- [3. Tests unitaires - Front-end](#3-tests-unitaires-front-end-angular)
  - [3.1 Guard](#31-guard-2-tests)
  - [3.2 Interceptor](#32-interceptor-1-test)
  - [3.3 UserService](#33-userservice-4-tests)
  - [3.4 FileService](#34-fileservice-3-tests)
  - [3.5 Download composant](#35-download-composant-2-tests)
  - [3.6 Upload composant](#36-upload-composant-9-tests)
  - [3.7 Home composant](#37-home-composant-1-test)
  - [3.8 Login composant](#38-login-composant-2-tests)
  - [3.9 Register composant](#39-register-composant-2-tests)
  - [3.10 Space composant](#310-space-composant-3-tests)
- [4. Tests fonctionnels end-to-end - Front-end](#4-tests-fonctionnels-end-to-end-front-end-)
  - [4.1 Enregistrement et authentification](#41-enregistrement-et-authentification-dun-utilisateur)
  - [4.2 Cycle de vie d'un fichier](#42-cycle-de-vie-dun-fichier)
- [5. Lancement des tests Back-end](#5-lancement-des-tests-back-end)
- [6. Lancement des tests unitaires Front-end](#6-lancement-des-tests-unitaires-front-end)
- [7. Lancement des tests fonctionnels e2e](#7-lancement-des-tests-fonctionnels-e2e-front-end)
  - [7.1 Avec interface graphique](#71-avec-interface-graphique)
  - [7.2 Sans interface graphique](#72-sans-interface-graphique)
  - [7.3 Couverture des tests e2e](#73-lancer-la-couverture-des-tests-fonctionnels-e2e-front-end)

---

## 1. Tests unitaires - Back-end

**Framework de test :** JUnit 5 
**Framework de mock :** Mockito 5

### 1.1 UserService (4 tests)

| **Action** | **Cas**                                                                                               |
| ---------- | ----------------------------------------------------------------------------------------------------- |
| REGISTER   | Erreur `IllegalArgumentException` quand l'email existe déjà.                                          |
| REGISTER   | Le bon déroulement de l'enregistrement d'un utilisateur.                                              |
| LOGIN      | Le bon déroulement de la connexion d'un utilisateur connu en base de données, il reçoit un token JWT. |
| LOGIN      | Un mauvais mot de passe lance une exception de type `BadCredentialsException`.                        |

### 1.2 FileService (4 tests)

| Action    | Cas                                                                                                                 |
| --------- | ------------------------------------------------------------------------------------------------------------------- |
| UPLOAD    | Le bon déroulement de l'upload ->   Validation taille, detection du type de fichier, sauvegarde, sauvegarde en BDD. |
| GET       | Si pas de mot de passe venant du front, la variable `hasPassword` est à false.                                      |
| DOWNLOAD  | Succès du téléchargement quand le mot de passe est correct.                                                         |
| GET FILES | Le système retourne bien une liste de fichiers.                                                                     |

### 2. Tests integration - Back-end

Les tests d'intégration utilisent Testcontainers, qui lance automatiquement une image Docker PostgreSQL le temps de l'exécution des tests, garantissant un environnement isolé et proche de la production.

### 2.1 UserController (5 tests)

| Route                 | Action   | Cas                                      |
| --------------------- | -------- | ---------------------------------------- |
| POST /api/users       | REGISTER | Inscription OK → 201 (CREATED)           |
| POST /api/users       | REGISTER | Manque infos -> erreur 400 (BAD_REQUEST) |
| POST /api/users       | REGISTER | email déjà existant → 400 (BAD_REQUEST)  |
| POST /api/users/login | LOGIN    | Identifiants corrects → 200 (OK)         |
| POST /api/users/login | LOGIN    | Manque infos -> 400 (BAD REQUEST)        |

### 2.2 FileController (9 tests)

| Route                          | Action   | Cas                                                                                    |
| ------------------------------ | -------- | -------------------------------------------------------------------------------------- |
| POST /api/files                | UPLOAD   | Téléversement ok → 201 (CREATED)                                                       |
| POST /api/files                | UPLOAD   | Fichier supérieur à 1 giga → 413 (FileSizeExceededException)                           |
| POST /api/files                | UPLOAD   | Mime-type non autorisé → 415 (InvalidFileTypeException)                                |
| POST /api/files                | UPLOAD   | Enregistrement erreur -> 503 (Service unavailaible)                                    |
| GET /api/files/{uuid}          | GET_FILE | Uuid d'un fichier inconnu → 404 (NOT_FOUND)                                            |
| GET /api/files/download/{uuid} | DOWNLOAD | Mauvais mot de passe → 403 (FORBIDDEN)                                                 |
| DELETE /api/files/{uuid}       | DELETE   | Effacement ok → 204 (NO_CONTENT)                                                       |
| DELETE /api/files/{uuid}       | DELETE   | Un utilisateur essaye d'effacer un fichier qui ne lui appartient pas → 403 (FORBIDDEN) |
| DELETE /api/files/{uuid}       | DELETE   | Uuid du fichier inconnu → 404 (NOT_FOUND)                                              |

### 3. Tests unitaires - Front-end ANGULAR

**Framework de test unitaire JavaScript:** Jest

## 3.1 Guard (2 tests)

| ACTION        | CAS                                                                                                                    |
| ------------- | ---------------------------------------------------------------------------------------------------------------------- |
| AUTHORIZATION | L'utilisateur est connecté -> les routes protégées par un guard sont accessibles.                                      |
| REDIRECTION   | L'utilisateur est redirigé vers la route `/login` si la route est protégée et que l'utilisateur n'est pas authentifié. |

## 3.2 Interceptor (1 test)

| ACTION      | CAS                                                                                      |
| ----------- | ---------------------------------------------------------------------------------------- |
| REDIRECTION | Intercepte les erreurs 401 (NON_AUTHORISE) venant du Back-end et redirige vers `/login`. |

## 3.3 UserService (4 tests)

| ACTION   | CAS                                                            |
| -------- | -------------------------------------------------------------- |
| CREATION | La classe a bien été instanciée.                               |
| REGISTER | L'enregistrement d'un utilisateur reçoit bien un 201 (CREATED) |
| LOGIN    | L'authentification d'un utilisateur reçoit bien un JWT token   |
| LOGIN    | Un mauvais password reçoit une erreur 401 (UNAUTHORIZED)       |

## 3.4 FileService (3 tests)

| ACTION    | CAS                                                            |
| --------- | -------------------------------------------------------------- |
| CREATION  | La classe a bien été instanciée.                               |
| GET_FILES | Le service renvoie bien une liste de fichiers.                 |
| UPLOAD    | Le service reçoit un status 201 (CREATED) après téléversement. |

## 3.5 Download composant (2 tests)

| ACTION     | CAS                                                 |
| ---------- | --------------------------------------------------- |
| CREATION   | Composant / FileResponseDTO / Loading `false`       |
| EXPIRATION | Vérifie que la date d'expiration est bien calculée. |

## 3.6 Upload composant (9 tests)

| ACTION     | CAS                                                                            |
| ---------- | ------------------------------------------------------------------------------ |
| CREATION   | Composant correctement créé.                                                   |
| SIZE       | Un fichier de deux mégaoctets affiche bien `Mo`.                               |
| SIZE       | Si fichier plus de 1 giga, affichage d'une erreur.                             |
| NAME       | Le nom du fichier est coupé par `...` quand il est trop long pour l'affichage. |
| EXPIRATION | Nombre de jours restants est bien affiché. (test avec 1 et 7)                  |
| UPLOAD     | Test sur le glisser de fichier dans la zone d'upload.                          |
| UPLOAD     | Test sur le glisser de fichier pour selection et erreur taille.                |
| UPLOAD     | Affichage du lien de partage si ok et erreur sinon                             |
| PASSWORD   | Erreur si moins de 6 caractères                                                |

## 3.7 Home composant (1 test)

| ACTION   | CAS                          |
| -------- | ---------------------------- |
| CREATION | Composant correctement créé. |

## 3.8 Login composant (2 tests)

| ACTION   | CAS                                             |
| -------- | ----------------------------------------------- |
| CREATION | Composant correctement créé.                    |
| LOGIN    | Enregistrement du token et redirection vers `/` |

## 3.9 Register composant (2 tests)

| ACTION   | CAS                                                                 |
| -------- | ------------------------------------------------------------------- |
| CREATION | Composant correctement créé.                                        |
| REGISTER | Requête `POST` / body avec email et password / status 201 (CREATED) |

## 3.10 Space composant (3 tests)

| ACTION   | CAS                           |
| -------- | ----------------------------- |
| CREATION | Composant correctement créé.  |
| FILES    | Affiche les fichiers.         |
| FILTER   | Filtre tous / actif / expiré. |

## 4. Tests fonctionnels end-to-end - Front-end

**Outil de test :** Cypress

### 4.1 Enregistrement et authentification d'un utilisateur

 register_and_login.cy.ts

1. Enregistrement d'un utilisateur.

2. Authentification avec un mauvais mot de passe.

3. Authentification avec un bon mot de passe.

## 4.2 Cycle de vie d'un fichier

upload_see_ddl_and_delete_file_with_authenticated_user.cy.ts

**L'utilisateur est authentifié.**

1. Téléversement d'un fichier.
2. Affichage du fichier dans l'espace utilisateur.
3. Téléchargement du fichier.
4. Effacement du fichier.

### 5. Lancement des tests Back-end

Ouvrir un terminal dans le dossier du Back-end :

`mvn clean test` -> Nettoyage et lancement des tests

=> ouverture de la page .html du rapport Jacoco dans le dossier du Back-end:

*.../target/site/jacoco/index.html*

### 6. Lancement des tests unitaires Front-end

Ouvrir un terminal dans le dossier du Front-end :

`npm test`

La couverture des tests s'affiche.

### 7. Lancement des tests fonctionnels e2e Front-end

## 7.1 Avec interface graphique

Ouvrir un terminal dans le dossier du Front-end :

`npx cypress open`

### 7.2 Sans interface graphique

Ouvrir un terminal dans le dossier du Front-end :

`npx cypress run`

## 7.3 Lancer la couverture des tests fonctionnels e2e Front-end

Ouvrir un terminal dans le dossier du Front-end :

1. Lancement du serveur en mode instrumenté : 
   `ng run front-end_dataShare:serve-coverage`

2. Lancement de chaque scénario : 
   `npx cypress run --spec "cypress/e2e/register_and_login.cy.ts"`
   `npx cypress run --spec "cypress/e2e/upload_see_ddl_and_delete_file_with_authenticated_user.cy.ts"`

3. Lancement et affichage du rapport de test: 
   
    `npx nyc report --reporter=text-summary`
