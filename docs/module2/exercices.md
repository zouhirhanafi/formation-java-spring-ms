# Module 2 : Exercices Pratiques - Service Catalogue (Approche TDD)

**Durée totale** : 6h
**Objectif** : Développer le service Catalogue avec Product et Category en suivant l'approche **Test-Driven Development (TDD)**

---

## 📋 Vue d'ensemble

### Approche pédagogique : Test-Driven Development (TDD)

Dans ces exercices, vous allez découvrir le **TDD** :

1. **🔴 RED** : Écrire un test qui échoue
2. **🟢 GREEN** : Écrire le code minimal pour faire passer le test
3. **🔵 REFACTOR** : Améliorer le code sans casser les tests

### Types de tests

| Type | Qui l'écrit ? | Quand ? | Technologie |
|------|---------------|---------|-------------|
| **Repository** | ✍️ **Vous** | Maintenant (TDD) | `@DataJpaTest` (pas de mock) |
| **Service** | 📦 **Fourni** | Validation (détails Module 7) | Mockito |
| **Controller** | 📦 **Fourni** | Validation (détails Module 7) | MockMvc |

### Organisation des exercices

1. **🎓 Partie Guidée** (Product - 4h) : Réalisée **ensemble**
2. **🚀 Partie Autonome** (Category - 2h) : À faire **seul** avec validation par les tests fournis

---

## 🎓 PARTIE 1 : Exercices Guidés (Product) - 4h

> **Note** : Cette partie sera réalisée **ensemble** pendant la formation en suivant le cycle TDD.

---

### Exercice 1.1 : Setup du Projet (30min)

#### Étape 1 : Créer le projet avec Spring Initializr

**Via Web** : <https://start.spring.io/>

**Configuration** :

- **Project** : Maven
- **Language** : Java
- **Spring Boot** : 4.x (dernière stable)
- **Group** : `ma.ensaf.ecommerce`
- **Artifact** : `catalogue-service`
- **Package name** : `ma.ensaf.ecommerce.catalogue`
- **Java** : 21

**Dependencies** :

- Spring Web
- Spring Data JPA
- H2 Database
- Lombok
- Validation
- Spring Boot DevTools

#### Étape 2 : Configuration `application.yml`

Créer `src/main/resources/application.yml` :

```yaml
server:
  port: 8081
  servlet:
    context-path: /catalogue

spring:
  application:
    name: catalogue-service

  datasource:
    url: jdbc:h2:mem:catalogue_db
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: true
      path: /h2-console

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    ma.ensaf.ecommerce: DEBUG
    org.hibernate.SQL: DEBUG
```

#### Étape 3 : Classe principale

`src/main/java/ma/ensaf/ecommerce/catalogue/CatalogueServiceApplication.java` :

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

#### ✅ Checkpoint Setup

```bash
mvn spring-boot:run
```

- Application démarre sur port 8081
- Console H2 accessible : <http://localhost:8081/catalogue/h2-console>

---

### Exercice 1.2 : TDD Repository - Tests d'abord ! (1h15)

> **🎯 Principe TDD** : Nous allons écrire les **tests AVANT** le code !

#### Étape 1 : Créer les classes de base (15min)

**BaseEntity** (`common/src/main/java/ma/ensaf/ecommerce/common/model/BaseEntity.java`) :

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

> 💡 **Nouveautés** : `<ID>` générique (vu au Module 1) + `Persistable` pour optimiser `save()`

**AuditedEntity** (`common/src/main/java/ma/ensaf/ecommerce/common/model/AuditedEntity.java`) :

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

> 💡 **Bonne pratique** : `@Getter @Setter @ToString` au lieu de `@Data` pour éviter les conflits avec equals/hashCode de BaseEntity

#### Étape 2 : Écrire les tests Repository (30min) - 🔴 RED

> **Consigne** : Écrivez TOUS les tests ci-dessous. Ils vont ÉCHOUER (c'est normal !).

`src/test/java/ma/ensaf/ecommerce/catalogue/repository/ProductRepositoryTest.java` :

```java
package ma.ensaf.ecommerce.catalogue.repository;

import ma.ensaf.ecommerce.catalogue.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveProduct() {
        // Given
        Product product = Product.builder()
            .name("Laptop")
            .sku("LAP-001")
            .price(999.99)
            .stockQuantity(10)
            .category("Electronics")
            .build();

        // When
        Product saved = repository.save(product);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Laptop");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindBySku() {
        // Given
        Product product = Product.builder()
            .name("Mouse")
            .sku("MOU-001")
            .price(29.99)
            .category("Electronics")
            .build();
        repository.save(product);

        // When
        Optional<Product> found = repository.findBySku("MOU-001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Mouse");
    }

    @Test
    void shouldFindByCategory() {
        // Given
        repository.save(Product.builder().name("Laptop").sku("LAP-1").category("Electronics").price(1000.0).build());
        repository.save(Product.builder().name("Mouse").sku("MOU-1").category("Electronics").price(30.0).build());
        repository.save(Product.builder().name("Desk").sku("DSK-1").category("Furniture").price(300.0).build());

        // When
        List<Product> electronics = repository.findByCategory("Electronics");

        // Then
        assertThat(electronics).hasSize(2);
        assertThat(electronics).allMatch(p -> "Electronics".equals(p.getCategory()));
    }

    @Test
    void shouldCheckIfSkuExists() {
        // Given
        repository.save(Product.builder().sku("LAP-001").name("Laptop").price(1000.0).build());

        // When & Then
        assertThat(repository.existsBySku("LAP-001")).isTrue();
        assertThat(repository.existsBySku("UNKNOWN")).isFalse();
    }

    @Test
    void shouldRespectUniqueConstraintOnSku() {
        // Given
        repository.save(Product.builder().sku("DUP-001").name("Product 1").price(100.0).build());

        // When & Then
        assertThatThrownBy(() -> {
            repository.save(Product.builder().sku("DUP-001").name("Product 2").price(200.0).build());
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldFindAvailableProducts() {
        // Given
        repository.save(Product.builder().name("Available").sku("AV-1").price(100.0).available(true).build());
        repository.save(Product.builder().name("Unavailable").sku("UN-1").price(100.0).available(false).build());

        // When
        List<Product> available = repository.findByAvailableTrue();

        // Then
        assertThat(available).hasSize(1);
        assertThat(available.get(0).getName()).isEqualTo("Available");
    }

    @Test
    void shouldSearchByNameIgnoreCase() {
        // Given
        repository.save(Product.builder().name("Laptop Dell").sku("LAP-1").price(1000.0).build());
        repository.save(Product.builder().name("Laptop HP").sku("LAP-2").price(900.0).build());
        repository.save(Product.builder().name("Mouse").sku("MOU-1").price(30.0).build());

        // When
        List<Product> laptops = repository.findByNameContainingIgnoreCase("laptop");

        // Then
        assertThat(laptops).hasSize(2);
    }
}
```

**Lancer les tests** :

```bash
mvn test -Dtest=ProductRepositoryTest
```

**Résultat attendu** : ❌ **Tous les tests échouent** (Product, ProductRepository n'existent pas encore)

#### Étape 3 : Créer l'entité Product (20min) - Toujours 🔴 RED

`src/main/java/ma/ensaf/ecommerce/catalogue/model/Product.java` :

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

    // TODO : Ajouter les attributs selon les tests
    // Indice : name, description, sku, price, stockQuantity, category, imageUrl, available

    // TODO : Ajouter les annotations JPA (@Column, nullable, unique, length, etc.)
}
```

**Lancer les tests** :

```bash
mvn test -Dtest=ProductRepositoryTest
```

**Résultat** : ❌ **Tests échouent toujours** (ProductRepository n'existe pas)

#### Étape 4 : Créer le Repository (10min) - 🟢 GREEN

`src/main/java/ma/ensaf/ecommerce/catalogue/repository/ProductRepository.java` :

```java
package ma.ensaf.ecommerce.catalogue.repository;

import ma.ensaf.ecommerce.catalogue.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // TODO : Ajouter les méthodes nécessaires pour faire passer les tests
    // Indice : findBySku, findByCategory, findByAvailableTrue, existsBySku, findByNameContainingIgnoreCase
}
```

**Lancer les tests** :

```bash
mvn test -Dtest=ProductRepositoryTest
```

**Objectif** : ✅ **Tous les tests doivent passer au VERT !**

---

### Exercice 1.3 : Service Layer avec Validation (1h)

#### Étape 1 : Tests Service FOURNIS (15min) - Pour validation uniquement

> **⚠️ IMPORTANT : Fichiers à télécharger**
>
> Ces fichiers de tests sont **FOURNIS** dans `docs/module2/resources/tests-fournis/` :
> - `ProductServiceTest.java`
> - `ProductControllerTest.java` (voir Exercice 1.4)
> - `CategoryServiceTest.java` (voir Partie 2)
> - `CategoryControllerTest.java` (voir Partie 2)
>
> **Copiez-les** dans votre projet sans les modifier !

> **💡 Pourquoi ces tests utilisent Mockito ?**
>
> Ces tests utilisent **Mockito** et **MockMvc**, concepts du **Module 7**.
>
> **Pour l'instant** : NE PAS essayer de comprendre le code !
> - Copiez les fichiers fournis
> - Lancez `mvn test`
> - Si tests ✅ → Votre code est correct
> - Si tests ❌ → Corrigez votre Service/Controller
>
> **Module 7** : Vous apprendrez à écrire ces tests vous-même avec Mockito.

`docs/module2/resources/tests-fournis/ProductServiceTest.java` :

```java
package ma.ensaf.ecommerce.catalogue.service;

import ma.ensaf.ecommerce.catalogue.model.Product;
import ma.ensaf.ecommerce.catalogue.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ⚠️ TESTS FOURNIS POUR VALIDATION (Module 7)
 *
 * Ces tests utilisent Mockito qui sera détaillé au Module 7.
 * Utilisez-les pour VALIDER votre ProductService.
 *
 * Si tous les tests passent ✅ → Votre Service est correct !
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldFindAllProducts() {
        // Given
        List<Product> products = Arrays.asList(
            Product.builder().id(1L).name("Product 1").build(),
            Product.builder().id(2L).name("Product 2").build()
        );
        doReturn(products).when(productRepository).findAll();

        // When
        List<Product> result = productService.findAll();

        // Then
        assertThat(result).hasSize(2);
        verify(productRepository).findAll();
    }

    @Test
    void shouldFindProductById() {
        // Given
        Product product = Product.builder().id(1L).name("Laptop").build();
        doReturn(Optional.of(product)).when(productRepository).findById(1L);

        // When
        Optional<Product> result = productService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Laptop");
    }

    @Test
    void shouldCreateProduct() {
        // Given
        Product product = Product.builder()
            .name("Laptop")
            .sku("LAP-001")
            .price(999.99)
            .build();
        doReturn(false).when(productRepository).existsBySku("LAP-001");
        doReturn(product).when(productRepository).save(product);

        // When
        Product result = productService.create(product);

        // Then
        assertThat(result).isNotNull();
        verify(productRepository).save(product);
    }

    @Test
    void shouldThrowExceptionWhenSkuAlreadyExists() {
        // Given
        Product product = Product.builder().sku("LAP-001").price(999.99).build();
        doReturn(true).when(productRepository).existsBySku("LAP-001");

        // When & Then
        assertThatThrownBy(() -> productService.create(product))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("SKU")
            .hasMessageContaining("already exists");
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        // Given
        Product product = Product.builder().sku("LAP-001").price(-100.0).build();

        // When & Then
        assertThatThrownBy(() -> productService.create(product))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Price");
    }

    @Test
    void shouldUpdateProduct() {
        // Given
        Product existing = Product.builder().id(1L).name("Old Name").sku("LAP-001").price(999.99).build();
        Product updates = Product.builder().name("New Name").price(1099.99).build();
        doReturn(Optional.of(existing)).when(productRepository).findById(1L);
        doReturn(existing).when(productRepository).save(existing);

        // When
        Product result = productService.update(1L, updates);

        // Then
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getPrice()).isEqualTo(1099.99);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundForUpdate() {
        // Given
        doReturn(Optional.empty()).when(productRepository).findById(999L);

        // When & Then
        assertThatThrownBy(() -> productService.update(999L, new Product()))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("not found");
    }

    @Test
    void shouldDeleteProduct() {
        // Given
        doReturn(true).when(productRepository).existsById(1L);

        // When
        productService.deleteById(1L);

        // Then
        verify(productRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundForDelete() {
        // Given
        doReturn(false).when(productRepository).existsById(999L);

        // When & Then
        assertThatThrownBy(() -> productService.deleteById(999L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("not found");
    }
}
```

#### Étape 2 : Créer ProductService (45min)

`src/main/java/ma/ensaf/ecommerce/catalogue/service/ProductService.java` :

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

    // TODO : Implémenter les méthodes suivantes pour faire passer les tests

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        // TODO : Retourner tous les produits
        throw new UnsupportedOperationException("À implémenter");
    }

    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        // TODO : Retourner le produit par ID
        throw new UnsupportedOperationException("À implémenter");
    }

    public Product create(Product product) {
        // TODO : Implémenter la création avec validations :
        // 1. Vérifier que le SKU n'existe pas déjà (lever IllegalArgumentException)
        // 2. Vérifier que le prix est positif (lever IllegalArgumentException)
        // 3. Sauvegarder le produit
        throw new UnsupportedOperationException("À implémenter");
    }

    public Product update(Long id, Product productDetails) {
        // TODO : Implémenter la mise à jour :
        // 1. Vérifier que le produit existe (lever RuntimeException si non trouvé)
        // 2. Mettre à jour tous les champs
        // 3. Sauvegarder
        throw new UnsupportedOperationException("À implémenter");
    }

    public void deleteById(Long id) {
        // TODO : Implémenter la suppression :
        // 1. Vérifier que le produit existe (lever RuntimeException si non trouvé)
        // 2. Supprimer
        throw new UnsupportedOperationException("À implémenter");
    }

    // Bonus : Ajouter d'autres méthodes utiles (findByCategory, searchByName, etc.)
}
```

**Validation** :

```bash
mvn test -Dtest=ProductServiceTest
```

**Objectif** : ✅ **Tous les tests doivent passer !**

---

### Exercice 1.4 : REST Controller (1h)

#### Étape 1 : Tests Controller FOURNIS (15min)

> **⚠️ Fichier fourni** : `docs/module2/resources/tests-fournis/ProductControllerTest.java`
>
> Copiez-le dans `src/test/java/.../controller/` et utilisez-le pour valider vos endpoints.

`docs/module2/resources/tests-fournis/ProductControllerTest.java` :

```java
package ma.ensaf.ecommerce.catalogue.controller;

import ma.ensaf.ecommerce.catalogue.model.Product;
import ma.ensaf.ecommerce.catalogue.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ⚠️ TESTS FOURNIS POUR VALIDATION (Module 7)
 *
 * Ces tests utilisent MockMvc qui sera détaillé au Module 7.
 * Utilisez-les pour VALIDER vos endpoints REST.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void shouldGetAllProducts() throws Exception {
        // Given
        doReturn(Arrays.asList(
            Product.builder().id(1L).name("Product 1").sku("P1").price(100.0).build(),
            Product.builder().id(2L).name("Product 2").sku("P2").price(200.0).build()
        )).when(productService).findAll();

        // When & Then
        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Product 1"));
    }

    @Test
    void shouldGetProductById() throws Exception {
        // Given
        Product product = Product.builder().id(1L).name("Laptop").sku("LAP-001").price(999.99).build();
        doReturn(Optional.of(product)).when(productService).findById(1L);

        // When & Then
        mockMvc.perform(get("/api/v1/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Laptop"))
            .andExpect(jsonPath("$.sku").value("LAP-001"));
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        // Given
        doReturn(Optional.empty()).when(productService).findById(999L);

        // When & Then
        mockMvc.perform(get("/api/v1/products/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateProduct() throws Exception {
        // Given
        Product product = Product.builder().id(1L).name("Laptop").sku("LAP-001").price(999.99).build();
        doReturn(product).when(productService).create(any(Product.class));

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Laptop\",\"sku\":\"LAP-001\",\"price\":999.99}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        // Given
        Product updated = Product.builder().id(1L).name("Updated").sku("LAP-001").price(1099.99).build();
        doReturn(updated).when(productService).update(eq(1L), any(Product.class));

        // When & Then
        mockMvc.perform(put("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\",\"sku\":\"LAP-001\",\"price\":1099.99}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        // Given
        doNothing().when(productService).deleteById(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/products/1"))
            .andExpect(status().isNoContent());
    }
}
```

#### Étape 2 : Créer ProductController (45min)

`src/main/java/ma/ensaf/ecommerce/catalogue/controller/ProductController.java` :

```java
package ma.ensaf.ecommerce.catalogue.controller;

import ma.ensaf.ecommerce.catalogue.model.Product;
import ma.ensaf.ecommerce.catalogue.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    // TODO : Implémenter les endpoints suivants pour faire passer les tests

    @GetMapping
    public List<Product> getAllProducts() {
        // TODO : Retourner tous les produits
        throw new UnsupportedOperationException("À implémenter");
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        // TODO : Retourner le produit par ID
        // Si non trouvé, lever ResponseStatusException avec HttpStatus.NOT_FOUND
        throw new UnsupportedOperationException("À implémenter");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product) {
        // TODO : Créer un produit (status 201 Created)
        throw new UnsupportedOperationException("À implémenter");
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        // TODO : Mettre à jour un produit
        throw new UnsupportedOperationException("À implémenter");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        // TODO : Supprimer un produit (status 204 No Content)
        throw new UnsupportedOperationException("À implémenter");
    }

    // Bonus : Ajouter d'autres endpoints (search, category, available)
}
```

**Validation** :

```bash
mvn test -Dtest=ProductControllerTest
```

**Objectif** : ✅ **Tous les tests doivent passer !**

#### Étape 3 : Tester avec cURL (15min)

```bash
# Démarrer l'application
mvn spring-boot:run

# Dans un autre terminal :

# 1. Récupérer tous les produits
curl http://localhost:8081/catalogue/api/v1/products | jq

# 2. Créer un produit
curl -X POST http://localhost:8081/catalogue/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro",
    "sku": "MAC-001",
    "price": 2499.99,
    "stockQuantity": 5,
    "category": "Electronics"
  }' | jq

# 3. Récupérer par ID
curl http://localhost:8081/catalogue/api/v1/products/1 | jq

# 4. Mettre à jour
curl -X PUT http://localhost:8081/catalogue/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro Updated",
    "sku": "MAC-001",
    "price": 2399.99,
    "stockQuantity": 10,
    "category": "Electronics"
  }' | jq

# 5. Supprimer
curl -X DELETE http://localhost:8081/catalogue/api/v1/products/1 -v
```

---

## ✅ CHECKPOINT Partie 1 - Product Complet

Avant de passer à la Partie 2, vérifiez :

- ✅ **Tests Repository** : 7 tests au vert
- ✅ **Tests Service** : 9 tests au vert
- ✅ **Tests Controller** : 6 tests au vert
- ✅ **Tests manuels cURL** : Tous les endpoints fonctionnent

**Total** : 22 tests passent ✅

---

## 🚀 PARTIE 2 : Exercices Autonomes (Category) - 2h

> **Note** : Cette partie est à réaliser **SEUL**, en suivant le même pattern que Product.

### 🎯 Objectif

Implémenter **Category** de A à Z en TDD :

1. Écrire les tests Repository
2. Créer l'entité et le repository
3. Créer le service (validé par tests fournis)
4. Créer le controller (validé par tests fournis)

---

### Exercice 2.1 : TDD Repository Category (45min)

#### Spécifications de l'entité Category

**Attributs** :

- `id` (Long) - hérité
- `name` (String) - obligatoire
- `code` (String) - obligatoire, unique (ex: "ELECTRONICS")
- `description` (String) - optionnel
- `displayOrder` (Integer) - défaut: 0
- `archived` (Boolean) - défaut: false
- `parentCategoryId` (Long) - optionnel
- `createdAt`, `updatedAt` - hérités

**Contraintes** :

- Table : `categories`
- Business key : `code`
- `@EqualsAndHashCode(of = "code")`

#### Étape 1 : Écrire les tests d'abord (30min) - 🔴 RED

`src/test/java/ma/ensaf/ecommerce/catalogue/repository/CategoryRepositoryTest.java` :

```java
package ma.ensaf.ecommerce.catalogue.repository;

import ma.ensaf.ecommerce.catalogue.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveCategory() {
        // Given
        Category category = Category.builder()
            .name("Electronics")
            .code("ELECTRONICS")
            .description("Electronic products")
            .displayOrder(1)
            .build();

        // When
        Category saved = repository.save(category);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Electronics");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindByCode() {
        // Given
        Category category = Category.builder()
            .name("Furniture")
            .code("FURNITURE")
            .build();
        repository.save(category);

        // When
        Optional<Category> found = repository.findByCode("FURNITURE");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Furniture");
    }

    @Test
    void shouldRespectUniqueConstraintOnCode() {
        // Given
        repository.save(Category.builder().code("DUP").name("Category 1").build());

        // When & Then
        assertThatThrownBy(() -> {
            repository.save(Category.builder().code("DUP").name("Category 2").build());
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldFindNonArchivedCategories() {
        // Given
        repository.save(Category.builder().name("Active").code("ACT").archived(false).build());
        repository.save(Category.builder().name("Archived").code("ARC").archived(true).build());

        // When
        List<Category> active = repository.findByArchivedFalse();

        // Then
        assertThat(active).hasSize(1);
        assertThat(active.get(0).getName()).isEqualTo("Active");
    }

    @Test
    void shouldFindRootCategories() {
        // Given
        repository.save(Category.builder().name("Root").code("ROOT").parentCategoryId(null).build());
        repository.save(Category.builder().name("Child").code("CHILD").parentCategoryId(1L).build());

        // When
        List<Category> roots = repository.findByParentCategoryIdIsNull();

        // Then
        assertThat(roots).hasSize(1);
        assertThat(roots.get(0).getName()).isEqualTo("Root");
    }

    // TODO : Ajouter au moins 2 autres tests (displayOrder, searchByName, etc.)
}
```

**Lancer** :

```bash
mvn test -Dtest=CategoryRepositoryTest
```

**Résultat attendu** : ❌ Tests échouent (Category n'existe pas)

#### Étape 2 : Créer Category et CategoryRepository (15min) - 🟢 GREEN

`src/main/java/ma/ensaf/ecommerce/catalogue/model/Category.java` :

```java
package ma.ensaf.ecommerce.catalogue.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.ensaf.ecommerce.common.model.AuditedEntity;

@Entity
@Table(name = "categories")
@Data
@EqualsAndHashCode(callSuper = false, of = "code")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Category extends AuditedEntity {

    // TODO : Ajouter les attributs selon les spécifications

    // TODO : Ajouter méthodes métier isRoot(), hasParent()
}
```

`src/main/java/ma/ensaf/ecommerce/catalogue/repository/CategoryRepository.java` :

```java
package ma.ensaf.ecommerce.catalogue.repository;

import ma.ensaf.ecommerce.catalogue.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // TODO : Ajouter les méthodes pour faire passer les tests
}
```

**Validation** :

```bash
mvn test -Dtest=CategoryRepositoryTest
```

**Objectif** : ✅ **Tous les tests au vert !**

---

### Exercice 2.2 : Service Category (35min)

#### Tests Service FOURNIS

`src/test/java/ma/ensaf/ecommerce/catalogue/service/CategoryServiceTest.java` :

```java
package ma.ensaf.ecommerce.catalogue.service;

import ma.ensaf.ecommerce.catalogue.model.Category;
import ma.ensaf.ecommerce.catalogue.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ⚠️ TESTS FOURNIS POUR VALIDATION
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldFindAllCategories() {
        doReturn(Arrays.asList(
            Category.builder().id(1L).name("Cat1").build(),
            Category.builder().id(2L).name("Cat2").build()
        )).when(categoryRepository).findAll();

        List<Category> result = categoryService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldCreateCategory() {
        Category category = Category.builder().name("Electronics").code("ELEC").build();
        doReturn(false).when(categoryRepository).existsByCode("ELEC");
        doReturn(category).when(categoryRepository).save(category);

        Category result = categoryService.create(category);

        assertThat(result).isNotNull();
        verify(categoryRepository).save(category);
    }

    @Test
    void shouldThrowExceptionWhenCodeAlreadyExists() {
        Category category = Category.builder().code("ELEC").build();
        doReturn(true).when(categoryRepository).existsByCode("ELEC");

        assertThatThrownBy(() -> categoryService.create(category))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("code");
    }

    @Test
    void shouldUpdateCategory() {
        Category existing = Category.builder().id(1L).name("Old").code("OLD").build();
        Category updates = Category.builder().name("New").build();
        doReturn(Optional.of(existing)).when(categoryRepository).findById(1L);
        doReturn(existing).when(categoryRepository).save(existing);

        Category result = categoryService.update(1L, updates);

        assertThat(result.getName()).isEqualTo("New");
    }

    @Test
    void shouldDeleteCategory() {
        doReturn(true).when(categoryRepository).existsById(1L);

        categoryService.deleteById(1L);

        verify(categoryRepository).deleteById(1L);
    }
}
```

#### Implémenter CategoryService

`src/main/java/ma/ensaf/ecommerce/catalogue/service/CategoryService.java` :

```java
package ma.ensaf.ecommerce.catalogue.service;

import ma.ensaf.ecommerce.catalogue.model.Category;
import ma.ensaf.ecommerce.catalogue.repository.CategoryRepository;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // TODO : Implémenter toutes les méthodes pour faire passer les tests

    // Validations requises :
    // - Code unique
    // - Si displayOrder null → 0 par défaut
}
```

**Validation** :

```bash
mvn test -Dtest=CategoryServiceTest
```

---

### Exercice 2.3 : Controller Category (40min)

#### Tests Controller FOURNIS

`src/test/java/ma/ensaf/ecommerce/catalogue/controller/CategoryControllerTest.java` :

```java
package ma.ensaf.ecommerce.catalogue.controller;

import ma.ensaf.ecommerce.catalogue.model.Category;
import ma.ensaf.ecommerce.catalogue.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ⚠️ TESTS FOURNIS POUR VALIDATION
 */
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void shouldGetAllCategories() throws Exception {
        doReturn(Arrays.asList(
            Category.builder().id(1L).name("Cat1").code("C1").build()
        )).when(categoryService).findAll();

        mockMvc.perform(get("/api/v1/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldCreateCategory() throws Exception {
        Category category = Category.builder().id(1L).name("Electronics").code("ELEC").build();
        doReturn(category).when(categoryService).create(any(Category.class));

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Electronics\",\"code\":\"ELEC\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Electronics"));
    }

    // TODO : D'autres tests sont fournis dans le fichier complet...
}
```

#### Implémenter CategoryController

`src/main/java/ma/ensaf/ecommerce/catalogue/controller/CategoryController.java` :

```java
package ma.ensaf.ecommerce.catalogue.controller;

import ma.ensaf.ecommerce.catalogue.model.Category;
import ma.ensaf.ecommerce.catalogue.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    // TODO : Implémenter tous les endpoints (GET, POST, PUT, DELETE, etc.)
}
```

**Validation** :

```bash
mvn test -Dtest=CategoryControllerTest
```

---

## ✅ VALIDATION FINALE

### Tests complets

Lancer **TOUS** les tests :

```bash
mvn test
```

**Résultat attendu** :

```
Tests run: 44, Failures: 0, Errors: 0, Skipped: 0
```

### Décomposition des tests

| Composant | Tests Repository | Tests Service | Tests Controller | Total |
|-----------|------------------|---------------|------------------|-------|
| **Product** | 7 | 9 | 6 | 22 |
| **Category** | 7 | 5 | 6 | 18 |
| **Common** | - | - | 4 | 4 |
| **TOTAL** | 14 | 14 | 16 | **44** ✅ |

---

## 📚 Récapitulatif TDD

### Ce que vous avez appris

#### Cycle TDD

1. **🔴 RED** : Écrire un test qui échoue
2. **🟢 GREEN** : Écrire le code minimal
3. **🔵 REFACTOR** : Améliorer sans casser

#### Types de tests

- **@DataJpaTest** : Tests d'intégration repository (vraie base H2)
- **@ExtendWith(MockitoExtension.class)** : Tests unitaires service (mocks)
- **@WebMvcTest** : Tests controller (MockMvc)

#### Avantages TDD

- ✅ **Confiance** : Tests ✅ = code correct
- ✅ **Documentation** : Tests = spécifications vivantes
- ✅ **Refactoring safe** : Tests garantissent non-régression
- ✅ **Design** : TDD force un bon design

---

## 🎯 Prochaine Étape : Module 3

Au Module 3, vous allez améliorer ce service avec :

- 📖 **Swagger/OpenAPI** : Documentation interactive
- 🛡️ **Exception Handling Global** : `@RestControllerAdvice`
- 🔄 **DTOs avec MapStruct** : Séparer entités/API
- ✅ **Bean Validation** : `@Valid`, `@NotBlank`, etc.

---

**Bravo ! Vous avez terminé le Module 2 en TDD ! 🎉**
