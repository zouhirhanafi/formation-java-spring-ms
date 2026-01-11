# Module 2 : Premier Microservice - Service Catalogue (Guide Essentiel)

**Durée** : 2h théorie
**Objectif** : Créer votre premier microservice avec Spring Boot, JPA et REST

> 💡 **Note** : Ce guide contient l'essentiel. Pour approfondir, consultez `cours-complet.md`

---

## 🎯 Ce que vous allez apprendre

- ✅ **Architecture Web** : Comprendre Backend/Frontend et HTTP/REST
- ✅ **Spring Boot** : Créer un projet rapidement avec convention over configuration
- ✅ **JPA & H2** : Persister des données avec une base en mémoire
- ✅ **REST API** : Créer des endpoints CRUD respectant les normes
- ✅ **Tests** : Tester un service Spring Boot

---

## 1. Architecture Web & REST

### Architecture Backend / Frontend

```
┌────────────────────────────────────────────────────────┐
│                    CLIENT (Frontend)                   │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Navigateur Web / Application Mobile / SPA       │  │
│  │  (React, Angular, Vue, Flutter, etc.)            │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────┬───────────────────────────────────┘
                     │ HTTP/HTTPS
                     │ (JSON, XML)
                     ▼
┌────────────────────────────────────────────────────────┐
│                   SERVEUR (Backend)                    │
│  ┌──────────────────────────────────────────────────┐  │
│  │  REST API (Spring Boot)                          │  │
│  │  - Controllers (endpoints HTTP)                  │  │
│  │  - Services (logique métier)                     │  │
│  │  - Repositories (accès données)                  │  │
│  └──────────────────────────────────────────────────┘  │
│                       │                                │
│                       ▼                                │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Base de Données (PostgreSQL, MySQL, etc.)       │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘
```

**Séparation des responsabilités** :

- **Frontend** : Interface utilisateur, expérience utilisateur (UX)
- **Backend** : Logique métier, accès données, sécurité
- **Communication** : HTTP/HTTPS avec JSON

### REST (Representational State Transfer)

**REST** = Style d'architecture pour les API web basé sur HTTP.

**Principes clés** :

1. **Ressources** : Tout est une ressource (Product, User, Order)
2. **HTTP Methods** : Utiliser les verbes HTTP correctement
3. **Stateless** : Chaque requête est indépendante (pas de session)
4. [**JSON**](https://www.json.org/json-fr.html) : Format d'échange standard

**Normes des API REST** :

| Opération | HTTP Method | URL | Body | Réponse |
|-----------|-------------|-----|------|---------|
| Lister tous | GET | `/api/v1/products` | ❌ | `200 OK` + liste |
| Lire un | GET | `/api/v1/products/1` | ❌ | `200 OK` + objet ou `404 Not Found` |
| Créer | POST | `/api/v1/products` | ✅ JSON | `201 Created` + objet créé |
| Modifier complet | PUT | `/api/v1/products/1` | ✅ JSON | `200 OK` + objet modifié |
| Modifier partiel | PATCH | `/api/v1/products/1` | ✅ JSON | `200 OK` + objet modifié |
| Supprimer | DELETE | `/api/v1/products/1` | ❌ | `204 No Content` |

**Bonnes pratiques** :

- ✅ **Versionner l'API** : `/api/v1/products` (permet d'évoluer sans casser les clients existants)
- ✅ Utiliser des **noms au pluriel** : `/api/v1/products` (pas `/api/v1/product`)
- ✅ Utiliser des **noms de ressources**, pas des verbes : `/api/v1/products` (pas `/api/v1/getProducts`)
- ✅ Utiliser les **query params** pour filtres/recherche : `/api/v1/products?category=Electronics&minPrice=100`
- ✅ Utiliser les **path params** pour identifiants : `/api/v1/products/1`

### Codes de Statut HTTP

**2xx - Succès** :

| Code | Nom | Usage | Exemple |
|------|-----|-------|---------|
| 200 | OK | Requête réussie (GET, PUT) | Récupérer un produit |
| 201 | Created | Ressource créée (POST) | Créer un produit |
| 204 | No Content | Succès sans contenu (DELETE) | Supprimer un produit |

**4xx - Erreur Client** :

| Code | Nom | Usage | Exemple |
|------|-----|-------|---------|
| 400 | Bad Request | Requête mal formée | JSON invalide |
| 401 | Unauthorized | Non authentifié | Pas de token JWT |
| 403 | Forbidden | Pas les droits | User tente action ADMIN |
| 404 | Not Found | Ressource inexistante | Produit ID 999 n'existe pas |
| 409 | Conflict | Conflit de données | SKU déjà existant |
| 422 | Unprocessable Entity | Données invalides | Prix négatif |

**5xx - Erreur Serveur** :

| Code | Nom | Usage | Exemple |
|------|-----|-------|---------|
| 500 | Internal Server Error | Erreur interne | Exception non gérée |
| 503 | Service Unavailable | Service indisponible | Base de données down |

**Exemple de flux complet** :

```
Client: POST /api/v1/products
        Body: {"name": "Laptop", "sku": "LAP-001", "price": 999.99}

Server: 201 Created
        Body: {"id": 1, "name": "Laptop", "sku": "LAP-001", "price": 999.99, ...}

Client: GET /api/v1/products/1

Server: 200 OK
        Body: {"id": 1, "name": "Laptop", ...}

Client: DELETE /api/v1/products/1

Server: 204 No Content
```

---

## 2. Spring Boot - Les Bases

### Qu'est-ce que Spring Boot ?

**Spring Boot** = Framework qui simplifie la création d'applications Spring :

- ✅ **Auto-configuration** : Configure automatiquement votre application
- ✅ **Starter dependencies** : Dépendances pré-packagées
- ✅ **Serveur embarqué** : Tomcat/Jetty inclus, pas besoin de WAR
- ✅ **Production-ready** : Métriques, health checks inclus

**Configuration simplifiée** :

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
```

Spring Boot remplace des dizaines de lignes de configuration XML par quelques lignes YAML simples.

### Structure d'un Projet Spring Boot

```text
catalogue-service/
├── src/main/java/ma/ensaf/ecommerce/catalogue/
│   ├── CatalogueServiceApplication.java    # Point d'entrée
│   ├── controller/
│   │   └── ProductController.java          # REST endpoints
│   ├── service/
│   │   └── ProductService.java             # Logique métier
│   ├── repository/
│   │   └── ProductRepository.java          # Accès données
│   └── model/
│       └── Product.java                    # Entité JPA
├── src/main/resources/
│   ├── application.yml                     # Configuration
│   └── data.sql                            # Données initiales (optionnel)
└── src/test/java/
    └── ...                                 # Tests
```

### Classe Principale

```java
package ma.ensaf.ecommerce.catalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatalogueServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogueServiceApplication.class, args);
    }
}
```

**@SpringBootApplication** = 3 annotations en 1 :

- `@Configuration` : Classe de configuration
- `@EnableAutoConfiguration` : Active l'auto-configuration
- `@ComponentScan` : Scanne les composants du package

---

## 2. JPA & Base de Données H2

### JPA (Java Persistence API)

**JPA** = Spécification pour mapper des objets Java vers des tables relationnelles (ORM).

**Hibernate** = Implémentation de JPA (la plus populaire).

### Entité JPA

**Version Simple** (pour démarrer) :

```java
package ma.ensaf.ecommerce.catalogue.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private Double price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    private String category;

    private boolean available = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

> ⚠️ **Important** : Utilisez toujours `@SuperBuilder` au lieu de `@Builder` pour supporter l'héritage.

**Version Recommandée** (avec héritage - approche professionnelle) :

Créer une hiérarchie d'entités dans le module `common` :

**Étape 1 : BaseEntity** (juste l'ID + génériques)

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

**Explication** :

- **`<ID>` (générique)** : Permet de varier le type d'ID (`Long`, `UUID`, etc.). Applique les **génériques** vus au Module 1.
- **`implements Persistable<ID>`** : Interface Spring Data qui optimise la détection des nouvelles entités.
- **`isNew()`** : Retourne `true` si l'entité n'a pas encore d'ID (avant `save()`). Spring utilise cette méthode pour décider entre `INSERT` (nouvelle entité) ou `UPDATE` (entité existante).
- **`@GeneratedValue`** sans stratégie : Laisse JPA choisir la stratégie par défaut (AUTO).

**Étape 2 : AuditedEntity** (ajoute l'audit + générique)

```java
package ma.ensaf.ecommerce.common.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AuditedEntity<ID> extends BaseEntity<ID> {

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

**Points clés** :

- **`<ID>` (générique propagé)** : `extends BaseEntity<ID>` transmet le type d'ID à la sous-classe
- **`@Getter @Setter @ToString`** : Bonne pratique avec héritage (évite les conflits avec equals/hashCode de BaseEntity)
- **Pas de `@EqualsAndHashCode`** : Délègue la responsabilité à BaseEntity

**Étape 3 : Product** (hérite de AuditedEntity)

```java
package ma.ensaf.ecommerce.catalogue.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.ensaf.ecommerce.common.model.AuditedEntity;

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

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private Double price;

    private Integer stockQuantity;

    private String category;

    private boolean available = true;
}
```

**Points importants** :

- **`extends AuditedEntity<Long>`** : Spécifie que l'ID sera de type `Long`
- **`@Getter @Setter @ToString`** : Même pattern que AuditedEntity (bonne pratique avec héritage)
- **equals/hashCode hérités** : Utilise `@EqualsAndHashCode(of = "id")` de BaseEntity par défaut

**Hiérarchie finale** :

```
BaseEntity<ID> (id, isNew())
    ↓ extends
AuditedEntity<ID> (id, createdAt, updatedAt, isNew())
    ↓ extends
Product extends AuditedEntity<Long> (id: Long, createdAt, updatedAt, name, sku, price, ...)
```

**Avantages de cette approche** :

- ✅ **Flexibilité** : Certaines entités peuvent hériter de BaseEntity (pas besoin d'audit), d'autres de AuditedEntity
- ✅ **Réutilisable** : Order, User, Payment peuvent tous hériter de AuditedEntity
- ✅ **Maintenable** : Logique d'audit centralisée
- ✅ **Génériques** : Type d'ID flexible (`Long`, `UUID`, etc.)
- ✅ **Persistable** : Optimisation de la détection INSERT vs UPDATE
- ✅ `@SuperBuilder` : Pattern builder fonctionne avec toute la hiérarchie

---

### 🎓 Technique Avancée : equals/hashCode avec Business Key

**Par défaut** : `BaseEntity` utilise `@EqualsAndHashCode(of = "id")` - fonctionne pour la majorité des cas.

**Optimisation avancée** : Pour les entités avec un **identifiant métier unique** (SKU, email, orderNumber), vous pouvez **surcharger** equals/hashCode pour utiliser cet identifiant au lieu de l'ID technique.

```java
// ✅ Approche standard : hérite de BaseEntity (utilise id)
public class OrderItem extends AuditedEntity {
    // Utilise @EqualsAndHashCode(of = "id") hérité
}

// 🎓 Approche avancée : Product avec business key
@EqualsAndHashCode(callSuper = false, of = "sku")
public class Product extends AuditedEntity {
    @Column(nullable = false, unique = true)
    private String sku;  // Business key : stable et unique
}

// 🎓 Approche avancée : User avec business key
@EqualsAndHashCode(callSuper = false, of = "email")
public class User extends AuditedEntity {
    @Column(nullable = false, unique = true)
    private String email;  // Business key : stable et unique
}
```

**Pourquoi c'est mieux avec un business key ?**

- Le business key est défini **dès la création** (pas besoin d'attendre `save()`)
- Il ne change **jamais** (stable)
- Plus **naturel** pour le métier (SKU, email vs ID technique)

> 💡 **Note** : Cette optimisation est optionnelle. L'approche avec `id` fonctionne très bien dans la plupart des cas. Utilisez un business key seulement si vous avez un identifiant métier naturel et unique.

**Annotations JPA essentielles** :

| Annotation | Rôle |
|------------|------|
| `@Entity` | Marque la classe comme entité JPA |
| `@Table(name="...")` | Nom de la table (optionnel, par défaut = nom classe) |
| `@MappedSuperclass` | Classe de base pour entités (non persistée seule) |
| `@Id` | Clé primaire |
| `@GeneratedValue` | Génération automatique de l'ID |
| `@Column` | Configuration colonne (nom, contraintes) |
| `@PrePersist` | Exécuté avant insertion |
| `@PreUpdate` | Exécuté avant mise à jour |

### Repository JPA

Spring Data JPA fournit des repositories **sans écrire de code SQL** !

```java
package ma.ensaf.ecommerce.catalogue.repository;

import ma.ensaf.ecommerce.catalogue.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Méthodes fournies automatiquement par JpaRepository :
    // - save(Product) : INSERT ou UPDATE
    // - findById(Long) : SELECT par ID
    // - findAll() : SELECT *
    // - deleteById(Long) : DELETE par ID
    // - count() : COUNT(*)
    // - existsById(Long) : EXISTS

    // Méthodes personnalisées (Spring génère l'implémentation !)
    Optional<Product> findBySku(String sku);

    List<Product> findByCategory(String category);

    List<Product> findByAvailableTrue();

    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    boolean existsBySku(String sku);
}
```

**Comment ça fonctionne ?**

1. **Interface seulement** : Vous déclarez uniquement l'interface, Spring génère l'implémentation au démarrage
2. **`JpaRepository<Product, Long>`** :
   - `Product` = Type de l'entité
   - `Long` = Type de l'ID
3. **`@Repository` optionnel** : Spring détecte automatiquement les interfaces qui étendent `JpaRepository`

### Query Methods : La Magie de Spring Data JPA

**Spring Data génère automatiquement les requêtes SQL à partir du nom de la méthode !**

**Convention de nommage** :

```
[Action][Subject]By[Property][Condition]
```

**Exemples** :

| Nom de méthode | Requête SQL générée | Explication |
|----------------|---------------------|-------------|
| `findBySku(String sku)` | `SELECT * FROM products WHERE sku = ?` | Recherche par SKU exact |
| `findByName(String name)` | `SELECT * FROM products WHERE name = ?` | Recherche par nom exact |
| `findByPriceLessThan(Double price)` | `SELECT * FROM products WHERE price < ?` | Prix inférieur à |
| `findByPriceGreaterThan(Double price)` | `SELECT * FROM products WHERE price > ?` | Prix supérieur à |
| `findByPriceBetween(Double min, Double max)` | `SELECT * FROM products WHERE price BETWEEN ? AND ?` | Prix dans intervalle |
| `findByNameContaining(String keyword)` | `SELECT * FROM products WHERE name LIKE %?%` | Nom contient mot-clé |
| `findByNameContainingIgnoreCase(String keyword)` | `SELECT * FROM products WHERE UPPER(name) LIKE UPPER(%?%)` | Insensible à la casse |
| `findByAvailableTrue()` | `SELECT * FROM products WHERE available = true` | Produits disponibles |
| `findByAvailableFalse()` | `SELECT * FROM products WHERE available = false` | Produits indisponibles |
| `findByNameAndCategory(String name, String cat)` | `SELECT * FROM products WHERE name = ? AND category = ?` | Deux conditions (AND) |
| `findByNameOrCategory(String name, String cat)` | `SELECT * FROM products WHERE name = ? OR category = ?` | Deux conditions (OR) |
| `findByOrderByPriceAsc()` | `SELECT * FROM products ORDER BY price ASC` | Tri ascendant |
| `findByCategoryOrderByPriceDesc(String cat)` | `SELECT * FROM products WHERE category = ? ORDER BY price DESC` | Filtre + tri |
| `existsBySku(String sku)` | `SELECT COUNT(*) > 0 FROM products WHERE sku = ?` | Vérifier existence |
| `countByCategory(String category)` | `SELECT COUNT(*) FROM products WHERE category = ?` | Compter par catégorie |
| `deleteByCategory(String category)` | `DELETE FROM products WHERE category = ?` | Supprimer par catégorie |

**Mots-clés supportés** :

| Mot-clé | SQL équivalent | Exemple |
|---------|----------------|---------|
| `And` | `AND` | `findByNameAndCategory` |
| `Or` | `OR` | `findByNameOrSku` |
| `Is`, `Equals` | `=` | `findByName`, `findByNameEquals` |
| `Between` | `BETWEEN ... AND ...` | `findByPriceBetween` |
| `LessThan` | `<` | `findByPriceLessThan` |
| `LessThanEqual` | `<=` | `findByPriceLessThanEqual` |
| `GreaterThan` | `>` | `findByPriceGreaterThan` |
| `GreaterThanEqual` | `>=` | `findByPriceGreaterThanEqual` |
| `Before` | `<` (dates) | `findByCreatedAtBefore` |
| `After` | `>` (dates) | `findByCreatedAtAfter` |
| `IsNull` | `IS NULL` | `findByDescriptionIsNull` |
| `IsNotNull`, `NotNull` | `IS NOT NULL` | `findByDescriptionIsNotNull` |
| `Like` | `LIKE` | `findByNameLike` |
| `NotLike` | `NOT LIKE` | `findByNameNotLike` |
| `StartingWith` | `LIKE ?%` | `findByNameStartingWith` |
| `EndingWith` | `LIKE %?` | `findByNameEndingWith` |
| `Containing` | `LIKE %?%` | `findByNameContaining` |
| `OrderBy` | `ORDER BY` | `findByOrderByPriceAsc` |
| `Not` | `!=` | `findByNameNot` |
| `In` | `IN (...)` | `findByCategoryIn(List<String>)` |
| `NotIn` | `NOT IN (...)` | `findByCategoryNotIn(List<String>)` |
| `True` | `= true` | `findByAvailableTrue` |
| `False` | `= false` | `findByAvailableFalse` |
| `IgnoreCase` | `UPPER(...)` | `findByNameIgnoreCase` |

**Types de retour supportés** :

```java
// Une seule entité (ou null)
Product findBySku(String sku);

// Une seule entité (ou Optional.empty())
Optional<Product> findBySku(String sku);

// Liste d'entités
List<Product> findByCategory(String category);

// Booléen (existence)
boolean existsBySku(String sku);

// Nombre (comptage)
long countByCategory(String category);

// Suppression (retourne nombre de suppressions)
long deleteByCategory(String category);
```

**💡 Pourquoi cette approche est puissante ?**

1. **Pas de SQL manuel** : Moins d'erreurs, code plus lisible
2. **Type-safe** : Le compilateur vérifie les noms de propriétés
3. **Maintenance facile** : Si vous renommez un champ dans l'entité, l'IDE détectera les erreurs
4. **Génération au démarrage** : Spring analyse le nom de méthode et génère le code au lancement

### Configuration H2 et Application

**H2** = Base de données en mémoire, parfaite pour développer/tester.

**application.yml** :

```yaml
# Configuration du serveur
server:
  port: 8081                              # Port du service (8080 par défaut)
  servlet:
    context-path: /catalogue              # ⚠️ OPTIONNEL - voir ci-dessous

# Configuration de la base de données
spring:
  application:
    name: catalogue-service               # Nom du service

  datasource:
    url: jdbc:h2:mem:catalogue_db
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: true                       # Active la console web H2
      path: /h2-console

  jpa:
    hibernate:
      ddl-auto: create-drop               # Recrée le schéma au démarrage
    show-sql: true                        # Affiche les requêtes SQL
    properties:
      hibernate:
        format_sql: true                  # Formate les requêtes SQL
```

**Paramètres importants** :

| Paramètre | Description | Valeur |
|-----------|-------------|--------|
| `server.port` | Port d'écoute | `8081` (défaut: 8080) |
| `spring.application.name` | Nom du service | `catalogue-service` |
| `spring.jpa.hibernate.ddl-auto` | Gestion du schéma | `create-drop` (dev), `validate` (prod) |

**⚠️ Context-path : OPTIONNEL**

```yaml
server:
  servlet:
    context-path: /catalogue  # Préfixe optionnel
```

| Si présent (`/catalogue`) | Si absent (commenté ou supprimé) |
|---------------------------|----------------------------------|
| ✅ URLs : `http://localhost:8081/catalogue/api/v1/products` | ✅ URLs : `http://localhost:8081/api/v1/products` |
| ✅ Console H2 : `http://localhost:8081/catalogue/h2-console` | ✅ Console H2 : `http://localhost:8081/h2-console` |

> 💡 **Dans ce module** : Nous utilisons `/catalogue` pour distinguer les services. Vous pouvez le retirer si vous préférez des URLs plus courtes.

> 💡 **Microservices** : Comme nous développons plusieurs services en parallèle, chaque service **doit avoir un port différent** :
>
> - `catalogue-service` : port 8081
> - `order-service` : port 8082
> - `user-service` : port 8083

**Accès à la console H2** : <http://localhost:8081/h2-console>

- JDBC URL: `jdbc:h2:mem:catalogue_db`
- Username: `sa`
- Password: (vide)

### 💡 H2 vs PostgreSQL : Quand utiliser quoi ?

**Pour ce module (Module 2)** : Nous utilisons **H2**

| Avantage | Raison |
|----------|--------|
| ✅ **Zéro configuration** | Aucune installation requise |
| ✅ **Démarrage instantané** | Base créée automatiquement en mémoire |
| ✅ **Parfait pour apprendre** | Focus sur JPA, pas sur la base de données |
| ✅ **Tests rapides** | Idéal pour TDD (cycle RED-GREEN-REFACTOR) |

| Limitation | Impact |
|------------|--------|
| ❌ **Données perdues au redémarrage** | Tout est en RAM |
| ❌ **Pas pour production** | Seulement dev/test |

**Module 4** : Migration vers **PostgreSQL**

| Avantage | Usage |
|----------|-------|
| ✅ **Données persistantes** | Survit aux redémarrages |
| ✅ **Production-ready** | Base de données professionnelle |
| ✅ **Migrations avec Liquibase** | Gestion du schéma versionnée |

> **Principe** : Commencez simple (H2), passez à robuste (PostgreSQL) quand nécessaire.

---

## 3. Service Layer

La couche service contient la **logique métier**.

```java
package ma.ensaf.ecommerce.catalogue.service;

import ma.ensaf.ecommerce.catalogue.model.Product;
import ma.ensaf.ecommerce.catalogue.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id);
    }

    public Product create(Product product) {
        log.info("Creating new product: {}", product.getName());

        if (productRepository.existsBySku(product.getSku())) {
            throw new IllegalArgumentException("Product with SKU " + product.getSku() + " already exists");
        }

        return productRepository.save(product);
    }

    public Product update(Long id, Product productDetails) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setCategory(productDetails.getCategory());
        product.setAvailable(productDetails.isAvailable());

        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        log.info("Deleting product with id: {}", id);

        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        productRepository.deleteById(id);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> searchByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
}
```

**Annotations importantes** :

- `@Service` : Marque comme bean de service
- `@RequiredArgsConstructor` : Lombok génère constructeur avec `final` fields → **injection par constructeur**
- `@Transactional` : Toutes les méthodes sont transactionnelles
- `@Slf4j` : Logger automatique

---

## 4. REST Controller

Le controller expose des **endpoints REST**.

### Version Simple (Recommandée)

Spring gère automatiquement les codes HTTP et la sérialisation JSON :

```java
package ma.ensaf.ecommerce.catalogue.controller;

import ma.ensaf.ecommerce.catalogue.model.Product;
import ma.ensaf.ecommerce.catalogue.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchByName(keyword);
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.findByCategory(category);
    }
}
```

**Codes HTTP automatiques** :

- `@GetMapping` → 200 OK (ou 404 si exception)
- `@PostMapping` avec `@ResponseStatus(CREATED)` → 201 Created
- `@PutMapping` → 200 OK
- `@DeleteMapping` avec `@ResponseStatus(NO_CONTENT)` → 204 No Content

### Version avec ResponseEntity (Contrôle Avancé)

Utilisez `ResponseEntity` uniquement quand vous devez **contrôler finement** la réponse (headers, statut conditionnel, etc.) :

```java
@GetMapping("/{id}")
public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    return productService.findById(id)
        .map(ResponseEntity::ok)                    // 200 OK si trouvé
        .orElse(ResponseEntity.notFound().build()); // 404 Not Found sinon
}

@PostMapping
public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    Product created = productService.create(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

> 💡 **Recommandation** : Utilisez la version simple pour 90% des cas. `ResponseEntity` est utile pour :
>
> - Téléchargement de fichiers
> - Headers personnalisés
> - Cache control
> - Statuts HTTP conditionnels complexes

### Annotations REST

| Annotation | HTTP Method | Usage |
|------------|-------------|-------|
| `@GetMapping` | GET | Récupérer des données |
| `@PostMapping` | POST | Créer une ressource |
| `@PutMapping` | PUT | Mettre à jour entièrement |
| `@PatchMapping` | PATCH | Mise à jour partielle |
| `@DeleteMapping` | DELETE | Supprimer |

| Annotation | Rôle |
|------------|------|
| `@RestController` | Controller REST (= `@Controller` + `@ResponseBody`) |
| `@RequestMapping("/api/v1/products")` | Préfixe pour tous les endpoints (avec version) |
| `@PathVariable` | Récupère variable de l'URL (`/products/{id}`) |
| `@RequestParam` | Récupère paramètre query string (`?keyword=...`) |
| `@RequestBody` | Mappe le JSON du body vers un objet |
| `@ResponseStatus` | Définit le code HTTP de retour (201, 204, etc.) |

---

## 5. Tester avec Spring Boot

> **Note** : Les tests avancés (Service avec Mockito, Controller avec MockMvc) seront couverts en détail au **Module 7**.
> Ici, nous voyons uniquement les **tests de Repository** qui sont essentiels pour valider JPA.

### Test du Repository

```java
package ma.ensaf.ecommerce.catalogue.repository;

import ma.ensaf.ecommerce.catalogue.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    void shouldSaveAndFindProduct() {
        // Arrange
        Product product = Product.builder()
            .name("Laptop")
            .sku("LAP-001")
            .price(999.99)
            .stockQuantity(10)
            .category("Electronics")
            .build();

        // Act
        Product saved = repository.save(product);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Laptop");
    }

    @Test
    void shouldFindBySku() {
        // Arrange
        Product product = Product.builder()
            .name("Mouse")
            .sku("MOU-001")
            .price(29.99)
            .build();
        repository.save(product);

        // Act
        Optional<Product> found = repository.findBySku("MOU-001");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Mouse");
    }

    @Test
    void shouldFindByCategory() {
        // Arrange
        repository.save(Product.builder().name("Laptop").category("Electronics").build());
        repository.save(Product.builder().name("Mouse").category("Electronics").build());
        repository.save(Product.builder().name("Desk").category("Furniture").build());

        // Act
        List<Product> electronics = repository.findByCategory("Electronics");

        // Assert
        assertThat(electronics).hasSize(2);
        assertThat(electronics).allMatch(p -> "Electronics".equals(p.getCategory()));
    }

    @Test
    void shouldCheckIfSkuExists() {
        // Arrange
        repository.save(Product.builder().sku("LAP-001").name("Laptop").build());

        // Act & Assert
        assertThat(repository.existsBySku("LAP-001")).isTrue();
        assertThat(repository.existsBySku("UNKNOWN")).isFalse();
    }
}
```

**@DataJpaTest** :

- ✅ Configure une base H2 en mémoire automatiquement
- ✅ Scanne les `@Entity` et configure les repositories
- ✅ **Écrit réellement dans H2** (ce ne sont pas des mocks !)
- ✅ **Rollback automatique** après chaque test → pas de pollution entre tests
- ✅ Rapide : ne charge que la couche JPA (pas tout Spring Boot)

> 💡 Les tests de repository sont de **vrais tests d'intégration** : ils écrivent dans une vraie base de données H2 en mémoire, exécutent de vraies requêtes SQL générées par Hibernate.

> 💡 **Module 7** couvrira :
>
> - Tests du Service avec Mockito (`@Mock`, `@InjectMocks`)
> - Tests du Controller avec MockMvc (`@WebMvcTest`)
> - Tests d'intégration complets (`@SpringBootTest`)
> - TestContainers pour PostgreSQL

---

## 6. Lancer l'Application

### Depuis Maven

```bash
cd catalogue-service
mvn spring-boot:run
```

### Depuis l'IDE

Exécutez la classe `CatalogueServiceApplication` (Run/Debug).

### Depuis le JAR

```bash
mvn clean package
java -jar target/catalogue-service-1.0.0-SNAPSHOT.jar
```

**Application démarrée** : <http://localhost:8080>

---

## 7. Tester les Endpoints avec cURL

```bash
# Créer un produit
curl -X POST http://localhost:8081/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell XPS 13",
    "sku": "LAP-001",
    "price": 1299.99,
    "stockQuantity": 10,
    "category": "Electronics",
    "description": "High performance laptop"
  }'

# Récupérer tous les produits
curl http://localhost:8081/api/v1/products

# Récupérer un produit par ID
curl http://localhost:8081/api/v1/products/1

# Rechercher par nom
curl "http://localhost:8081/api/v1/products/search?keyword=laptop"

# Filtrer par catégorie
curl http://localhost:8081/api/v1/products/category/Electronics

# Mettre à jour un produit
curl -X PUT http://localhost:8081/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell XPS 13 Updated",
    "sku": "LAP-001",
    "price": 1199.99,
    "stockQuantity": 8,
    "category": "Electronics"
  }'

# Supprimer un produit
curl -X DELETE http://localhost:8081/api/v1/products/1
```

---

## 📚 Récapitulatif

### Architecture en Couches

```
┌─────────────────────────────────┐
│   REST Controller (@RestController)   │  ← Endpoints HTTP
├─────────────────────────────────┤
│   Service Layer (@Service)      │  ← Logique métier
├─────────────────────────────────┤
│   Repository (@Repository)      │  ← Accès données
├─────────────────────────────────┤
│   JPA Entities (@Entity)        │  ← Modèle de données
└─────────────────────────────────┘
```

### Annotations Clés

| Annotation | Couche | Rôle |
|------------|--------|------|
| `@Entity` | Model | Entité JPA |
| `@Repository` | Data | Repository Spring Data |
| `@Service` | Business | Service métier |
| `@RestController` | Web | Controller REST |
| `@Transactional` | Business | Gestion transactions |

### Dépendances Maven Essentielles

**Version utilisée** : Spring Boot 4.0.0 (Java 21 minimum)

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.0</version>
    <relativePath/>
</parent>

<properties>
    <java.version>21</java.version>
</properties>

<dependencies>
    <!-- Spring Boot Starter Web MVC : API REST -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webmvc</artifactId>
    </dependency>

    <!-- Spring Boot Starter Data JPA : Persistance -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- H2 Database : Base de données en mémoire -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- H2 Console : Interface web pour H2 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-h2console</artifactId>
    </dependency>

    <!-- Actuator : Monitoring et métriques -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- Lombok : Réduction du code boilerplate -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Tests JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Tests Web MVC -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webmvc-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 🎯 Points Clés à Retenir

1. **Spring Boot = Simplicité** : Convention over configuration
2. **JPA = ORM** : Mapper objets ↔ tables sans SQL
3. **Spring Data = Magie** : Repositories sans implémentation
4. **REST = Stateless** : Chaque requête est indépendante
5. **Tests = Essentiel** : @DataJpaTest, @WebMvcTest, Mockito

---

## ⚡ Commandes Utiles

```bash
# Lancer l'app
mvn spring-boot:run

# Lancer sur un port spécifique
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082

# Lancer les tests
mvn test

# Package JAR
mvn clean package

# Lancer le JAR
java -jar target/catalogue-service-1.0.0-SNAPSHOT.jar

# Lancer le JAR sur un port spécifique
java -jar target/catalogue-service-1.0.0-SNAPSHOT.jar --server.port=8082
```

---

**🔗 Pour approfondir** : Consultez `cours-complet.md`

**Prochaine étape** : Module 3 - Documentation Swagger et Gestion d'erreurs
