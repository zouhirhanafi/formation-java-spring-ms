# Module 2 : Premier Microservice - Service Catalogue (Guide Complet)

**Durée** : 2h théorie + 6h pratique
**Objectif** : Maîtriser Spring Boot, JPA et REST pour créer un microservice professionnel

> 💡 **Ce guide approfondit tous les concepts**. Pour un démarrage rapide, consultez `cours-essentiel.md`

---

## Table des Matières

1. [Architecture Web & REST](#1-architecture-web--rest)
2. [Spring Boot - Architecture Interne](#2-spring-boot---architecture-interne)
3. [JPA & Hibernate - Concepts Avancés](#3-jpa--hibernate---concepts-avancés)
4. [Service Layer - Patterns et Transactions](#4-service-layer---patterns-et-transactions)
5. [REST Controller - Best Practices](#5-rest-controller---best-practices)
6. [Tests - Stratégies Complètes](#6-tests---stratégies-complètes)
7. [Performance et Optimisations](#7-performance-et-optimisations)

---

## 1. Architecture Web & REST

### 1.1. Architecture Backend / Frontend

#### Évolution Historique

**Approche Monolithique Traditionnelle** (années 2000) :

```
┌────────────────────────────────────────┐
│      Serveur d'Application Monolithe   │
│  ┌──────────────────────────────────┐  │
│  │  JSP/Thymeleaf (Vue)             │  │
│  │  Controllers (Contrôleur)        │  │
│  │  Services (Modèle)               │  │
│  │  DAO/Repository (Données)        │  │
│  └──────────────────────────────────┘  │
│             ↓ ↑                        │
│  ┌──────────────────────────────────┐  │
│  │  Base de Données                 │  │
│  └──────────────────────────────────┘  │
└────────────────────────────────────────┘
```

**Problèmes** :

- ❌ Frontend et backend fortement couplés
- ❌ Difficile d'avoir plusieurs clients (web, mobile, IoT)
- ❌ Scalabilité limitée (on scale tout ou rien)
- ❌ Technologies frontend liées au backend (JSP, Thymeleaf)

**Approche Moderne (SPA + API)** :

```
┌───────────────────────────────────────────────────┐
│               Clients (Frontend)                  │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐  │
│  │ React SPA   │ │ Flutter App │ │ IoT Device  │  │
│  │ (Port 3000) │ │ (Mobile)    │ │             │  │
│  └─────────────┘ └─────────────┘ └─────────────┘  │
└──────────────────┬────────────────────────────────┘
                   │ HTTP/HTTPS (JSON)
                   │ REST API Calls
                   ▼
┌───────────────────────────────────────────────────┐
│            Backend (Microservices)                │
│  ┌─────────────────────────────────────────────┐  │
│  │  API Gateway (Port 8080)                    │  │
│  │  - Routing                                  │  │
│  │  - Load Balancing                           │  │
│  │  - Authentication                           │  │
│  └─────────────────────────────────────────────┘  │
│           │                 │                     │
│           ▼                 ▼                     │
│  ┌──────────────┐  ┌──────────────┐               │
│  │ Catalogue    │  │ Order        │               │
│  │ Service      │  │ Service      │  ...          │
│  │ (Port 8081)  │  │ (Port 8082)  │               │
│  └──────────────┘  └──────────────┘               │
│         │                  │                      │
│         ▼                  ▼                      │
│  ┌──────────────┐  ┌──────────────┐               │
│  │ PostgreSQL   │  │ PostgreSQL   │               │
│  │ catalogue_db │  │ orders_db    │               │
│  └──────────────┘  └──────────────┘               │
└───────────────────────────────────────────────────┘
```

**Avantages** :

- ✅ **Indépendance technologique** : Frontend (React/Vue/Angular) décorrélé du Backend (Spring/Node/Go)
- ✅ **Multi-clients** : Une API sert web, mobile, desktop, IoT
- ✅ **Scalabilité ciblée** : Scale uniquement les services chargés
- ✅ **Équipes autonomes** : Frontend et Backend peuvent évoluer indépendamment
- ✅ **Déploiement indépendant** : Mise à jour d'un service sans toucher aux autres

#### Séparation des Responsabilités

| Couche | Responsabilités | Technologies |
|--------|-----------------|--------------|
| **Frontend** | - Interface utilisateur<br>- Expérience utilisateur (UX)<br>- Validation côté client<br>- Gestion d'état local | React, Vue, Angular, Flutter, SwiftUI |
| **Backend** | - Logique métier<br>- Validation côté serveur<br>- Sécurité et autorisation<br>- Accès aux données<br>- Transactions | Spring Boot, Node.js, Go, .NET |
| **Base de données** | - Persistence des données<br>- Intégrité référentielle<br>- Contraintes métier | PostgreSQL, MySQL, MongoDB |

### 1.2. REST (Representational State Transfer)

#### Les 6 Principes Fondamentaux de REST

**1. Architecture Client-Serveur**

- Client et serveur sont séparés
- Client gère l'interface utilisateur
- Serveur gère les données et la logique métier
- Permet l'évolution indépendante

**2. Stateless (Sans État)**

- Chaque requête du client doit contenir **toute l'information nécessaire**
- Le serveur ne stocke **aucun contexte de session**
- Chaque requête est indépendante

```java
// ❌ Stateful (mauvais pour REST)
// Le serveur stocke le panier en session
GET /cart → Retourne le panier de la session

// ✅ Stateless (bon pour REST)
// Le client envoie tout le contexte nécessaire
GET /carts/12345 → Retourne le panier ID 12345
Authorization: Bearer eyJhbGc... → JWT contient l'identité
```

**Avantages du Stateless** :

- ✅ Scalabilité horizontale facile (load balancer vers n'importe quel serveur)
- ✅ Cache côté client plus efficace
- ✅ Résilience (panne d'un serveur n'impacte pas les sessions)

**3. Cacheable (Cachable)**

- Les réponses doivent indiquer si elles sont cachables ou non
- Headers HTTP : `Cache-Control`, `ETag`, `Last-Modified`

```http
HTTP/1.1 200 OK
Cache-Control: max-age=3600, public
ETag: "33a64df551425fcc55e4d42a148795d9f25f89d4"
Last-Modified: Wed, 15 Nov 2023 12:45:26 GMT

{ "id": 1, "name": "Laptop", ... }
```

**4. Interface Uniforme**

Quatre contraintes :

1. **Identification des ressources** : Chaque ressource a un URI unique
2. **Manipulation via représentations** : Client manipule les ressources via JSON/XML
3. **Messages auto-descriptifs** : Chaque message contient assez d'info pour être compris
4. **HATEOAS** : Hypermedia As The Engine Of Application State (voir ci-dessous)

**5. Système en Couches**

- Client ne sait pas s'il est connecté directement au serveur final
- Permet d'ajouter des couches (cache, load balancer, proxy)

```
Client → Load Balancer → API Gateway → Microservice → Database
```

**6. Code à la demande (optionnel)**

- Le serveur peut envoyer du code exécutable (JavaScript, applets)
- Rarement utilisé en pratique

#### HATEOAS (Hypermedia as the Engine of Application State)

**Concept** : Les réponses contiennent des liens vers les actions possibles.

**Exemple sans HATEOAS** (classique) :

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 999.99
}
```

**Exemple avec HATEOAS** :

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 999.99,
  "_links": {
    "self": { "href": "/api/v1/products/1" },
    "update": { "href": "/api/v1/products/1", "method": "PUT" },
    "delete": { "href": "/api/v1/products/1", "method": "DELETE" },
    "category": { "href": "/api/v1/categories/5" },
    "reviews": { "href": "/api/v1/products/1/reviews" }
  }
}
```

**Avantages** :

- ✅ Client découvre l'API dynamiquement
- ✅ Évolution de l'API plus facile (changement d'URLs)
- ✅ Self-documenting API

**Spring HATEOAS** :

```java
@GetMapping("/{id}")
public EntityModel<Product> getProduct(@PathVariable Long id) {
    Product product = productService.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    return EntityModel.of(product,
        linkTo(methodOn(ProductController.class).getProduct(id)).withSelfRel(),
        linkTo(methodOn(ProductController.class).deleteProduct(id)).withRel("delete"),
        linkTo(methodOn(CategoryController.class).getCategory(product.getCategoryId())).withRel("category")
    );
}
```

> 💡 **Note** : HATEOAS est considéré comme le niveau 3 du modèle de maturité REST (Richardson Maturity Model), mais reste optionnel pour la plupart des API modernes.

### 1.3. Normes des API REST

#### Ressources et URLs

**Principes** :

1. **Noms au pluriel** : `/products` (pas `/product`)
2. **Hiérarchie reflète la structure** : `/categories/5/products`
3. **Pas de verbes** : `/products` (pas `/getProducts`)
4. **Versioning** : `/api/v1/products`

**Exemples d'URLs bien conçues** :

| URL | Description |
|-----|-------------|
| `GET /api/v1/products` | Liste tous les produits |
| `GET /api/v1/products/1` | Récupère le produit ID 1 |
| `GET /api/v1/categories/5/products` | Produits de la catégorie 5 |
| `GET /api/v1/products?category=Electronics` | Filtrer par catégorie |
| `GET /api/v1/products?page=2&size=20` | Pagination |
| `GET /api/v1/products/search?q=laptop` | Recherche |

#### Méthodes HTTP

| Méthode | Idempotent | Safe | Usage |
|---------|-----------|------|-------|
| GET | ✅ | ✅ | Récupérer une ressource |
| POST | ❌ | ❌ | Créer une nouvelle ressource |
| PUT | ✅ | ❌ | Remplacer complètement une ressource |
| PATCH | ❌ | ❌ | Modifier partiellement une ressource |
| DELETE | ✅ | ❌ | Supprimer une ressource |
| HEAD | ✅ | ✅ | Récupérer les headers seulement |
| OPTIONS | ✅ | ✅ | Récupérer les méthodes supportées |

**Idempotent** : Appeler N fois = même résultat qu'appeler 1 fois
**Safe** : Lecture seule, ne modifie pas l'état du serveur

**Exemples détaillés** :

**POST - Création** :

```http
POST /api/v1/products
Content-Type: application/json

{
  "name": "Laptop",
  "sku": "LAP-001",
  "price": 999.99
}

→ 201 Created
Location: /api/v1/products/123
{
  "id": 123,
  "name": "Laptop",
  "sku": "LAP-001",
  "price": 999.99,
  "createdAt": "2025-01-15T10:30:00Z"
}
```

**PUT - Remplacement complet** :

```http
PUT /api/v1/products/123
Content-Type: application/json

{
  "name": "Laptop Dell XPS 13",  // Modifié
  "sku": "LAP-001",               // Obligatoire
  "price": 1099.99,               // Modifié
  "stockQuantity": 10             // Obligatoire
}

→ 200 OK
{ "id": 123, ... }
```

**PATCH - Modification partielle** :

```http
PATCH /api/v1/products/123
Content-Type: application/json

{
  "price": 899.99  // Seulement le prix
}

→ 200 OK
{ "id": 123, "price": 899.99, ... }
```

**Différence PUT vs PATCH** :

- **PUT** : Remplace **toute** la ressource (tous les champs doivent être fournis)
- **PATCH** : Modifie **seulement** les champs fournis

#### Codes de Statut HTTP

**2xx - Succès**

| Code | Nom | Usage | Exemple |
|------|-----|-------|---------|
| 200 | OK | Requête réussie (GET, PUT, PATCH) | Récupérer/modifier un produit |
| 201 | Created | Ressource créée (POST) | Créer un produit |
| 202 | Accepted | Requête acceptée, traitement asynchrone | Job lancé en background |
| 204 | No Content | Succès sans corps de réponse (DELETE) | Supprimer un produit |
| 206 | Partial Content | Contenu partiel (pagination, range) | Téléchargement partiel |

**3xx - Redirection**

| Code | Nom | Usage |
|------|-----|-------|
| 301 | Moved Permanently | Ressource déplacée définitivement |
| 302 | Found | Redirection temporaire |
| 304 | Not Modified | Ressource non modifiée (cache valide) |

**4xx - Erreur Client**

| Code | Nom | Usage | Exemple |
|------|-----|-------|---------|
| 400 | Bad Request | Requête mal formée | JSON invalide, champ manquant |
| 401 | Unauthorized | Non authentifié | Token manquant/expiré |
| 403 | Forbidden | Pas les droits | USER tente action ADMIN |
| 404 | Not Found | Ressource inexistante | Produit ID 999 n'existe pas |
| 405 | Method Not Allowed | Méthode HTTP non supportée | DELETE non autorisé |
| 409 | Conflict | Conflit de données | SKU déjà existant, version obsolète |
| 410 | Gone | Ressource supprimée définitivement | Produit archivé |
| 422 | Unprocessable Entity | Données invalides mais bien formées | Prix négatif, email invalide |
| 429 | Too Many Requests | Rate limiting dépassé | 1000 req/min dépassées |

**5xx - Erreur Serveur**

| Code | Nom | Usage |
|------|-----|-------|
| 500 | Internal Server Error | Erreur interne non gérée |
| 502 | Bad Gateway | Erreur du serveur en amont (proxy/gateway) |
| 503 | Service Unavailable | Service temporairement indisponible |
| 504 | Gateway Timeout | Timeout du serveur en amont |

#### Headers HTTP Importants

**Request Headers** :

```http
Accept: application/json                    # Type de contenu accepté
Content-Type: application/json              # Type du body envoyé
Authorization: Bearer eyJhbGc...            # Token JWT
Accept-Language: fr-FR,fr;q=0.9,en;q=0.8   # Langue préférée
If-None-Match: "33a64df551425fcc..."        # ETag pour cache
If-Modified-Since: Wed, 15 Nov 2023 12:45:26 GMT
```

**Response Headers** :

```http
Content-Type: application/json; charset=utf-8
Location: /api/v1/products/123             # URL de la ressource créée
ETag: "33a64df551425fcc..."                # Version de la ressource
Cache-Control: max-age=3600, public        # Durée de cache
X-Total-Count: 150                         # Total d'éléments (pagination)
Link: </api/v1/products?page=2>; rel="next" # Lien pagination
```

#### Négociation de Contenu (Content Negotiation)

Le client demande un format, le serveur répond dans ce format :

```http
GET /api/v1/products/1
Accept: application/json

→ 200 OK
Content-Type: application/json
{ "id": 1, "name": "Laptop" }
```

```http
GET /api/v1/products/1
Accept: application/xml

→ 200 OK
Content-Type: application/xml
<product><id>1</id><name>Laptop</name></product>
```

```http
GET /api/v1/products/1
Accept: text/csv

→ 200 OK
Content-Type: text/csv
id,name,price
1,Laptop,999.99
```

**Spring Boot** supporte cela nativement avec Jackson (JSON) et Jackson XML :

```java
@GetMapping(value = "/{id}", produces = {
    MediaType.APPLICATION_JSON_VALUE,
    MediaType.APPLICATION_XML_VALUE
})
public Product getProduct(@PathVariable Long id) {
    return productService.findById(id).orElseThrow();
}
```

#### Pagination, Tri et Filtrage

**Pagination** :

```http
GET /api/v1/products?page=2&size=20&sort=price,desc

→ 200 OK
X-Total-Count: 150
X-Total-Pages: 8
Link: </api/v1/products?page=1&size=20>; rel="prev",
      </api/v1/products?page=3&size=20>; rel="next"

{
  "content": [ ... ],
  "page": 2,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8
}
```

**Filtrage** :

```http
GET /api/v1/products?category=Electronics&minPrice=100&maxPrice=500&available=true
```

**Recherche** :

```http
GET /api/v1/products/search?q=laptop
GET /api/v1/products?name=*laptop*&description=*high*
```

#### Versioning d'API

**Pourquoi versionner ?**

- Faire évoluer l'API sans casser les clients existants
- Permettre une migration progressive

**Approches** :

1. **URI Versioning** (recommandé pour simplicité) :

```
/api/v1/products
/api/v2/products
```

2. **Header Versioning** :

```http
GET /api/products
Accept: application/vnd.company.v1+json
```

3. **Query Parameter** :

```
/api/products?version=1
```

**Stratégie de migration** :

```
v1 → Maintenue pendant 6 mois après release de v2
v2 → Nouvelle version avec breaking changes
```

---

## 2. Spring Boot - Architecture Interne

### 2.1. Spring vs Spring Boot

**Spring Framework** (2003) :

- ❌ Configuration XML massive
- ❌ Setup complexe (serveur d'application, WAR, etc.)
- ❌ Dépendances manuelles
- ✅ Inversion de contrôle (IoC)
- ✅ Dependency Injection (DI)
- ✅ AOP (Aspect Oriented Programming)

**Spring Boot** (2014) :

- ✅ **Auto-configuration** : Configuration automatique basée sur le classpath
- ✅ **Starter dependencies** : Groupes de dépendances pré-configurés
- ✅ **Embedded servers** : Tomcat/Jetty/Undertow embarqués
- ✅ **Production-ready** : Actuator pour monitoring
- ✅ **Opinionated** : Conventions par défaut intelligentes

### 2.2. Auto-Configuration

**Comment ça marche ?**

```java
@SpringBootApplication
public class CatalogueServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogueServiceApplication.class, args);
    }
}
```

`@SpringBootApplication` est une méta-annotation qui combine :

```java
@SpringBootConfiguration  // = @Configuration
@EnableAutoConfiguration  // Active l'auto-configuration
@ComponentScan            // Scanne les composants du package
```

**@EnableAutoConfiguration** :

- Scanne le classpath
- Détecte les librairies présentes
- Configure automatiquement les beans nécessaires

**Exemple** : Si `spring-boot-starter-data-jpa` est dans le classpath :

1. Détecte Hibernate et H2/PostgreSQL
2. Crée automatiquement :
   - `DataSource` (source de données)
   - `EntityManagerFactory` (gestion des entités JPA)
   - `TransactionManager` (gestion des transactions)
   - `JpaRepository` beans

**Voir ce qui est auto-configuré** :

```bash
# Lancer avec debug
mvn spring-boot:run -Dspring-boot.run.arguments=--debug

# Ou dans application.yml
logging:
  level:
    org.springframework.boot.autoconfigure: DEBUG
```

**Désactiver une auto-configuration** :

```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application { }
```

### 2.3. Starter Dependencies

**Concept** : Groupes de dépendances cohérents.

**Exemple** : `spring-boot-starter-web` inclut :

- spring-web
- spring-webmvc
- jackson (JSON)
- tomcat-embed (serveur embarqué)
- validation-api

**Starters courants** :

| Starter | Inclut |
|---------|--------|
| `spring-boot-starter-web` | REST APIs, Spring MVC, Tomcat |
| `spring-boot-starter-data-jpa` | JPA, Hibernate, Spring Data |
| `spring-boot-starter-test` | JUnit 5, Mockito, AssertJ, Spring Test |
| `spring-boot-starter-security` | Spring Security |
| `spring-boot-starter-actuator` | Monitoring endpoints |
| `spring-boot-starter-cache` | Caching (EhCache, Redis, etc.) |
| `spring-boot-starter-validation` | Bean Validation (Hibernate Validator) |

### 2.4. Configuration Externalisée

**Hiérarchie de priorité** (du plus prioritaire au moins) :

1. Arguments en ligne de commande : `--server.port=8082`
2. Variables d'environnement : `SERVER_PORT=8082`
3. `application.yml` dans le JAR
4. `application.yml` dans `/config` du répertoire courant
5. `application.yml` à la racine du projet
6. Valeurs par défaut

**Exemple** :

```yaml
# application.yml (défaut)
server:
  port: 8080

# application-dev.yml (profile dev)
server:
  port: 8081
logging:
  level:
    root: DEBUG

# application-prod.yml (profile prod)
server:
  port: 80
logging:
  level:
    root: WARN
```

**Activer un profile** :

```bash
# Ligne de commande
java -jar app.jar --spring.profiles.active=dev

# Variable d'environnement
export SPRING_PROFILES_ACTIVE=prod

# application.yml
spring:
  profiles:
    active: dev
```

**Configuration personnalisée** :

```java
@ConfigurationProperties(prefix = "catalogue")
@Component
public class CatalogueProperties {
    private int maxProductsPerPage = 50;
    private boolean enableCache = true;

    // Getters/setters
}
```

```yaml
catalogue:
  max-products-per-page: 100
  enable-cache: false
```

#### Configuration du Context Path (optionnel)

**Context path** = Préfixe URL pour toutes les routes de l'application.

**Configuration** :

```yaml
server:
  servlet:
    context-path: /catalogue  # Optionnel
```

**Impact sur les URLs** :

| Configuration | URLs finales | Cas d'usage |
|---------------|--------------|-------------|
| **Sans context-path** (par défaut) | `http://localhost:8081/api/v1/products` | Application unique ou API Gateway |
| **Avec `/catalogue`** | `http://localhost:8081/catalogue/api/v1/products` | Plusieurs services sur même port (dev local) |
| **Avec `/api`** | `http://localhost:8081/api/v1/products` | Convention courante |

**Exemples concrets** :

```yaml
# Option 1 : Pas de context-path (recommandé en production)
server:
  port: 8081
# URLs : http://localhost:8081/api/v1/products
#        http://localhost:8081/h2-console

# Option 2 : Avec context-path (pratique en dev multi-services)
server:
  port: 8081
  servlet:
    context-path: /catalogue
# URLs : http://localhost:8081/catalogue/api/v1/products
#        http://localhost:8081/catalogue/h2-console
```

**⚠️ Points d'attention** :

1. **Controllers** : Le context-path est automatiquement ajouté

```java
@RestController
@RequestMapping("/api/v1/products")  // Pas besoin de préfixer avec /catalogue
public class ProductController { }

// URLs générées :
// Sans context-path : /api/v1/products
// Avec /catalogue : /catalogue/api/v1/products
```

2. **Tests** : MockMvc utilise automatiquement le context-path

```java
@Test
void test() {
    // Si context-path = /catalogue
    mockMvc.perform(get("/api/v1/products"))  // Fonctionne !
        .andExpect(status().isOk());
}
```

3. **Client HTTP** : Pensez à inclure le context-path

```bash
# Sans context-path
curl http://localhost:8081/api/v1/products

# Avec context-path
curl http://localhost:8081/catalogue/api/v1/products
```

**💡 Bonne pratique** :

- **Dev local** : Utilisez context-path si plusieurs services tournent sur ports différents
- **Production** : Évitez context-path, utilisez API Gateway pour router
- **Multi-tenancy** : Context-path par tenant possible mais pas recommandé (préférez sous-domaines)

**Dans ce module** : Nous utilisons `/catalogue` pour distinguer visuellement les microservices pendant l'apprentissage. En production réelle, vous utiliseriez plutôt un API Gateway (Module 9).

### 2.5. Dependency Injection et IoC

**Inversion of Control (IoC)** : Le framework gère le cycle de vie des objets, pas le développeur.

**Dependency Injection (DI)** : Les dépendances sont injectées, pas créées manuellement.

**3 types d'injection** :

**1. Constructor Injection** (recommandé) :

```java
@Service
@RequiredArgsConstructor  // Lombok génère le constructeur
public class ProductService {
    private final ProductRepository productRepository;  // final = immutable
    private final CategoryClient categoryClient;

    // Lombok génère :
    // public ProductService(ProductRepository repo, CategoryClient client) {
    //     this.productRepository = repo;
    //     this.categoryClient = client;
    // }
}
```

**Avantages** :

- ✅ Immutabilité (final)
- ✅ Testabilité (facile de mocker dans les tests)
- ✅ Obligatoire (ne peut pas créer le bean sans les dépendances)

**2. Setter Injection** (déconseillé) :

```java
@Service
public class ProductService {
    private ProductRepository productRepository;

    @Autowired
    public void setProductRepository(ProductRepository repo) {
        this.productRepository = repo;
    }
}
```

**Problèmes** :

- ❌ Mutable (peut être changé après création)
- ❌ Optionnel (bean peut être créé sans dépendance)

**3. Field Injection** (déconseillé) :

```java
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
}
```

**Problèmes** :

- ❌ Difficile à tester (pas de constructeur)
- ❌ Reflection nécessaire
- ❌ Couplage fort avec Spring

**Bean Scopes** :

| Scope | Cycle de vie |
|-------|-------------|
| `singleton` (défaut) | 1 seule instance pour tout le contexte Spring |
| `prototype` | Nouvelle instance à chaque injection |
| `request` | 1 instance par requête HTTP (web uniquement) |
| `session` | 1 instance par session HTTP (web uniquement) |

```java
@Service
@Scope("prototype")
public class PrototypeService { }
```

---

## 3. JPA & Hibernate - Concepts Avancés

### 3.0. Choix de la Base de Données : H2 vs PostgreSQL

#### Pourquoi H2 pour ce Module ?

**H2** est une base de données **en mémoire** écrite en Java, parfaite pour le développement et l'apprentissage.

**Avantages en développement** :

| Critère | H2 | Impact |
|---------|----|----|
| **Installation** | ✅ Aucune | Ajoutez la dépendance Maven, c'est tout |
| **Configuration** | ✅ Minimale | 3 lignes dans `application.yml` |
| **Démarrage** | ✅ Instantané | Base créée automatiquement en RAM |
| **Tests** | ✅ Parfait | Isolation complète, rapide |
| **Portabilité** | ✅ Maximale | Fonctionne partout (Windows, Mac, Linux) |
| **Apprentissage** | ✅ Focus JPA | Pas de distraction avec la base |

**Limitations** :

| Problème | Conséquence |
|----------|-------------|
| ❌ **Données volatiles** | Tout est perdu au redémarrage |
| ❌ **RAM uniquement** | Pas de persistance disque (mode par défaut) |
| ❌ **Pas pour production** | Utilisable uniquement en dev/test |
| ❌ **Dialecte SQL limité** | Quelques différences vs PostgreSQL |

**Configuration H2** (`application.yml`) :

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:catalogue_db      # Base en mémoire
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: true                     # Console web H2
      path: /h2-console

  jpa:
    hibernate:
      ddl-auto: create-drop             # Recrée le schéma au démarrage
    show-sql: true                      # Affiche les requêtes SQL
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.H2Dialect
```

**Modes H2** :

1. **En mémoire** (ce module) : `jdbc:h2:mem:dbname`
   - Données perdues au redémarrage
   - Ultra-rapide

2. **Fichier** : `jdbc:h2:file:./data/dbname`
   - Données persistées sur disque
   - Utile pour développement local

3. **Serveur** : `jdbc:h2:tcp://localhost/~/dbname`
   - Plusieurs applications peuvent se connecter
   - Rarement utilisé

#### Migration vers PostgreSQL (Module 4)

**PostgreSQL** est la base de données **production** que nous utiliserons à partir du Module 4.

**Pourquoi PostgreSQL ?**

| Avantage | Description |
|----------|-------------|
| ✅ **Données persistantes** | Survit aux redémarrages |
| ✅ **Production-ready** | Utilisé par millions d'applications |
| ✅ **ACID complet** | Garanties transactionnelles solides |
| ✅ **Fonctionnalités avancées** | JSON, Full-text search, GIS, etc. |
| ✅ **Performance** | Optimisations pour grandes volumétries |
| ✅ **Outils** | pgAdmin, DBeaver, extensions riches |

**Migration H2 → PostgreSQL** :

Au Module 4, nous changerons simplement la configuration :

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/catalogue_db
    driver-class-name: org.postgresql.Driver
    username: postgres
    password: your_password

  jpa:
    hibernate:
      ddl-auto: validate  # ⚠️ Ne plus utiliser create-drop !
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

**Gestion du schéma en production** :

| Outil | Usage |
|-------|-------|
| **Liquibase** (Module 4) | Migrations versionnées (recommandé) |
| **Flyway** | Alternative à Liquibase |
| `ddl-auto: validate` | Vérifie que le schéma correspond aux entités |

**Différences SQL H2 vs PostgreSQL** :

```sql
-- H2 : AUTO_INCREMENT
CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY
);

-- PostgreSQL : SERIAL ou IDENTITY
CREATE TABLE products (
  id BIGSERIAL PRIMARY KEY
);
```

**💡 Bonne pratique** : JPA/Hibernate abstrait ces différences. Si vous utilisez uniquement `@GeneratedValue(strategy = GenerationType.IDENTITY)`, votre code fonctionnera sur H2 **et** PostgreSQL sans changement.

#### Stratégie de Développement

**Phase 1 (Modules 2-3)** : H2
- Focus sur JPA, REST, tests
- Pas de friction infrastructure
- Cycle de développement rapide

**Phase 2 (Module 4)** : PostgreSQL + Docker
- Environnement proche production
- Migrations avec Liquibase
- Performance réelle

**Phase 3 (Modules 10+)** : Multi-environnements
- Profils Spring (`dev`, `test`, `prod`)
- H2 pour tests automatisés
- PostgreSQL pour dev local et production

> **Principe** : Commencez simple (H2), évoluez vers robuste (PostgreSQL) au bon moment.

---

### 3.1. Entity Lifecycle

**États d'une entité** :

```
    NEW (Transient)
         ↓ persist()
    MANAGED (Persistent)  ←─┐ find() / getReference()
         ↓ commit()          │
    DETACHED               ─┘ merge()
         ↓ remove()
    REMOVED
```

**1. NEW/Transient** : Objet créé mais pas géré par JPA

```java
Product product = new Product();  // Transient
product.setName("Laptop");
```

**2. MANAGED/Persistent** : Géré par EntityManager, changements trackés

```java
Product product = entityManager.persist(new Product());  // Managed
product.setPrice(999.99);  // Changement tracké, sera sauvegardé automatiquement
```

**3. DETACHED** : Était géré, ne l'est plus (après close() ou clear())

```java
entityManager.close();
// product est maintenant Detached
product.setPrice(899.99);  // Changement PAS tracké

// Re-attacher
Product managed = entityManager.merge(product);  // Retourne une nouvelle instance Managed
```

**4. REMOVED** : Marqué pour suppression

```java
entityManager.remove(product);  // Removed, sera supprimé au commit
```

### 3.2. Persistence Context et Cache de Niveau 1

**Persistence Context** = Cache de niveau 1 (L1 cache) géré par l'EntityManager.

**Caractéristiques** :

- ✅ Automatique (pas de configuration)
- ✅ Scope = transaction (vidé après commit)
- ✅ Évite les requêtes redondantes
- ✅ Garantit l'unicité des entités (same ID = same instance)

**Exemple** :

```java
@Transactional
public void example() {
    Product p1 = entityManager.find(Product.class, 1L);  // SELECT en base
    Product p2 = entityManager.find(Product.class, 1L);  // PAS de SELECT (cache L1)

    System.out.println(p1 == p2);  // true (même instance)
}
```

**Write-Behind** : Les modifications sont bufferisées et envoyées à la fin :

```java
@Transactional
public void example() {
    Product product = entityManager.find(Product.class, 1L);  // SELECT
    product.setPrice(999.99);  // Pas de UPDATE tout de suite
    product.setName("Laptop");  // Toujours pas d'UPDATE

    // Commit → 1 seul UPDATE avec tous les changements
}
```

**Flush** : Force l'envoi des changements en base :

```java
product.setPrice(999.99);
entityManager.flush();  // Force UPDATE maintenant (sans commit)
```

### 3.3. Relations JPA

#### @ManyToOne / @OneToMany

**Product → Category (Many-to-One)** :

```java
@Entity
public class Product extends AuditedEntity {
    @ManyToOne(fetch = FetchType.LAZY)  // Recommandé : LAZY
    @JoinColumn(name = "category_id")   // Nom de la FK
    private Category category;
}

@Entity
public class Category extends AuditedEntity {
    @OneToMany(mappedBy = "category")   // "category" = champ dans Product
    private List<Product> products = new ArrayList<>();
}
```

**Fetch Types** :

| Type | Comportement | Usage |
|------|-------------|-------|
| `LAZY` | Charge la relation à la demande (lors de l'accès) | Par défaut pour @OneToMany, @ManyToMany - Recommandé |
| `EAGER` | Charge immédiatement avec l'entité | Par défaut pour @ManyToOne, @OneToOne - Souvent problématique |

**Problème N+1 avec LAZY** :

```java
List<Product> products = productRepository.findAll();  // 1 requête

for (Product p : products) {
    System.out.println(p.getCategory().getName());  // N requêtes (1 par produit)
}

// Total : 1 + N requêtes = problème de performance
```

**Solutions** :

**1. JOIN FETCH** :

```java
@Query("SELECT p FROM Product p JOIN FETCH p.category")
List<Product> findAllWithCategory();

// 1 seule requête avec LEFT JOIN
```

**2. Entity Graph** :

```java
@EntityGraph(attributePaths = {"category"})
List<Product> findAll();
```

**3. @BatchSize** :

```java
@OneToMany(mappedBy = "category")
@BatchSize(size = 10)  // Charge par batch de 10 au lieu de 1 par 1
private List<Product> products;
```

#### @ManyToMany

**Exemple : Product ↔ Tag** :

```java
@Entity
public class Product extends AuditedEntity {
    @ManyToMany
    @JoinTable(
        name = "product_tags",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
}

@Entity
public class Tag {
    @ManyToMany(mappedBy = "tags")
    private Set<Product> products = new HashSet<>();
}
```

**Table de liaison générée** :

```sql
CREATE TABLE product_tags (
    product_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, tag_id)
);
```

**Ajouter/retirer des relations** :

```java
Product product = productRepository.findById(1L).orElseThrow();
Tag tag = tagRepository.findById(5L).orElseThrow();

product.getTags().add(tag);      // Ajouter
productRepository.save(product); // Sauvegarde la relation

product.getTags().remove(tag);   // Retirer
productRepository.save(product);
```

### 3.4. Cascade Types

**CascadeType** définit quelles opérations se propagent aux entités liées.

```java
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> items = new ArrayList<>();
```

| Type | Comportement |
|------|-------------|
| `PERSIST` | persist() se propage |
| `MERGE` | merge() se propage |
| `REMOVE` | remove() se propage |
| `REFRESH` | refresh() se propage |
| `DETACH` | detach() se propage |
| `ALL` | Toutes les opérations se propagent |

**Exemple** :

```java
Order order = new Order();
order.addItem(new OrderItem());
order.addItem(new OrderItem());

entityManager.persist(order);  // Avec CASCADE.PERSIST, les items sont aussi persistés
```

**orphanRemoval** : Supprime automatiquement les enfants orphelins

```java
order.getItems().remove(0);  // L'OrderItem est supprimé en base automatiquement
```

### 3.5. Optimistic Locking

**Problème** : Deux utilisateurs modifient la même entité simultanément.

**Solution** : Versioning avec `@Version`.

```java
@Entity
public class Product extends AuditedEntity {
    @Version
    private Long version;  // Incrémenté automatiquement à chaque UPDATE
}
```

**Scénario** :

```
User 1                          User 2
------                          ------
SELECT ... WHERE id=1           SELECT ... WHERE id=1
  → version=5                     → version=5

UPDATE ... SET version=6        (attend)
  WHERE id=1 AND version=5
  → OK

                                UPDATE ... SET version=6
                                  WHERE id=1 AND version=5
                                  → FAIL (version déjà 6)
                                  → OptimisticLockException
```

**Gérer l'exception** :

```java
try {
    productRepository.save(product);
} catch (OptimisticLockException e) {
    // Relire l'entité et ré-appliquer les changements
    Product latest = productRepository.findById(product.getId()).orElseThrow();
    latest.setPrice(product.getPrice());
    productRepository.save(latest);
}
```

### 3.6. Auditing Automatique

**JPA Auditing** : Gère automatiquement `createdAt`, `updatedAt`, `createdBy`, `updatedBy`.

**Configuration** :

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(SecurityContextHolder.getContext()
            .getAuthentication()
            .getName());  // User connecté
    }
}
```

**Hiérarchie d'entités avec génériques** :

**BaseEntity** (ID + Persistable) :

```java
package ma.ensaf.ecommerce.common.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Persistable;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(of = "id")
public abstract class BaseEntity<ID> implements Persistable<ID> {

    @Id
    @GeneratedValue
    private ID id;

    @Override
    public boolean isNew() {
        return getId() == null;
    }
}
```

**Points clés de BaseEntity** :

- **`<ID>` générique** : Permet de varier le type d'ID (`Long`, `UUID`, etc.)
- **`implements Persistable<ID>`** : Optimise la détection des nouvelles entités par Spring Data
- **`isNew()`** : Spring utilise cette méthode pour décider entre `INSERT` ou `UPDATE`
- **`@EqualsAndHashCode(of = "id")`** : Comparaison basée sur l'ID par défaut

**AuditedEntity** (Audit automatique + génériques) :

```java
package ma.ensaf.ecommerce.common.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditedEntity<ID> extends BaseEntity<ID> {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
```

**Points clés de AuditedEntity** :

- **`<ID>` générique propagé** : `extends BaseEntity<ID>`
- **`@EntityListeners(AuditingEntityListener.class)`** : Active l'auditing automatique Spring Data
- **`@CreatedDate`, `@LastModifiedDate`** : Spring remplit automatiquement ces champs
- **`@CreatedBy`, `@LastModifiedBy`** : Remplis via `AuditorAware` (utilisateur connecté)
- **`@Getter @Setter @ToString`** : Bonne pratique avec héritage (évite conflits equals/hashCode)

**Utilisation** :

```java
@Entity
@Table(name = "products")
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends AuditedEntity<Long> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private Double price;

    private Integer stockQuantity;
    private String category;
    private boolean available = true;
}
```

**Avantages de cette hiérarchie** :

- ✅ **Génériques** : Flexibilité du type d'ID (Long, UUID, etc.)
- ✅ **Persistable** : Optimisation de `save()` (INSERT vs UPDATE)
- ✅ **Audit automatique** : Pas besoin de `@PrePersist`/`@PreUpdate`
- ✅ **Traçabilité** : Sait qui a créé/modifié et quand
- ✅ **Réutilisable** : Toutes les entités héritent de l'auditing

### 3.7. Query Methods avancés

**Mots-clés Spring Data JPA** :

| Mot-clé | Exemple | JPQL généré |
|---------|---------|-------------|
| `findBy` | `findByName(String name)` | `WHERE name = ?1` |
| `And`, `Or` | `findByNameAndPrice(...)` | `WHERE name = ?1 AND price = ?2` |
| `LessThan`, `GreaterThan` | `findByPriceLessThan(Double price)` | `WHERE price < ?1` |
| `Between` | `findByPriceBetween(Double min, Double max)` | `WHERE price BETWEEN ?1 AND ?2` |
| `Like` | `findByNameLike(String pattern)` | `WHERE name LIKE ?1` |
| `IgnoreCase` | `findByNameIgnoreCase(String name)` | `WHERE UPPER(name) = UPPER(?1)` |
| `OrderBy` | `findByOrderByPriceAsc()` | `ORDER BY price ASC` |
| `Containing` | `findByNameContaining(String keyword)` | `WHERE name LIKE %?1%` |
| `StartingWith` | `findByNameStartingWith(String prefix)` | `WHERE name LIKE ?1%` |
| `EndingWith` | `findByNameEndingWith(String suffix)` | `WHERE name LIKE %?1` |
| `In` | `findByCategoryIn(List<String> cats)` | `WHERE category IN (?1)` |
| `IsNull`, `IsNotNull` | `findByDescriptionIsNull()` | `WHERE description IS NULL` |
| `True`, `False` | `findByAvailableTrue()` | `WHERE available = TRUE` |
| `First`, `Top` | `findFirst10ByOrderByPriceDesc()` | `LIMIT 10` |

**Exemples** :

```java
// Pagination et tri
Page<Product> findByCategory(String category, Pageable pageable);

// Usage
Pageable pageable = PageRequest.of(0, 20, Sort.by("price").descending());
Page<Product> page = productRepository.findByCategory("Electronics", pageable);

// Projection (DTO)
interface ProductSummary {
    Long getId();
    String getName();
    Double getPrice();
}

List<ProductSummary> findByCategory(String category);

// @Query personnalisée
@Query("SELECT p FROM Product p WHERE p.price > :minPrice AND p.stockQuantity > 0")
List<Product> findAvailableProductsAbovePrice(@Param("minPrice") Double minPrice);

// Native SQL
@Query(value = "SELECT * FROM products WHERE MATCH(name, description) AGAINST (?1)", nativeQuery = true)
List<Product> fullTextSearch(String keyword);
```

---

## 4. Service Layer - Patterns et Transactions

### 4.1. Responsabilités du Service

**Service Layer** = Logique métier.

**Responsabilités** :

- ✅ Validation métier (au-delà de la validation technique)
- ✅ Orchestration de plusieurs repositories
- ✅ Transactions
- ✅ Logique métier complexe
- ✅ Communication avec d'autres services (OpenFeign)
- ❌ Pas de logique HTTP (headers, status codes) → c'est le rôle du Controller

**Exemple** :

```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EventPublisher eventPublisher;  // Publication d'événements

    public Product create(Product product) {
        log.info("Creating product: {}", product.getName());

        // 1. Validation métier
        validateProduct(product);

        // 2. Vérifier que la catégorie existe
        Category category = categoryRepository.findById(product.getCategoryId())
            .orElseThrow(() -> new BusinessException("Category not found"));

        if (category.isArchived()) {
            throw new BusinessException("Cannot add product to archived category");
        }

        // 3. Vérifier unicité SKU
        if (productRepository.existsBySku(product.getSku())) {
            throw new DuplicateSkuException("SKU already exists: " + product.getSku());
        }

        // 4. Sauvegarder
        Product saved = productRepository.save(product);

        // 5. Publier événement
        eventPublisher.publish(new ProductCreatedEvent(saved));

        return saved;
    }

    private void validateProduct(Product product) {
        if (product.getPrice() < 0) {
            throw new ValidationException("Price cannot be negative");
        }
        if (product.getStockQuantity() != null && product.getStockQuantity() < 0) {
            throw new ValidationException("Stock cannot be negative");
        }
    }

    @Transactional(readOnly = true)  // Optimisation pour lectures
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
}
```

### 4.2. Gestion des Transactions

**@Transactional** : Gère automatiquement les transactions.

**Configuration par défaut** :

```java
@Transactional
// Équivaut à :
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.DEFAULT,
    timeout = -1,  // Pas de timeout
    readOnly = false,
    rollbackFor = RuntimeException.class
)
```

**Propagation Types** :

| Type | Comportement |
|------|-------------|
| `REQUIRED` (défaut) | Utilise la transaction existante, en crée une si besoin |
| `REQUIRES_NEW` | Suspend la transaction courante, en crée une nouvelle |
| `SUPPORTS` | Utilise la transaction si elle existe, sinon pas de transaction |
| `NOT_SUPPORTED` | Suspend la transaction courante, exécute sans transaction |
| `MANDATORY` | Utilise la transaction existante, erreur si aucune |
| `NEVER` | Exécute sans transaction, erreur si une existe |

**Exemple REQUIRES_NEW** :

```java
@Transactional
public void processOrder(Order order) {
    // Transaction 1
    orderRepository.save(order);

    // Transaction 2 (indépendante)
    auditService.logOrderCreated(order);  // Même si processOrder rollback, le log reste
}

@Service
public class AuditService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logOrderCreated(Order order) {
        auditRepository.save(new AuditLog("ORDER_CREATED", order.getId()));
    }
}
```

**Isolation Levels** :

| Level | Dirty Read | Non-Repeatable Read | Phantom Read |
|-------|-----------|---------------------|--------------|
| `READ_UNCOMMITTED` | ✅ | ✅ | ✅ |
| `READ_COMMITTED` (défaut PostgreSQL) | ❌ | ✅ | ✅ |
| `REPEATABLE_READ` (défaut MySQL) | ❌ | ❌ | ✅ |
| `SERIALIZABLE` | ❌ | ❌ | ❌ |

**Rollback** :

```java
@Transactional(rollbackFor = Exception.class)  // Rollback même pour checked exceptions
public void process() throws IOException {
    // ...
}

@Transactional(noRollbackFor = SpecificException.class)  // Pas de rollback pour cette exception
public void process() {
    // ...
}
```

**Rollback manuel** :

```java
@Transactional
public void process() {
    try {
        // ...
    } catch (Exception e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        throw e;
    }
}
```

### 4.3. Design Patterns pour Services

#### 1. Service Layer Pattern

**Problème** : Séparer la logique métier de la logique de présentation.

**Solution** : Service Layer entre Controller et Repository.

```
Controller → Service → Repository → Database
```

#### 2. DTO (Data Transfer Object) Pattern

**Problème** : Ne pas exposer les entités JPA directement (évite lazy loading issues, coupling).

**Solution** : Mapper Entity ↔ DTO.

```java
// Entity (interne)
@Entity
public class Product extends AuditedEntity {
    private String name;
    private Double price;
    private Category category;  // Lazy
}

// DTO (API)
public record ProductDto(
    Long id,
    String name,
    Double price,
    String categoryName
) {}

// Mapper
@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.name", target = "categoryName")
    ProductDto toDto(Product product);

    Product toEntity(ProductDto dto);
}

// Service
public ProductDto getProduct(Long id) {
    Product product = productRepository.findById(id).orElseThrow();
    return productMapper.toDto(product);  // Pas de lazy loading issue
}
```

#### 3. Repository Pattern

**Déjà implémenté par Spring Data JPA** :

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Spring génère l'implémentation
}
```

#### 4. Specification Pattern (Criteria API)

**Problème** : Construire des requêtes dynamiques complexes.

**Solution** : JPA Criteria API via Spring Data Specifications.

```java
public interface ProductRepository extends JpaRepository<Product, Long>,
                                          JpaSpecificationExecutor<Product> {}

// Specification
public class ProductSpecifications {
    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    public static Specification<Product> priceBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("price"), min, max);
    }

    public static Specification<Product> isAvailable() {
        return (root, query, cb) -> cb.isTrue(root.get("available"));
    }
}

// Usage
Specification<Product> spec = Specification
    .where(hasCategory("Electronics"))
    .and(priceBetween(100.0, 500.0))
    .and(isAvailable());

List<Product> products = productRepository.findAll(spec);
```

---

## 5. REST Controller - Best Practices

### 5.1. Controller vs RestController

```java
@Controller  // Retourne des vues (Thymeleaf, JSP)
public class WebController {
    @GetMapping("/products")
    public String listProducts(Model model) {
        return "products";  // Template name
    }
}

@RestController  // = @Controller + @ResponseBody (retourne JSON)
public class ProductController {
    @GetMapping("/api/products")
    public List<Product> listProducts() {
        return productService.findAll();  // Auto-sérialisé en JSON
    }
}
```

### 5.2. Validation avec Bean Validation

**Dépendance** :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**Entity/DTO avec contraintes** :

```java
public record CreateProductRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String name,

    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Z]{3}-\\d{3}$", message = "SKU format: XXX-123")
    String sku,

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    @Digits(integer = 8, fraction = 2, message = "Price max 8 digits, 2 decimals")
    BigDecimal price,

    @Min(value = 0, message = "Stock cannot be negative")
    Integer stockQuantity,

    @NotNull(message = "Category ID is required")
    Long categoryId
) {}
```

**Controller** :

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public ProductDto createProduct(@Valid @RequestBody CreateProductRequest request) {
    return productService.create(request);
}
```

**Gestion des erreurs de validation** :

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage
            ));

        return Map.of(
            "timestamp", LocalDateTime.now(),
            "status", 400,
            "error", "Bad Request",
            "message", "Validation failed",
            "errors", errors
        );
    }
}
```

**Réponse d'erreur** :

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "name": "Name is required",
    "price": "Price must be positive",
    "sku": "SKU format: XXX-123"
  }
}
```

**Contraintes courantes** :

| Annotation | Usage |
|-----------|-------|
| `@NotNull` | Ne doit pas être null |
| `@NotBlank` | Ne doit pas être null, vide ou whitespace |
| `@NotEmpty` | Ne doit pas être null ou vide (Collections, String) |
| `@Size(min, max)` | Taille entre min et max |
| `@Min(value)` | Valeur minimale |
| `@Max(value)` | Valeur maximale |
| `@DecimalMin` | Nombre décimal minimum |
| `@DecimalMax` | Nombre décimal maximum |
| `@Digits(integer, fraction)` | Nombre de digits |
| `@Pattern(regexp)` | Regex |
| `@Email` | Email valide |
| `@Past` | Date passée |
| `@Future` | Date future |
| `@Positive` | Nombre positif |
| `@PositiveOrZero` | Nombre positif ou zéro |
| `@Negative` | Nombre négatif |

### 5.3. Exception Handling Global

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 404 - Ressource non trouvée
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage()
        );
    }

    // 409 - Conflit (SKU déjà existant)
    @ExceptionHandler(DuplicateSkuException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateSku(DuplicateSkuException ex) {
        log.warn("Duplicate SKU: {}", ex.getMessage());
        return new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage()
        );
    }

    // 422 - Validation métier
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleBusinessException(BusinessException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());
        return new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Business Rule Violation",
            ex.getMessage()
        );
    }

    // 500 - Erreur interne
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        log.error("Internal server error", ex);
        return new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred"
        );
    }
}

public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message
) {}
```

### 5.4. CORS Configuration

**Problème** : Le frontend (localhost:3000) ne peut pas appeler le backend (localhost:8081) par défaut.

**Solution CORS** :

**Option 1 : Annotation sur le controller** :

```java
@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "http://localhost:3000")  // Frontend React
public class ProductController { }
```

**Option 2 : Configuration globale** (recommandé) :

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000", "https://app.example.com")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

**Headers CORS ajoutés** :

```http
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Access-Control-Allow-Headers: *
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

### 5.5. Pagination et Tri

```java
@GetMapping
public Page<ProductDto> getAllProducts(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size,
    @RequestParam(defaultValue = "id,asc") String[] sort
) {
    // Parse sort: ["price,desc", "name,asc"]
    List<Sort.Order> orders = Arrays.stream(sort)
        .map(s -> {
            String[] parts = s.split(",");
            String property = parts[0];
            Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
            return new Sort.Order(direction, property);
        })
        .toList();

    Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
    Page<Product> productPage = productRepository.findAll(pageable);

    return productPage.map(productMapper::toDto);
}
```

**Requête** :

```
GET /api/v1/products?page=2&size=10&sort=price,desc&sort=name,asc
```

**Réponse** :

```json
{
  "content": [ ... ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 2,
    "pageSize": 10,
    "offset": 20,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 15,
  "totalElements": 150,
  "last": false,
  "first": false,
  "size": 10,
  "number": 2,
  "numberOfElements": 10,
  "empty": false
}
```

### 5.6. Content Negotiation

**Controller** :

```java
@GetMapping(value = "/{id}", produces = {
    MediaType.APPLICATION_JSON_VALUE,
    MediaType.APPLICATION_XML_VALUE
})
public Product getProduct(@PathVariable Long id) {
    return productService.findById(id).orElseThrow();
}
```

**Request JSON** :

```http
GET /api/v1/products/1
Accept: application/json

→ {"id": 1, "name": "Laptop", ...}
```

**Request XML** :

```http
GET /api/v1/products/1
Accept: application/xml

→ <Product><id>1</id><name>Laptop</name>...</Product>
```

---

## 6. Tests - Stratégies Complètes

### 6.1. Pyramide de Tests

```
       /\
      /  \     E2E Tests (5%)
     /────\
    /      \   Integration Tests (15%)
   /────────\
  /          \ Unit Tests (80%)
 /────────────\
```

### 6.2. Tests Repository (@DataJpaTest)

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // Utilise H2 par défaut
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private TestEntityManager entityManager;  // Pour manipulations avancées

    @Test
    void shouldSaveAndGenerateId() {
        // Arrange
        Product product = Product.builder()
            .name("Laptop")
            .sku("LAP-001")
            .price(999.99)
            .build();

        // Act
        Product saved = repository.save(product);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindBySkuCaseInsensitive() {
        // Arrange
        entityManager.persist(Product.builder().sku("LAP-001").name("Laptop").build());
        entityManager.flush();

        // Act
        Optional<Product> found = repository.findBySku("lap-001");

        // Assert - utilise AssertJ
        assertThat(found)
            .isPresent()
            .get()
            .satisfies(p -> {
                assertThat(p.getSku()).isEqualToIgnoringCase("LAP-001");
                assertThat(p.getName()).isEqualTo("Laptop");
            });
    }

    @Test
    void shouldFindByPriceRange() {
        // Arrange
        repository.saveAll(List.of(
            Product.builder().name("Cheap").price(50.0).build(),
            Product.builder().name("Medium").price(150.0).build(),
            Product.builder().name("Expensive").price(500.0).build()
        ));

        // Act
        List<Product> products = repository.findByPriceBetween(100.0, 200.0);

        // Assert
        assertThat(products)
            .hasSize(1)
            .extracting(Product::getName)
            .containsExactly("Medium");
    }

    @Test
    void shouldRespectUniqueConstraintOnSku() {
        // Arrange
        repository.save(Product.builder().sku("LAP-001").name("Laptop 1").build());

        // Act & Assert
        assertThatThrownBy(() -> {
            repository.save(Product.builder().sku("LAP-001").name("Laptop 2").build());
            entityManager.flush();  // Force flush pour déclencher l'exception
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
```

**Avantages @DataJpaTest** :

- ✅ Configure H2 en mémoire automatiquement
- ✅ Rollback automatique après chaque test
- ✅ Charge uniquement la couche JPA (rapide)
- ✅ Pas besoin de mocker le repository (vraie base)

### 6.3. Tests Service (Mockito)

> **Note** : Les tests Mockito seront couverts en détail au **Module 7**. Voici un aperçu.

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldCreateProduct() {
        // Arrange
        Product product = Product.builder()
            .name("Laptop")
            .sku("LAP-001")
            .price(999.99)
            .categoryId(1L)
            .build();

        Category category = Category.builder()
            .id(1L)
            .name("Electronics")
            .archived(false)
            .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsBySku("LAP-001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.create(product);

        // Assert
        assertThat(result).isNotNull();
        verify(productRepository).save(product);
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        // Arrange
        Product product = Product.builder().categoryId(999L).build();
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.create(product))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Category not found");
    }
}
```

### 6.4. Tests Controller (MockMvc)

> **Note** : Les tests MockMvc seront couverts en détail au **Module 7**.

```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void shouldReturnProductById() throws Exception {
        // Arrange
        Product product = Product.builder()
            .id(1L)
            .name("Laptop")
            .price(999.99)
            .build();

        when(productService.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Laptop"))
            .andExpect(jsonPath("$.price").value(999.99));
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        when(productService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/products/999"))
            .andExpect(status().isNotFound());
    }
}
```

---

## 7. Performance et Optimisations

### 7.1. N+1 Problem

**Problème** :

```java
List<Product> products = productRepository.findAll();  // 1 query

for (Product p : products) {
    System.out.println(p.getCategory().getName());  // N queries
}
```

**Solution 1 : JOIN FETCH** :

```java
@Query("SELECT p FROM Product p LEFT JOIN FETCH p.category")
List<Product> findAllWithCategory();
```

**Solution 2 : @EntityGraph** :

```java
@EntityGraph(attributePaths = {"category"})
List<Product> findAll();
```

### 7.2. Caching avec Spring Cache

**Configuration** :

```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("products", "categories");
    }
}
```

**Service** :

```java
@Service
public class ProductService {

    @Cacheable(value = "products", key = "#id")
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @CachePut(value = "products", key = "#result.id")
    public Product update(Product product) {
        return productRepository.save(product);
    }

    @CacheEvict(value = "products", key = "#id")
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void clearCache() {
        // Cache vidé
    }
}
```

### 7.3. Projections et DTOs

**Au lieu de** :

```java
List<Product> products = productRepository.findAll();  // Charge TOUTES les colonnes
```

**Utilisez une projection** :

```java
interface ProductSummary {
    Long getId();
    String getName();
    Double getPrice();
}

List<ProductSummary> productRepository.findAllBy();  // SELECT id, name, price only
```

---

## 📚 Récapitulatif

### Ce que vous avez appris

1. **Architecture REST** : Principes fondamentaux, HATEOAS, codes HTTP
2. **Spring Boot** : Auto-configuration, IoC/DI, configuration externalisée
3. **JPA/Hibernate** : Entity lifecycle, cache L1, relations, transactions
4. **Service Layer** : Patterns, transactions, isolation levels
5. **REST Controller** : Validation, exception handling, CORS, pagination
6. **Tests** : @DataJpaTest, Mockito, MockMvc
7. **Performance** : N+1 problem, caching, projections

### Architecture Finale

```
Client (React/Mobile)
        ↓ HTTP/JSON
API Gateway (Port 8080)
        ↓
┌───────────────────────────────┐
│  Catalogue Service (8081)     │
│  ┌─────────────────────────┐  │
│  │  Controller Layer       │  │
│  │  @RestController        │  │
│  └──────────┬──────────────┘  │
│             ↓                 │
│  ┌─────────────────────────┐  │
│  │  Service Layer          │  │
│  │  @Service @Transactional│  │
│  └──────────┬──────────────┘  │
│             ↓                 │
│  ┌─────────────────────────┐  │
│  │  Repository Layer       │  │
│  │  JpaRepository          │  │
│  └──────────┬──────────────┘  │
│             ↓                 │
│  ┌─────────────────────────┐  │
│  │  Database (PostgreSQL)  │  │
│  └─────────────────────────┘  │
└───────────────────────────────┘
```

### Points Clés

1. **Stateless** : Pas de session, JWT pour l'authentification
2. **Versionning** : `/api/v1/products`
3. **Validation** : Bean Validation + validation métier
4. **Transactions** : @Transactional sur les services
5. **Exception Handling** : @RestControllerAdvice
6. **Tests** : @DataJpaTest pour repositories
7. **Performance** : JOIN FETCH, cache, projections

---

**🔗 Pour démarrer rapidement** : Consultez `cours-essentiel.md`

**Prochaine étape** : Module 3 - Documentation Swagger et Gestion d'erreurs avancée
