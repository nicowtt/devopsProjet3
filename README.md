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

### Fonctionnalités

- **Téléversement** (avec ou sans compte) de fichiers jusqu'à 1 Go, types autorisés (images, vidéos, audio, PDF, ZIP…) protégé par mot de passe (optionnel - 6 caractères minimum)

- **Téléchargement** via lien de partage, avec ou sans mot de passe.

- **Expiration** du lien réglable lors du téléversement de 1 à 7 jours.

- **Suppression automatique du lien** par tâche planifiée tous les jours à 23h (cron interne à la JVM Spring Boot).

- **Inscription / Connexion** avec authentification par JWT (token valable 24h).

- **Espace personnel** quand utilisateur authentifié avec liste des fichiers uploadés, filtres (actif / expiré, suppression.

---

## Sommaire

- [Fonctionnalités](#fonctionnalités)
- [Stack technique](#stack-technique)
- [Versions](#versions)
- [Variables d'environnement](#variables-denvironnement)
  - [Back-end](#back-end)
- [Prérequis](#prérequis)
- [Lancement de l'application](#lancement-de-lapplication)
  - [1. Back-end](#1-back-end)
  - [2. Front-end](#2-front-end)
- [Tests](#tests)
  - [Back-end (JUnit 5 + Testcontainers)](#back-end-junit-5--testcontainers)
  - [Front-end — Tests unitaires (Jest)](#front-end--tests-unitaires-jest)
  - [Front-end — Tests e2e (Cypress)](#front-end--tests-e2e-cypress)
- [Structure du projet](#structure-du-projet)

---

## Stack technique

| Couche          | Technologies                                                                  |
| --------------- | ----------------------------------------------------------------------------- |
| Back-end        | Spring Boot 4, Spring Security, JWT, PostgreSQL, Liquibase, Lombok, MapStruct |
| Front-end       | Angular 19, Lucide Angular(icônes), Ngx-toastr(notification)                  |
| Tests back-end  | JUnit 5, Mockito, Testcontainers(Bdd PostgreSQL)                              |
| Tests front-end | Jest, Cypress                                                                 |
| Bdd             | PostgreSQL                                                                    |

---

## Versions

- Pour le détail des versions des dépendances, voir [MAINTENANCE.md]

---

## Variables d'environnement

### Back-end

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

## Prérequis

Cloner le repository :

```bash
git clone https://github.com/nicowtt/devopsProjet3.git
cd devopsProjet3
```

## Lancement de l'application

### 1. Back-end

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

### 2. Front-end

```bash
cd front-end_dataShare
npm install
ng serve
```

L'application est disponible sur : `http://localhost:4200`

---

## Tests

### Back-end (JUnit 5 + Testcontainers)

```bash
cd back-end_dataShare
mvn clean test
```

Rapport de couverture JaCoCo : `target/site/jacoco/index.html`

### Front-end — Tests unitaires (Jest)

```bash
cd front-end_dataShare
npm test
```

### Front-end — Tests e2e (Cypress)

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

## Structure du projet

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
