# DataShare

Datashare est un site internet permettant à un utilisateur d'uploader un fichier et de générer un lien de téléchargement unique et sécurisé, partageable avec n'importe quelle personne sans nécessiter de compte.

- L'utilisateur peut choisir la **durée de validité de ce lien de 1 à 7 jours**.
- Le fichier ne doit pas dépasser **1 gigaoctet**.
- Les types de fichiers autorisés sont :
  - **jpg, jpeg, png, gif, svg, bmp** pour les fichiers images.
  - **mp3, wav, ogg, flac, aac et m4a** pour les fichiers audio.
  - **mp4, mov, avi, mkv** pour les fichiers vidéo.
  - **pdf, zip et rar.**
- Ce lien peut être protégé par un mot de passe (minimum 6 caractères).
- L'utilisateur, en créant un compte, aura accès à son espace personnel où il pourra consulter et supprimer tous ses fichiers uploadés.

---

## Sommaire

- [1. Fonctions](#1-fonctions)
- [2. Stack technique](#2-stack-technique)
- [3. Versions](#3-versions)
- [4.Variables d'environnement](#4-variables-denvironnement)

- [5. Prérequis](#5-prerequis)
- [6.Lancement de l'application](#6-lancement-application)
  - [6.1. Back-end](#61-back-end)
  - [6.2. Front-end](#62-front-end)
- [7.Tests](#7-tests)
  - [7.1Back-end](#71-back-end)
  - [7.2 Front-end - Tests unitaires](#72-front-end--tests-unitaires)
  - [7.3 Front-end - Tests e2e](#73-front-end-tests-e2e)
- [8.Structure du projet](#8-structure-du-projet)
- [9.Déploiement](#9-deploiement)

---

### 1. Fonctions

- **Téléversement** (avec ou sans compte) de fichiers jusqu'à 1 Go, types autorisés (images, vidéos, audio, PDF, ZIP…) protégé par mot de passe (optionnel - 6 caractères minimum)

- **Téléchargement** via lien de partage, avec ou sans mot de passe.

- **Expiration** du lien réglable lors du téléversement de 1 à 7 jours.

- **Suppression automatique du lien** par tâche planifiée tous les jours à 23h (cron interne à la JVM Spring Boot).

- **Inscription / Connexion** avec authentification par JWT (token valable 24h).

- **Espace personnel** quand utilisateur authentifié avec liste des fichiers uploadés, filtres (actif / expiré, suppression.

---

## 2. Stack technique

| Couche          | Technologies                                                                  |
| --------------- | ----------------------------------------------------------------------------- |
| Back-end        | Spring Boot 4, Spring Security, JWT, PostgreSQL, Liquibase, Lombok, MapStruct |
| Front-end       | Angular 19, Lucide Angular(icônes), Ngx-toastr(notification)                  |
| Tests back-end  | JUnit 5, Mockito, Testcontainers(Bdd PostgreSQL)                              |
| Tests front-end | Jest, Cypress                                                                 |
| Bdd             | PostgreSQL                                                                    |

---

## 3. Versions

- Pour le détail des versions des dépendances, voir [MAINTENANCE.md]

---

## 4. Variables d'environnement

(Back-end)

Créer un fichier `.env` à la racine de `back-end_dataShare/` (ou définir les variables dans votre environnement) :

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=<datashare>
DB_USER=<user>
DB_PASSWORD=<password>
JWT_SECRET=<long_secret>
```

---

## 5. Prerequis

Cloner le repository :

```bash
git clone https://github.com/nicowtt/devopsProjet3.git
cd devopsProjet3
```

## 6. Lancement application

### 6.1. Back-end

Spring Boot démarre automatiquement la base de données PostgreSQL via Docker Compose.

```bash
cd back-end_dataShare
mvn spring-boot:run
```

**<u>!! ATTENTION !!</u>**

**En production :**
la ligne `docker.compose.enabled: true` dans le fichier de configuration application.yaml démarre automatiquement la base de données PostgreSQL.

->  Il faudra désactiver ce mécanisme de démarrage. Il est nécessaire de séparer le démarrage de la BDD et celui de l'application pour ne pas perdre des fichiers dans le volume docker suite à un  `docker-compose down -v`

```bash
cd back-end_dataShare
docker compose up -d
mvn spring-boot:run
```

Dès que le Back-end est lancé la documentation est disponible aux urls :

- JSON API : `http://localhost:9000/api-docs`

- Swagger API : `http://localhost:9000/swagger-ui/index.html`

### 6.2. Front-end

```bash
cd front-end_dataShare
npm install
ng serve
```

L'application est disponible sur : `http://localhost:4200`

---

## 7. Tests

### 7.1 Back-end

Outil : JUnit 5 + Testcontainers

```bash
cd back-end_dataShare
mvn clean test
```

Rapport de couverture JaCoCo : `target/site/jacoco/index.html`

### 7.2 Front-end - Tests unitaires

Outil : Jest

```bash
cd front-end_dataShare
npm test
```

### 7.3 Front-end - Tests e2e

Outil : Cypress

**Mode avec interface graphique :**

```bash
cd front-end_dataShare
npx cypress open
```

**Mode création de rapport de couverture :**

```bash
# Terminal 1 — démarrer le serveur en mode instrumenté
ng run front-end_dataShare:serve-coverage

# Terminal 2 — lancer les scénarios de tests
npx cypress run --spec "cypress/e2e/register_and_login.cy.ts"
npx cypress run --spec "cypress/e2e/upload_see_ddl_and_delete_file_with_authenticated_user.cy.ts"

# Afficher le rapport de couverture
npx nyc report --reporter=text-summary
```

---

## 8. Structure du projet

```
projet-3/
    back-end_dataShare/     # API Spring Boot
        - files/            # Fichiers des partages d'utilisateur en cours
        - src/
        - compose.yaml      # PostgreSQL via Docker
        - pom.xml
    front-end_dataShare/    # Application Angular
        - src/
        - cypress/          # Tests e2e
        - package.json
    MAINTENANCE.md          # Plan de maintenance
    TESTING.md              # Plan de test détaillé
    README.md               # Processus d'installation et d'exécution
```

### 9. Deploiement

**A/** Acheter un nom de domaine + serveur.

    -> pointer le nom de domaine vers l’ip du serveur.

**B/** Installer les outils (Nginx / JAVA  / NodeJS /PostgreSQL) sur le serveur.

**C/** Cloner le repository (https://github.com/nicowtt/devopsProjet3)

**D/** Crée le fichier .env

**E/** Lancer la base donnée PostgreSQL (docker compose up -d)

**F/** Builder et deployer le Back-end

**G/** Builder et déployer le Front-end.

    -> ng build --configuration production

**H/** Configurer le Nginx pour la déclaration des applications.

**I/** Activer le https (ssl) avec Certbot.
