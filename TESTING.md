# Plan de test — DataShare

- Back-end Spring Boot 

- Front-end Angular

## 1. Tests unitaires — Back-end

**Framework de test :** JUnit 5 
**Framework de mock :** Mockito 5

### 1.1 FileService

| Action    | Cas                                                                                                                 |
| --------- | ------------------------------------------------------------------------------------------------------------------- |
| UPLOAD    | Le bon déroulement de l'upload ->   Validation taille, detection du type de fichier, sauvegarde, sauvegarde en BDD. |
| GET       | Si pas de mot de passe venant du front, la variable `hasPassword` est à false.                                      |
| DOWNLOAD  | Succès du téléchargement quand le mot de passe est correct.                                                         |
| GET FILES | Le système retourne bien une liste de fichiers.                                                                     |

### 1.2 UserService

| **Action** | **Cas**                                                                                               |
| ---------- | ----------------------------------------------------------------------------------------------------- |
| REGISTER   | Erreur `IllegalArgumentException` quand l'email existe déja.                                          |
| REGISTER   | Le bon déroulement de l'enregistrement d'un utilisateur.                                              |
| LOGIN      | Le bon déroulement de la connexion d'un utilisateur connu en base de données, il reçoit un token JWT. |
| LOGIN      | Un mauvais mot de passe lance une exception de type `BadCredentialsException`.                        |

## 2. Tests d'intégration — Back-end

Les tests d'intégration utilisent Testcontainers, qui lance automatiquement une image Docker PostgreSQL le temps de l'exécution des tests, garantissant un environnement isolé et proche de la production.

### 2.1 UserController

| Route                 | Action   | Cas                                      |
| --------------------- | -------- | ---------------------------------------- |
| POST /api/users       | REGISTER | Inscription OK → 201 (CREATED)           |
| POST /api/users       | REGISTER | Manque infos -> erreur 400 (BAD_REQUEST) |
| POST /api/users       | REGISTER | email déjà existant → 400 (BAD_REQUEST)  |
| POST /api/users/login | LOGIN    | Identifiants corrects → 200 (OK)         |
| POST /api/users/login | LOGIN    | Manque infos -> 400 (BAD REQUEST)        |

### 2.2 FileController

| Route                          | Action   | Cas                                                                                    |
| ------------------------------ | -------- | -------------------------------------------------------------------------------------- |
| POST /api/files                | UPLOAD   | Téléversement ok → 201 (CREATED)                                                       |
| POST /api/files                | UPLOAD   | Fichier supérieur à 1 giga → 413 (FileSizeExceededException)                           |
| POST /api/files                | UPLOAD   | Mime-type non autorisé → 415 (InvalidFileTypeException)                                |
| POST /api/files                | UPLOAD   | Enregistrement erreur -> 503 (Service unvailaible)                                     |
| GET /api/files/{uuid}          | GET_FILE | Uuid d'un fichier inconnu → 404 (NOT_FOUND)                                            |
| GET /api/files/download/{uuid} | DOWNLOAD | Mauvais mot de passe → 403 (FORBIDDEN)                                                 |
| DELETE /api/files/{uuid}       | DELETE   | Effacement ok → 204 (NO_CONTENT)                                                       |
| DELETE /api/files/{uuid}       | DELETE   | Un utilisateur essaye d'effacer un fichier qui ne lui appartient pas → 403 (FORBIDDEN) |
| DELETE /api/files/{uuid}       | DELETE   | Uuid du fichier inconnu → 404 (NOT_FOUND)                                              |

### 3.Tests unitaire - Front-end (ANGULAR)

**Framework de test unitaire JavaScript:** Jest

## 3.1 Guard

| ACTION        | CAS                                                                                                                    |
| ------------- | ---------------------------------------------------------------------------------------------------------------------- |
| AUTHORISATION | L'utilisateur est connecté -> les routes protégées par un guard sont accessibles.                                      |
| REDIRECTION   | L'utilisateur est redirigé vers la route `/login` si la route est protégée et que l'utilisateur n'est pas authentifié. |

## 3.2 Interceptor

| ACTION      | CAS                                                                                        |
| ----------- | ------------------------------------------------------------------------------------------ |
| REDIRECTION | Interception les erreurs 401 (NON_AUTHORISE) venant du Back-end et redirige vers `/login`. |

## 3.3 FileService

| ACTION    | CAS                                                            |
| --------- | -------------------------------------------------------------- |
| CREATION  | La classe à bien été instanciée.                               |
| GET_FILES | Le service renvoi bien une liste de fichier.                   |
| UPLOAD    | Le service reçoit un status 201 (CREATED) aprés téléversement. |

## 3.4 UserService

| ACTION   | CAS                                                            |
| -------- | -------------------------------------------------------------- |
| CREATION | La classe à bien été instanciée.                               |
| REGISTER | L'enregistrement d'un utilisateur reçois bien un 201 (CREATED) |
| LOGIN    | L'authentification d'un utilisateur reçois bien un JWT token   |
| LOGIN    | Un mauvais password reçois une erreur 401 (UNAUTHORIZED)       |

## 3.5 Download composant

| ACTION   | CAS                                                 |
| -------- | --------------------------------------------------- |
| CREATION | Composant / FileResponseDTO / Loading `false`       |
| EXPIRER  | Verifie que la date d'expiration est bien calculée. |

## 3.6 Upload composant

| ACTION     | CAS                                                                            |
| ---------- | ------------------------------------------------------------------------------ |
| CREATION   | Composant correctement créé.                                                   |
| SIZE       | Un fichier de deux mégaOctet affiche bien `Mo`.                                |
| NOM        | Le nom du fichier est coupé par `...` quand il est trop long pour l'affichage. |
| EXPIRATION | Nombre de jour restant est bien affiché. (test avec 1 et 7)                    |
| TAILLE     | Si fichier plus de 1 giga, affichage d'une erreur.                             |
| UPLOAD     | Test sur le glisser de fichier dans la zone d'upload.                          |
| UPLOAD     | Test sur le glisser de fichier pour selection et erreur taille.                |
| PASSWORD   | Erreur si moins de 6 caractères                                                |
| UPLOAD     | Affichage du lien de partage si ok et erreur sinon                             |

## 3.7 Home composant

| ACTION   | CAS                          |
| -------- | ---------------------------- |
| CREATION | Composant correctement créé. |

## 3.8 Login composant

| ACTION   | CAS                                             |
| -------- | ----------------------------------------------- |
| CREATION | Composant correctement créé.                    |
| LOGIN    | Enregistrement du token et redirection vers `/` |

## 3.9 Register composant

| ACTION   | CAS                                                                 |
| -------- | ------------------------------------------------------------------- |
| CREATION | Composant correctement créé.                                        |
| REGISTER | Requête `POST` / body avec email et password / status 201 (CREATED) |

## 3.10 Space composant

| ACTION   | CAS                           |
| -------- | ----------------------------- |
| CREATION | Composant correctement créé.  |
| FILES    | Affiche les fichiers.         |
| FILTER   | Filtre tous / actif / expiré. |

## 4. Tests end-to-end (e2e) — Front-end (Angular)

**Outil de test end-to-end (e2e) :** Cypress

### 4.1 Enregistrement et authentification d'un utilisateur

 register_and_login.cy.ts

1. Enregistrement d'un utilisateur.

2. Authentification avec un mauvais mot de passe.

3. Authentification avec un bon mot de passe.

## 4.2 Chaîne de vie d'un fichier

upload_see_ddl_and_delete_file_with_authenticated_user.cy.ts

**L'utilisateur est authentifié.**

1. Téléversement d'un fichier.
2. Affichage du fichier dans son espace.
3. Téléchargement du fichier.
4. Effacement du fichier.



### 5. Lancement des tests Back-end :

Ouvrir un terminal dans le dossier du Back-end :

`mvn clean test` -> Nettoyage et lancement des tests



=> ouverture de la page .html du rapport Jacoco dans le dossier du Back-end:

*.../target/site/jacoco/index.html*



### 6. Lancement des tests unitaire Front-end :

Ouvrir un terminal dans le dossier du Front-end :

`npm test`

La couverture des tests s'affiche.

### 7. Lancement des tests fonctionnels (e2e) Front-end :

## 7.1 Avec interface :

Ouvrir un terminal dans le dossier du Front-end :

`npx cypress open`



## 7.2 Lancer la couverture des tests fonctionnels (e2e) Front-end

Ouvrir un terminal dans le dossier du Front-end :

1. Lancement du server en mode instrumenté : 
   `ng run front-end_dataShare:serve-coverage`

2. Lancement de chaque scénario : 
   `npx cypress run --spec "cypress/e2e/register_and_login.cy.ts"`
   `npx cypress run --spec "cypress/e2e/upload_see_ddl_and_delete_file_with_authenticated_user.cy.ts"`

3. Lancement et affichage du rapport de test: 

    `npx nyc report --reporter=text-summary`
