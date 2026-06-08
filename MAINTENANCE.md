# MAINTENANCE DataShare

Ce document décrit les procédures de maintenance de l'application DataShare : 

* Mises à jour des dépendances.

* Fréquence recommandée.

* Risques associés. 

* Procédures de rollback.

---

## Sommaire

- [1. Versions actuelles](#1-versions-actuelles)
  - [Back-end](#back-end)
  - [Front-end](#front-end)
- [2. Fréquence des mises à jour recommandée](#2-fréquence-des-mises-à-jour-recommandée)
- [3. Procédures de mise à jour](#3-procédures-de-mise-à-jour)
  - [3.1 Back-end (Maven)](#31-back-end-maven)
  - [3.2 Front-end (npm / Angular)](#32-front-end-npm--angular)
  - [3.3 Base de données PostgreSQL](#33-base-de-données-postgresql)
  - [3.4 Schéma de base de données (Liquibase)](#34-schéma-de-base-de-données-liquibase)
- [4. Risques par composant](#4-risques-par-composant)
  - [Spring Boot](#spring-boot)
  - [JWT](#jwt-jjwt-013x)
  - [Angular](#angular)
  - [PostgreSQL](#postgresql)
  - [Apache Tika](#apache-tika)
- [5. Sauvegarde](#5-sauvegarde)
  - [Base de données](#base-de-données)
  - [Fichiers uploadés](#fichiers-uploadés)
- [6. Rollback](#6-rollback)
  - [Rollback applicatif](#rollback-applicatif)
  - [Rollback de schéma (Liquibase)](#rollback-de-schéma-liquibase)
- [7. Checklist de mise à jour](#7-checklist-de-mise-à-jour)

---

## 1. Versions actuelles

### Back-end

Toutes les versions sont centralisées dans le fichier maven `pom.xml`.

-> section <properties> ligne 32.

| Composant                 | Version actuelle     |
| ------------------------- | -------------------- |
| Java                      | 21                   |
| Spring Boot               | 4.0.6                |
| PostgreSQL (image Docker) | 18                   |
| Liquibase                 | géré par Spring Boot |
| JJWT                      | 0.13.0               |
| Apache Tika               | 3.2.2                |
| Lombok                    | 1.18.32              |
| MapStruct                 | 1.6.3                |
| Testcontainers            | 1.20.0               |
| SpringDoc OpenAPI         | 2.8.6                |

### Front-end

| Composant      | Version actuelle |
| -------------- | ---------------- |
| Node.js        | 22.9.0           |
| npm            | 10.8.3           |
| Angular CLI    | 19.2.25          |
| Angular        | 19.2.0           |
| TypeScript     | 5.7.0            |
| Lucide Angular | 1.0.0            |
| Ngx-toastr     | 19.1.0           |
| Jest           | 29.7.0           |
| Cypress        | 15.0             |

---

## 2. Fréquence des mises à jour recommandée

| Type                         | Fréquence                  | Déclencheur                                                                  |
| ---------------------------- | -------------------------- | ---------------------------------------------------------------------------- |
| Patches de sécurité          | Immédiat                   | vulnérabilité critique ou alerte Dependabot (non mis en place sur ce projet) |
| Dépendances mineures (patch) | Mensuelle                  | / (veille)                                                                   |
| Dépendances majeures         | Trimestrielle              | / (veille)                                                                   |
| Image Docker PostgreSQL      | Trimestrielle              | Nouvelle version stable                                                      |
| Java LTS                     | Lors des sorties LTS       | Tous les 2/3 ans.                                                            |
| Angular                      | Lors des versions majeures | Tous les ans environ.                                                        |

---

## 3. Procédures de mise à jour

### 3.1 Back-end (Maven)

**Vérifier les mises à jour disponibles :**

```bash
cd back-end_dataShare
mvn versions:display-dependency-updates
mvn versions:display-plugin-updates
```

**Appliquer une mise à jour :**

1. Modifier la version dans `pom.xml` (section `<properties>`).

2. Relancer les tests :
   
   ```bash
   mvn clean test
   ```

3. Vérifier que l'application démarre correctement :
   
   ```bash
   mvn spring-boot:run
   ```

**Mise à jour de Spring Boot :**

Changer la version dans `<parent>` :

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>X.Y.Z</version>
</parent>
```

Consulter le [Migration Guide Spring Boot](https://github.com/spring-projects/spring-boot/wiki) avant toute mise à jour majeure.

---

### 3.2 Front-end (npm / Angular)

**Vérifier les mises à jour disponibles :**

```bash
cd front-end_dataShare
npm outdated
```

**Patches et mises à jour mineures :**

```bash
npm update
npm test
```

**Mise à jour majeure d'Angular (ex. : 19 → 20) :**

Angular fournit un outil de migration automatisé :

```bash
ng update @angular/core @angular/cli
```

Consulter [update.angular.io](https://update.angular.io) pour le guide de migration interactif.

**Mise à jour de Cypress :**

```bash
npm install cypress@latest --save-dev
npx cypress verify
```

---

### 3.3 Base de données PostgreSQL

L'image Docker est définie dans `back-end_dataShare/compose.yaml` :

```yaml
image: 'postgres:18'
```

**Procédure de mise à jour d'image :**

1. Sauvegarder les données existantes (voir section 5).

2. Modifier le tag dans `compose.yaml`.

3. Arrêter et recréer le conteneur :
   
   ```bash
   docker compose down
   docker compose up -d
   ```

4. Vérifier que les migrations Liquibase s'appliquent correctement au démarrage de Spring Boot.

---

### 3.4 Schéma de base de données (Liquibase)

Les migrations sont versionées au fur et à mesure du développement dans `src/main/resources/db/changelog/`.

**Règles impératives :**

- Ne **jamais modifier** un changeset déjà appliqué en production. Créer un nouveau fichier de migration.
- Nommer les fichiers de manière incrémentale : `003-nom-de-la-migration.sql`.
- Référencer le nouveau fichier dans `db.changelog-master.yaml`.

---

## 4. Risques par composant

### Spring Boot

| Risque                                                | Probabilité | Impact   | Action                                                       |
| ----------------------------------------------------- | ----------- | -------- | ------------------------------------------------------------ |
| Impossibilité de démarrage lors d'une version majeure | Moyen       | Élevé    | Lire le migration guide, tester sur branche dédiée en local. |
| Incompatibilité avec une dépendance tierce            | Moyen       | Moyen    | Mettre à jour les dépendances dans le même cycle             |
| Faille de sécurité non patchée                        | Faible      | Critique | Surveiller les CVE, mettre à jour rapidement                 |

### JWT

| Risque                                   | Probabilité                | Impact | Mitigation                                |
| ---------------------------------------- | -------------------------- | ------ | ----------------------------------------- |
| Changement d'API entre versions majeures | Élevé                      | Moyen  | Tester en local.                          |
| Invalidation des tokens en circulation   | Certain (si secret change) | Élevé  | Ne pas changer `JWT_SECRET` en production |

### Angular

| Risque                     | Probabilité | Impact | Mitigation                                  |
| -------------------------- | ----------- | ------ | ------------------------------------------- |
| Impossibilité de démarrage | Moyen       | Moyen  | Utiliser `ng update` et le guide interactif |
| Incompatibilité Jest       | Moyen       | Moyen  | Vérifier en local.                          |
| Incompatibilité Cypress    | Faible      | Moyen  | Vérifier en local.                          |

### PostgreSQL

| Risque                                              | Probabilité         | Impact   | Mitigation                             |
| --------------------------------------------------- | ------------------- | -------- | -------------------------------------- |
| Incompatibilité JDBC driver                         | Faible              | Élevé    | Vérifier en local.                     |
| Perte de données lors de la recréation du conteneur | Certain sans volume | Critique | Attention à la sauvegarde des données. |

### Apache Tika

| Risque                          | Probabilité | Impact | Mitigation                                          |
| ------------------------------- | ----------- | ------ | --------------------------------------------------- |
| Faille sur un parseur de format | Moyen       | Élevé  | Surveillance CVE, mise à jour prioritaire si alerte |

---

## 5. Sauvegarde

### Base de données

```bash
# Dump
docker exec <nom_conteneur_postgres> pg_dump -U $DB_USER $DB_NAME > backup_$(date +%Y%m%d).sql

# Restauration
docker exec -i <nom_conteneur_postgres> psql -U $DB_USER $DB_NAME < backup_YYYYMMDD.sql
```

### Fichiers uploadés

Les fichiers sont stockés dans le dossier défini par `file.upload-dir` dans `application.yaml` (par défaut : `files/` à la racine du back-end). 

-> Inclure ce dossier dans les sauvegardes régulières.

---

## 6. Rollback

### Rollback applicatif

En cas de régression après une mise à jour :

1. Revenir au commit précédent :
   
   ```bash
   git revert <commit>
   # ou
   git checkout <tag-version-stable>
   ```

2. Relancer les tests pour valider.

3. Redéployer.

### Rollback de schéma bdd (Liquibase)

Liquibase supporte le rollback si les changesets définissent un `rollback` :

```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

Si aucun rollback n'est défini dans le changeset, restaurer le dump SQL de la base avant migration.

---

## 7. Checklist de mise à jour

Avant de déployer une mise à jour :

- [ ] Les tests back-end passent (`mvn clean test`)
- [ ] Les tests unitaires front-end passent (`npm test`)
- [ ] Les tests fonctionnels (e2e) passent (`npx cypress run`)
- [ ] Les applications démarre sans erreur.
- [ ] La documentation Swagger API est accessible (`/swagger-ui`)
- [ ] Une sauvegarde de la base de donnée a été effectuée.
