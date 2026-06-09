# Performances — DataShare

## Sommaire

1. [Performance API](#1-performance-api)
   
   - 1.1 [Pourquoi cet endpoint ?](#11-pourquoi-cet-endpoint-)
   
   - 1.2 [Méthode et charge basique](#12-méthode-et-charge-basique)
   
   - 1.3 [Analyse performance](#13-analyse-performance)
   
   - 1.4 [Forte charge](#14-forte-charge-)

2. [Analyse du bundle Angular front-end](#2-analyse-du-bundle-angular-front-end)
   
   - 2.1 [Réglage par défaut du budget](#21-réglage-par-default-du-budgets)
   
   - 2.2 [Installation des outils](#22-installation-des-outils)
   
   - 2.3 [Analyse](#23-analyse)
   
   - 2.4 [Amélioration effectuée](#24-amélioration-effectué-)
   
   - 2.5 [Warnings restants](#25-warnings-restants)
   
   - 2.6 [Conclusion et correctif](#26-conclusion-et-correctif)

## 1. Performance API

Test de l'endpoint `GET /api/files/{uuid}`

### 1.1 Pourquoi cet endpoint ?

Ce sera le plus sollicité : chaque visite sur une page de téléchargement déclenche un appel public (sans authentification) vers `GET /api/files/{uuid}`. Le Back-end fait une requête en bdd avec l'uuid (indexé), puis retourne les métadonnées du fichier.

### 1.2 Méthode et charge basique

Test en environnement local, avec `curl` pour la latence unitaire et `ab` (ApacheBench) pour simuler de la charge.

**Latence unitaire — 5 requêtes curl :**

```bash
curl -s -o /dev/null -w "temps total : %{time_total}s (TTFB: %{time_starttransfer}s)\n" \
  http://localhost:9000/api/files/0c01372e-b1cc-4d28-b6a9-309af194a3d6
```

```
TTFB: Temps de traitement serveur de la reqûete.
requête 1 : 0.009726s  (TTFB: 0.009590s)
requête 2 : 0.008488s  (TTFB: 0.008350s)
requête 3 : 0.007585s  (TTFB: 0.007449s)
requête 4 : 0.008047s  (TTFB: 0.007916s)
requête 5 : 0.008857s  (TTFB: 0.008681s)
```

**Charge — 500 requêtes, 20 connexions simultanées (`ab -n 500 -c 20`) :**

```bash
ab -n 500 -c 20 http://localhost:9000/api/files/0c01372e-b1cc-4d28-b6a9-309af194a3d6
```

```bash
Concurrency Level:      20
Time taken for tests:   0.377 seconds
Complete requests:      500
Failed requests:        0
Total transferred:      236000 bytes
HTML transferred:       88000 bytes
Requests per second:    1324.72 [#/sec] (mean)
Time per request:       15.097 [ms] (mean)
Time per request:       0.755 [ms] (mean, across all concurrent requests)
Transfer rate:          610.62 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.2      0       1
Processing:     6   14   4.8     13      35
Waiting:        6   14   4.6     13      35
Total:          6   15   4.8     13      35

Percentage of the requests served within a certain time (ms)
  50%     13
  66%     14
  75%     15
  80%     16
  90%     19
  95%     24 -> 95% des requête font moins de 24 ms
  98%     33
  99%     34
 100%     35 (longest request) -
```

### 1.3 Analyse performance

50% des requête en dessous de 13 ms et 99% en dessous de 34 -> résultat très bon.
Aucune requête n'a échoué parmi les 500.

Le temps de connexion est quasi nul (0–1 ms) : on bénéficie du keep-alive HTTP.

En production, il faudra ajouter la latence réseau (typiquement +20–50 ms selon la région), mais l'endpoint lui-même ne présente pas de goulet d'étranglement — c'est un simple `SELECT` par UUID indexé suivi d'une projection DTO, pas de calcul coûteux.



## 1.4 Forte charge :

**Charge — 1000 requêtes, 500 connexions simultanées (`ab -n 500 -c 20`) :**

```bash
ab -n 1000 -c 500 http://localhost:9000/api/files/0c01372e-b1cc-4d28-b6a9-309af194a3d6
```

```bash
Concurrency Level:      500
Time taken for tests:   0.672 seconds
Complete requests:      1000
Failed requests:        0
Total transferred:      472000 bytes
HTML transferred:       176000 bytes
Requests per second:    1488.17 [#/sec] (mean)
Time per request:       335.982 [ms] (mean)
Time per request:       0.672 [ms] (mean, across all concurrent requests)
Transfer rate:          685.96 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0   11  11.5     11      33
Processing:    11  241  85.1    278     409
Waiting:        7  241  85.2    278     409
Total:         39  252  77.6    279     410

Percentage of the requests served within a certain time (ms)
  50%    279
  66%    283
  75%    288
  80%    302
  90%    338
  95%    362
  98%    375
  99%    384
 100%    410 (longest request)
```

- Aucun fail request

- Les résultats sont très bon malgré cette charge.

**<u>Tableau de référence (Pour information)</u>**

Pour 500 connections simultanées :

| Percent      | Bon      | Acceptable | Mauvais  |
| ------------ | -------- | ---------- | -------- |
| p50(médiane) | < 100 ms | < 300 ms   | > 500 ms |
| P95          | < 500 ms | < 1 s      | > 2 s    |
| p99          | < 1 s    | < 2 s      | > 3 s    |

---

## 2. Analyse du bundle Angular front-end

 **<u>Est-ce que mon front est léger ? (Ce que le navigateur charge)</u>**

Quand un utilisateur ouvre l'app, le navigateur télécharge des fichiers JS et CSS. Plus c'est gros, plus c'est long à charger, surtout sur mobile ou connexion lente.

Angular compile tout ton code en quelques fichiers (les "chunks"). Il y a deux façons de voir ce qu'il y a dedans :

1. **Le log du build** → tailles par fichier
2. **source-map-explorer** → répartition visuelle par bibliothèque

### 2.1 Réglage par default du budgets

Il est définie dans `angular.json`

```json
"budgets": [
  { "type": "initial", "maximumWarning": "500kB", "maximumError": "1MB" },
  { "type": "anyComponentStyle", "maximumWarning": "4kB", "maximumError": "8kB" }
]
```

**`type: "initial"`** : C'est le bundle chargé au démarrage de l'application

C'est tout ce qu'Angular envoie au navigateur avant même que l'utilisateur interagisse. Si ce bundle dépasse :

- **500 kB** → warning dans le terminal, le build passe quand même
- **1 MB** → erreur, le build échoue
  

**`type: "anyComponentStyle"`** : Ce sont les styles propres à chaque composant.

Les fichiers `.scss` / `.css` associés à un composant.
Si un seul composant dépasse :

- **4 kB** → warning
- **8 kB** → erreur

**Ce que ça ne couvre pas**

Ces budgets ne mesurent pas les chunks chargés en **lazy loading** (les modules chargés à la demande). Pour ça il faut ajouter un budget de type `"anyLazyChunk"` .

## 2.2 Installation des outils

 **Ce projet est analysé avec source-map-explorer :**

```bash
# Installation
npm install --save-dev source-map-explorer

# Build avec source maps
ng build --source-map

# Analyse
npx source-map-explorer dist/<nom-projet>/browser/*.js
```

Et dans `package.json` ajout des scripts :

json

```json
"scripts": {
  "build:analyze": "ng build --source-map",
  "analyze": "source-map-explorer dist/<nom-projet>/browser/*.js"
}
```




## 2.3 Analyse

```bash
npm run build
```

![](/Users/nicolasbodelle/Library/Application%20Support/marktext/images/2026-06-09-12-18-28-image.png)

Budget 440.26 kB -> en dessous du seuil de warning de 500 kb définie par default.

```bash
# Génèration du rapport HTML
npm run analyze
```

Un rapport **bundle-report.html** est généré.

 (.../front-end_dataShare/bundle-report.html)
![](/Users/nicolasbodelle/Library/Application%20Support/marktext/images/2026-06-09-12-10-30-image.png) 

@Angular -> 71% (331 Kb)-> framework Angular incompréssible !

src -> 11 % -> Code métier (léger -> pas de code inutile)

<u>Conclusion:</u>

Idéalement le code métier ne doit pas dépasser 20-30%, ok dans ce projet.
Les 71 % restant c'est le framework et les dépendances -> C'est le coût d'angular.

## 2.4 Amélioration effectué :

Avant cette session, `@fortawesome/fontawesome-free` était inclus inutilement.
J'avait deux librairie d'icones, `fontawesome` et `lucide-angular`.
Fontawesome ajoutait ~75 kB au CSS et faisait dépasser le budget (> 500 kB). 

<u>La suppression de `fontawesome` ont ramené le total sous la limite.</u> 

### 2.5 Warnings restants

Deux composants dépassent légèrement le budget de 4 kB pour les styles :

- `upload.component.css` : 4.40 kB 
- `space.component.css` : 5.11 kB 

Ce sont des warnings non bloquants. 
Ces composants sont les plus riches visuellement (drag & drop, liste avec actions). 

## 2.6 Conclusion et correctif

Le projet est dans un état sain côté performances :

- L'endpoint `GET /api/files/{uuid}` répond en **~8 ms** unitaire et tient **1 324 req/s** à 20 connexions simultanées sans aucun échec.
- Aprés amélioration le bundle front est à **440 kB** brut (< 500) , dans les limites du budget Angular.




<u>Pour éviter la regression de performance, j'ai resseré les seuils :</u>

- Pour le bundle initial **600 kB en warning et 700 kB en erreur**. 
  -> Si quelqu'un ajoute une grosse librarie, en sera en warning. -> Détection non bloquante.

- Pour le style, on ajuste à **6kb en warning et 8kb en max.** -> Cela préviendra si un composant devient excessivement lourd.
