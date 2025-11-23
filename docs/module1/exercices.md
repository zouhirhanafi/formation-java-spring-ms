# Module 1 : Exercices Pratiques - Fondamentaux Java Moderne

**Durée** : 4h  
**Objectif** : Initialiser le projet Maven et implémenter des utilitaires utilisant les concepts Java modernes

---

## 🎯 Objectifs de la partie pratique

À la fin de ces exercices, vous aurez :

- ✅ Créé la structure du projet Maven multi-modules
- ✅ Configuré Lombok et les dépendances de test
- ✅ Implémenté une classe `ProductFilter` avec Streams API
- ✅ Écrit des tests unitaires avec JUnit 6 et AssertJ
- ✅ Manipulé les génériques, lambda et Optional

---

## 📦 Exercice 1 : Setup du Projet Maven (45 min)

### 1.1 Créer la structure du projet

**Objectif** : Initialiser un projet Maven multi-modules pour la plateforme e-commerce.

#### Structure cible

```
formation-java-spring-ms/
├── pom.xml                          # POM parent
├── catalogue-service/
│   ├── pom.xml
│   └── src/
│       ├── main/java/
│       └── test/java/
├── order-service/
│   ├── pom.xml
│   └── src/
│       ├── main/java/
│       └── test/java/
├── user-service/
│   ├── pom.xml
│   └── src/
│       ├── main/java/
│       └── test/java/
└── common/
    ├── pom.xml
    └── src/
        ├── main/java/
        └── test/java/
```

#### Étapes

1. **Créer le POM parent** (`pom.xml` à la racine)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>ma.ensaf.ecommerce</groupId>
    <artifactId>ecommerce-platform</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <name>E-Commerce Platform</name>
    <description>Plateforme e-commerce en microservices</description>

    <modules>
        <module>common</module>
        <module>catalogue-service</module>
        <module>order-service</module>
        <module>user-service</module>
    </modules>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        
        <!-- Versions des dépendances -->
        <lombok.version>1.18.34</lombok.version>
        <junit.version>6.0.1</junit.version>
        <assertj.version>3.27.6</assertj.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Lombok -->
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
                <scope>provided</scope>
            </dependency>

            <!-- JUnit 6 -->
            <dependency>
                <groupId>org.junit.jupiter</groupId>
                <artifactId>junit-jupiter</artifactId>
                <version>${junit.version}</version>
                <scope>test</scope>
            </dependency>

            <!-- AssertJ -->
            <dependency>
                <groupId>org.assertj</groupId>
                <artifactId>assertj-core</artifactId>
                <version>${assertj.version}</version>
                <scope>test</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.13.0</version>
                    <configuration>
                        <source>${java.version}</source>
                        <target>${java.version}</target>
                        <annotationProcessorPaths>
                            <path>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                                <version>${lombok.version}</version>
                            </path>
                        </annotationProcessorPaths>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <version>3.5.2</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

2. **Créer le module `common`** (`common/pom.xml`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>ma.ensaf.ecommerce</groupId>
        <artifactId>ecommerce-platform</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>common</artifactId>
    <name>Common - Utilities and Shared Classes</name>

    <dependencies>
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <!-- JUnit 6 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
        </dependency>

        <!-- AssertJ -->
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
        </dependency>
    </dependencies>
</project>
```

3. **Créer les autres modules** (même structure pour `catalogue-service`, `order-service`, `user-service`)

**Template pour les services** :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>ma.ensaf.ecommerce</groupId>
        <artifactId>ecommerce-platform</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>catalogue-service</artifactId>
    <name>Catalogue Service</name>

    <dependencies>
        <!-- Common module -->
        <dependency>
            <groupId>ma.ensaf.ecommerce</groupId>
            <artifactId>common</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <!-- JUnit 6 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
        </dependency>

        <!-- AssertJ -->
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
        </dependency>
    </dependencies>
</project>
```

4. **Créer les packages de base**

Dans le module `common`, créer l'arborescence :

```
common/src/main/java/ma/ensaf/ecommerce/
├── common/
│   ├── model/
│   ├── util/
│   └── exception/
```

5. **Vérifier la compilation**

```bash
# À la racine du projet
mvn clean compile

# Vérifier la structure
mvn dependency:tree
```

#### ✅ Critères de validation

- [ ] Le projet compile sans erreur
- [ ] Les 4 modules sont créés (common, catalogue, order, user)
- [ ] Lombok est correctement configuré
- [ ] Les dépendances de test sont présentes

---

## 🏗️ Exercice 2 : Modèle Product avec Lombok (30 min)

### 2.1 Créer la classe Product

**Objectif** : Créer une classe `Product` en utilisant Lombok pour réduire le boilerplate.

**Fichier** : `common/src/main/java/ma/ensaf/ecommerce/common/model/Product.java`

```java
package ma.ensaf.ecommerce.common.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité représentant un produit dans le catalogue
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    private Long id;
    
    private String name;
    
    private String description;
    
    private Double price;
    
    private String category;
    
    private Integer stock;
    
    private boolean available;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    /**
     * Vérifie si le produit est en rupture de stock
     */
    public boolean isOutOfStock() {
        return stock == null || stock <= 0;
    }
    
    /**
     * Vérifie si le produit est disponible à la vente
     */
    public boolean isAvailableForSale() {
        return available && !isOutOfStock();
    }
    
    /**
     * Réduit le stock d'une certaine quantité
     */
    public void reduceStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (stock < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        this.stock -= quantity;
    }
    
    /**
     * Augmente le stock d'une certaine quantité
     */
    public void increaseStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stock = (this.stock == null ? 0 : this.stock) + quantity;
    }
}
```

### 2.2 Créer un test pour la classe Product

**Fichier** : `common/src/test/java/ma/ensaf/ecommerce/common/model/ProductTest.java`

```java
package ma.ensaf.ecommerce.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class ProductTest {
    
    @Test
    @DisplayName("Builder devrait créer un produit avec tous les champs")
    void testProductBuilder() {
        // Arrange & Act
        Product product = Product.builder()
            .id(1L)
            .name("Laptop Dell XPS 13")
            .description("Laptop haute performance")
            .price(1299.99)
            .category("Electronics")
            .stock(10)
            .available(true)
            .createdAt(LocalDateTime.now())
            .build();
        
        // Assert
        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Laptop Dell XPS 13");
        assertThat(product.getPrice()).isEqualTo(1299.99);
        assertThat(product.getStock()).isEqualTo(10);
    }
    
    @Test
    @DisplayName("isOutOfStock devrait retourner true si stock est 0")
    void testIsOutOfStock() {
        Product product = Product.builder().stock(0).build();
        assertThat(product.isOutOfStock()).isTrue();
    }
    
    @Test
    @DisplayName("isOutOfStock devrait retourner false si stock > 0")
    void testIsNotOutOfStock() {
        Product product = Product.builder().stock(5).build();
        assertThat(product.isOutOfStock()).isFalse();
    }
    
    @Test
    @DisplayName("isAvailableForSale devrait retourner true si disponible et en stock")
    void testIsAvailableForSale() {
        Product product = Product.builder()
            .available(true)
            .stock(10)
            .build();
        
        assertThat(product.isAvailableForSale()).isTrue();
    }
    
    @Test
    @DisplayName("isAvailableForSale devrait retourner false si pas disponible")
    void testIsNotAvailableForSale_WhenNotAvailable() {
        Product product = Product.builder()
            .available(false)
            .stock(10)
            .build();
        
        assertThat(product.isAvailableForSale()).isFalse();
    }
    
    @Test
    @DisplayName("reduceStock devrait diminuer le stock correctement")
    void testReduceStock() {
        // Arrange
        Product product = Product.builder().stock(10).build();
        
        // Act
        product.reduceStock(3);
        
        // Assert
        assertThat(product.getStock()).isEqualTo(7);
    }
    
    @Test
    @DisplayName("reduceStock devrait lever une exception si quantité négative")
    void testReduceStockThrowsExceptionForNegativeQuantity() {
        Product product = Product.builder().stock(10).build();
        
        assertThatIllegalArgumentException()
            .isThrownBy(() -> product.reduceStock(-5))
            .withMessage("Quantity must be positive");
    }
    
    @Test
    @DisplayName("reduceStock devrait lever une exception si stock insuffisant")
    void testReduceStockThrowsExceptionForInsufficientStock() {
        Product product = Product.builder().stock(5).build();
        
        assertThatIllegalStateException()
            .isThrownBy(() -> product.reduceStock(10))
            .withMessage("Insufficient stock");
    }
    
    @Test
    @DisplayName("increaseStock devrait augmenter le stock correctement")
    void testIncreaseStock() {
        // Arrange
        Product product = Product.builder().stock(10).build();
        
        // Act
        product.increaseStock(5);
        
        // Assert
        assertThat(product.getStock()).isEqualTo(15);
    }
    
    @Test
    @DisplayName("increaseStock devrait initialiser le stock si null")
    void testIncreaseStockWhenStockIsNull() {
        Product product = Product.builder().build();
        
        product.increaseStock(10);
        
        assertThat(product.getStock()).isEqualTo(10);
    }
}
```

**Exécuter les tests** :

```bash
cd common
mvn test
```

#### ✅ Critères de validation

- [ ] La classe `Product` compile sans erreur
- [ ] Lombok génère automatiquement getters, setters, toString, equals, hashCode
- [ ] Le builder fonctionne correctement
- [ ] Tous les tests passent (10 tests)

---

## 🔍 Exercice 3 : Classe ProductFilter avec Streams API (1h30)

### 3.1 Créer la classe ProductFilter

**Objectif** : Implémenter une classe utilitaire pour filtrer et traiter des listes de produits en utilisant la Streams API.

**Fichier** : `common/src/main/java/ma/ensaf/ecommerce/common/util/ProductFilter.java`

```java
package ma.ensaf.ecommerce.common.util;

import ma.ensaf.ecommerce.common.model.Product;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe utilitaire pour filtrer et traiter des collections de produits
 * en utilisant les Streams API et la programmation fonctionnelle
 */
public class ProductFilter {
    
    /**
     * Filtre les produits par catégorie
     */
    public List<Product> filterByCategory(List<Product> products, String category) {
        if (products == null || category == null) {
            return Collections.emptyList();
        }
        
        return products.stream()
            .filter(p -> category.equals(p.getCategory()))
            .collect(Collectors.toList());
    }
    
    /**
     * Filtre les produits par fourchette de prix
     */
    public List<Product> filterByPriceRange(List<Product> products, double minPrice, double maxPrice) {
        if (products == null) {
            return Collections.emptyList();
        }
        
        return products.stream()
            .filter(p -> p.getPrice() != null)
            .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
            .collect(Collectors.toList());
    }
    
    /**
     * Filtre les produits disponibles à la vente
     */
    public List<Product> filterAvailableProducts(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }
        
        return products.stream()
            .filter(Product::isAvailableForSale)
            .collect(Collectors.toList());
    }
    
    /**
     * Recherche des produits par nom (insensible à la casse)
     */
    public List<Product> searchByNameContaining(List<Product> products, String searchTerm) {
        if (products == null || searchTerm == null || searchTerm.isEmpty()) {
            return Collections.emptyList();
        }
        
        String lowerSearchTerm = searchTerm.toLowerCase();
        return products.stream()
            .filter(p -> p.getName() != null)
            .filter(p -> p.getName().toLowerCase().contains(lowerSearchTerm))
            .collect(Collectors.toList());
    }
    
    /**
     * Trouve le produit le moins cher
     */
    public Optional<Product> findCheapest(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return Optional.empty();
        }
        
        return products.stream()
            .filter(p -> p.getPrice() != null)
            .min(Comparator.comparing(Product::getPrice));
    }
    
    /**
     * Trouve le produit le plus cher
     */
    public Optional<Product> findMostExpensive(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return Optional.empty();
        }
        
        return products.stream()
            .filter(p -> p.getPrice() != null)
            .max(Comparator.comparing(Product::getPrice));
    }
    
    /**
     * Calcule le prix moyen des produits
     */
    public double calculateAveragePrice(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return 0.0;
        }
        
        return products.stream()
            .filter(p -> p.getPrice() != null)
            .mapToDouble(Product::getPrice)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Calcule le prix total de tous les produits
     */
    public double calculateTotalValue(List<Product> products) {
        if (products == null) {
            return 0.0;
        }
        
        return products.stream()
            .filter(p -> p.getPrice() != null && p.getStock() != null)
            .mapToDouble(p -> p.getPrice() * p.getStock())
            .sum();
    }
    
    /**
     * Groupe les produits par catégorie
     */
    public Map<String, List<Product>> groupByCategory(List<Product> products) {
        if (products == null) {
            return Collections.emptyMap();
        }
        
        return products.stream()
            .filter(p -> p.getCategory() != null)
            .collect(Collectors.groupingBy(Product::getCategory));
    }
    
    /**
     * Compte les produits par catégorie
     */
    public Map<String, Long> countByCategory(List<Product> products) {
        if (products == null) {
            return Collections.emptyMap();
        }
        
        return products.stream()
            .filter(p -> p.getCategory() != null)
            .collect(Collectors.groupingBy(
                Product::getCategory,
                Collectors.counting()
            ));
    }
    
    /**
     * Obtient la liste des noms de produits en majuscules
     */
    public List<String> getProductNamesUpperCase(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }
        
        return products.stream()
            .map(Product::getName)
            .filter(Objects::nonNull)
            .map(String::toUpperCase)
            .collect(Collectors.toList());
    }
    
    /**
     * Trie les produits par prix (croissant)
     */
    public List<Product> sortByPrice(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }
        
        return products.stream()
            .filter(p -> p.getPrice() != null)
            .sorted(Comparator.comparing(Product::getPrice))
            .collect(Collectors.toList());
    }
    
    /**
     * Trie les produits par nom
     */
    public List<Product> sortByName(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }
        
        return products.stream()
            .filter(p -> p.getName() != null)
            .sorted(Comparator.comparing(Product::getName))
            .collect(Collectors.toList());
    }
    
    /**
     * Obtient les n produits les moins chers
     */
    public List<Product> getTopNCheapest(List<Product> products, int n) {
        if (products == null || n <= 0) {
            return Collections.emptyList();
        }
        
        return products.stream()
            .filter(p -> p.getPrice() != null)
            .sorted(Comparator.comparing(Product::getPrice))
            .limit(n)
            .collect(Collectors.toList());
    }
    
    /**
     * Vérifie si tous les produits sont disponibles
     */
    public boolean areAllProductsAvailable(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return false;
        }
        
        return products.stream()
            .allMatch(Product::isAvailable);
    }
    
    /**
     * Vérifie si au moins un produit est disponible
     */
    public boolean isAnyProductAvailable(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return false;
        }
        
        return products.stream()
            .anyMatch(Product::isAvailable);
    }
    
    /**
     * Compte le nombre de produits en rupture de stock
     */
    public long countOutOfStockProducts(List<Product> products) {
        if (products == null) {
            return 0;
        }
        
        return products.stream()
            .filter(Product::isOutOfStock)
            .count();
    }
}
```

### 3.2 Créer les tests pour ProductFilter

**Fichier** : `common/src/test/java/ma/ensaf/ecommerce/common/util/ProductFilterTest.java`

```java
package ma.ensaf.ecommerce.common.util;

import ma.ensaf.ecommerce.common.model.Product;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class ProductFilterTest {
    
    private ProductFilter filter;
    private List<Product> products;
    
    @BeforeEach
    void setUp() {
        filter = new ProductFilter();
        products = createSampleProducts();
    }
    
    @Test
    @DisplayName("Filtrer par catégorie devrait retourner uniquement les produits de cette catégorie")
    void testFilterByCategory() {
        // Act
        List<Product> electronics = filter.filterByCategory(products, "Electronics");
        
        // Assert
        assertThat(electronics).hasSize(3);
        assertThat(electronics).allMatch(p -> p.getCategory().equals("Electronics"));
        assertThat(electronics).extracting(Product::getName)
            .contains("Laptop Dell XPS 13", "iPhone 15 Pro", "Samsung Galaxy S24");
    }
    
    @Test
    @DisplayName("Filtrer par catégorie avec null devrait retourner liste vide")
    void testFilterByCategoryWithNull() {
        assertThat(filter.filterByCategory(null, "Electronics")).isEmpty();
        assertThat(filter.filterByCategory(products, null)).isEmpty();
    }
    
    @Test
    @DisplayName("Filtrer par fourchette de prix")
    void testFilterByPriceRange() {
        // Act
        List<Product> inRange = filter.filterByPriceRange(products, 50.0, 150.0);
        
        // Assert
        assertThat(inRange).isNotEmpty();
        assertThat(inRange).allMatch(p -> p.getPrice() >= 50 && p.getPrice() <= 150);
    }
    
    @Test
    @DisplayName("Filtrer les produits disponibles")
    void testFilterAvailableProducts() {
        // Act
        List<Product> available = filter.filterAvailableProducts(products);
        
        // Assert
        assertThat(available).isNotEmpty();
        assertThat(available).allMatch(Product::isAvailableForSale);
    }
    
    @Test
    @DisplayName("Rechercher par nom (insensible à la casse)")
    void testSearchByNameContaining() {
        // Act
        List<Product> results = filter.searchByNameContaining(products, "pro");
        
        // Assert
        assertThat(results).hasSizeGreaterThan(0);
        assertThat(results).allMatch(p -> 
            p.getName().toLowerCase().contains("pro")
        );
    }
    
    @Test
    @DisplayName("Trouver le produit le moins cher")
    void testFindCheapest() {
        // Act
        Optional<Product> cheapest = filter.findCheapest(products);
        
        // Assert
        assertThat(cheapest).isPresent();
        assertThat(cheapest.get().getName()).isEqualTo("T-Shirt Nike");
        assertThat(cheapest.get().getPrice()).isEqualTo(29.99);
    }
    
    @Test
    @DisplayName("Trouver le moins cher sur liste vide retourne Optional.empty()")
    void testFindCheapestEmptyList() {
        // Act
        Optional<Product> cheapest = filter.findCheapest(Collections.emptyList());
        
        // Assert
        assertThat(cheapest).isEmpty();
    }
    
    @Test
    @DisplayName("Trouver le produit le plus cher")
    void testFindMostExpensive() {
        // Act
        Optional<Product> expensive = filter.findMostExpensive(products);
        
        // Assert
        assertThat(expensive).isPresent();
        assertThat(expensive.get().getPrice()).isEqualTo(1299.99);
    }
    
    @Test
    @DisplayName("Calculer le prix moyen")
    void testCalculateAveragePrice() {
        // Act
        double average = filter.calculateAveragePrice(products);
        
        // Assert
        assertThat(average).isGreaterThan(0);
        assertThat(average).isLessThan(1500);
    }
    
    @Test
    @DisplayName("Calculer le prix moyen sur liste vide retourne 0")
    void testCalculateAveragePriceEmptyList() {
        assertThat(filter.calculateAveragePrice(Collections.emptyList())).isEqualTo(0.0);
    }
    
    @Test
    @DisplayName("Calculer la valeur totale du stock")
    void testCalculateTotalValue() {
        // Act
        double total = filter.calculateTotalValue(products);
        
        // Assert
        assertThat(total).isGreaterThan(0);
    }
    
    @Test
    @DisplayName("Grouper par catégorie")
    void testGroupByCategory() {
        // Act
        Map<String, List<Product>> grouped = filter.groupByCategory(products);
        
        // Assert
        assertThat(grouped).hasSize(3);
        assertThat(grouped).containsKeys("Electronics", "Clothing", "Furniture");
        assertThat(grouped.get("Electronics")).hasSize(3);
        assertThat(grouped.get("Clothing")).hasSize(2);
    }
    
    @Test
    @DisplayName("Compter par catégorie")
    void testCountByCategory() {
        // Act
        Map<String, Long> counts = filter.countByCategory(products);
        
        // Assert
        assertThat(counts).hasSize(3);
        assertThat(counts.get("Electronics")).isEqualTo(3);
        assertThat(counts.get("Clothing")).isEqualTo(2);
        assertThat(counts.get("Furniture")).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Obtenir les noms en majuscules")
    void testGetProductNamesUpperCase() {
        // Act
        List<String> names = filter.getProductNamesUpperCase(products);
        
        // Assert
        assertThat(names).isNotEmpty();
        assertThat(names).allMatch(name -> name.equals(name.toUpperCase()));
        assertThat(names).contains("LAPTOP DELL XPS 13", "IPHONE 15 PRO");
    }
    
    @Test
    @DisplayName("Trier par prix")
    void testSortByPrice() {
        // Act
        List<Product> sorted = filter.sortByPrice(products);
        
        // Assert
        assertThat(sorted).isNotEmpty();
        assertThat(sorted.get(0).getPrice()).isLessThanOrEqualTo(sorted.get(1).getPrice());
        assertThat(sorted.get(0).getName()).isEqualTo("T-Shirt Nike");
    }
    
    @Test
    @DisplayName("Trier par nom")
    void testSortByName() {
        // Act
        List<Product> sorted = filter.sortByName(products);
        
        // Assert
        assertThat(sorted).isNotEmpty();
        // Vérifier que c'est trié alphabétiquement
        for (int i = 0; i < sorted.size() - 1; i++) {
            assertThat(sorted.get(i).getName())
                .isLessThanOrEqualTo(sorted.get(i + 1).getName());
        }
    }
    
    @Test
    @DisplayName("Obtenir les N produits les moins chers")
    void testGetTopNCheapest() {
        // Act
        List<Product> top3 = filter.getTopNCheapest(products, 3);
        
        // Assert
        assertThat(top3).hasSize(3);
        assertThat(top3.get(0).getPrice()).isLessThanOrEqualTo(top3.get(1).getPrice());
        assertThat(top3.get(1).getPrice()).isLessThanOrEqualTo(top3.get(2).getPrice());
    }
    
    @Test
    @DisplayName("Vérifier si tous les produits sont disponibles")
    void testAreAllProductsAvailable() {
        // Act
        boolean allAvailable = filter.areAllProductsAvailable(products);
        
        // Assert
        assertThat(allAvailable).isFalse(); // Car un produit n'est pas disponible dans nos données
    }
    
    @Test
    @DisplayName("Vérifier si au moins un produit est disponible")
    void testIsAnyProductAvailable() {
        // Act
        boolean anyAvailable = filter.isAnyProductAvailable(products);
        
        // Assert
        assertThat(anyAvailable).isTrue();
    }
    
    @Test
    @DisplayName("Compter les produits en rupture de stock")
    void testCountOutOfStockProducts() {
        // Act
        long count = filter.countOutOfStockProducts(products);
        
        // Assert
        assertThat(count).isGreaterThanOrEqualTo(0);
    }
    
    private List<Product> createSampleProducts() {
        return Arrays.asList(
            Product.builder()
                .id(1L).name("Laptop Dell XPS 13").price(1299.99)
                .category("Electronics").available(true).stock(15)
                .createdAt(LocalDateTime.now())
                .build(),
            Product.builder()
                .id(2L).name("iPhone 15 Pro").price(1199.99)
                .category("Electronics").available(true).stock(25)
                .createdAt(LocalDateTime.now())
                .build(),
            Product.builder()
                .id(3L).name("Samsung Galaxy S24").price(999.99)
                .category("Electronics").available(true).stock(30)
                .createdAt(LocalDateTime.now())
                .build(),
            Product.builder()
                .id(4L).name("T-Shirt Nike").price(29.99)
                .category("Clothing").available(true).stock(100)
                .createdAt(LocalDateTime.now())
                .build(),
            Product.builder()
                .id(5L).name("Jean Levi's 501").price(89.99)
                .category("Clothing").available(false).stock(0)
                .createdAt(LocalDateTime.now())
                .build(),
            Product.builder()
                .id(6L).name("Chaise Bureau Ergonomique").price(299.99)
                .category("Furniture").available(true).stock(5)
                .createdAt(LocalDateTime.now())
                .build()
        );
    }
}
```

**Exécuter les tests** :

```bash
cd common
mvn test
```

#### ✅ Critères de validation

- [ ] La classe `ProductFilter` compile sans erreur
- [ ] Toutes les méthodes utilisent la Streams API
- [ ] Tous les tests passent (23 tests)
- [ ] Couverture de code > 80%

---

## 🎓 Exercice 4 : Génériques avancés - Classe Pair (45 min)

### 4.1 Créer une classe générique Pair

**Objectif** : Créer une classe générique réutilisable pour stocker deux valeurs.

**Fichier** : `common/src/main/java/ma/ensaf/ecommerce/common/util/Pair.java`

```java
package ma.ensaf.ecommerce.common.util;

import lombok.*;

/**
 * Classe générique représentant une paire de valeurs
 * 
 * @param <K> Type de la première valeur (key)
 * @param <V> Type de la seconde valeur (value)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pair<K, V> {
    
    private K key;
    private V value;
    
    /**
     * Factory method pour créer une paire
     */
    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
    
    /**
     * Transforme la key avec une fonction
     */
    public <R> Pair<R, V> mapKey(java.util.function.Function<K, R> mapper) {
        return new Pair<>(mapper.apply(key), value);
    }
    
    /**
     * Transforme la value avec une fonction
     */
    public <R> Pair<K, R> mapValue(java.util.function.Function<V, R> mapper) {
        return new Pair<>(key, mapper.apply(value));
    }
    
    /**
     * Transforme les deux éléments
     */
    public <R1, R2> Pair<R1, R2> map(
        java.util.function.Function<K, R1> keyMapper,
        java.util.function.Function<V, R2> valueMapper
    ) {
        return new Pair<>(keyMapper.apply(key), valueMapper.apply(value));
    }
}
```

### 4.2 Créer les tests pour Pair

**Fichier** : `common/src/test/java/ma/ensaf/ecommerce/common/util/PairTest.java`

```java
package ma.ensaf.ecommerce.common.util;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

class PairTest {
    
    @Test
    @DisplayName("Créer une paire avec of()")
    void testOfMethod() {
        // Act
        Pair<String, Integer> pair = Pair.of("Age", 25);
        
        // Assert
        assertThat(pair.getKey()).isEqualTo("Age");
        assertThat(pair.getValue()).isEqualTo(25);
    }
    
    @Test
    @DisplayName("Transformer la key avec mapKey()")
    void testMapKey() {
        // Arrange
        Pair<String, Integer> pair = Pair.of("hello", 5);
        
        // Act
        Pair<String, Integer> result = pair.mapKey(String::toUpperCase);
        
        // Assert
        assertThat(result.getKey()).isEqualTo("HELLO");
        assertThat(result.getValue()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("Transformer la value avec mapValue()")
    void testMapValue() {
        // Arrange
        Pair<String, Integer> pair = Pair.of("count", 10);
        
        // Act
        Pair<String, String> result = pair.mapValue(v -> "Value: " + v);
        
        // Assert
        assertThat(result.getKey()).isEqualTo("count");
        assertThat(result.getValue()).isEqualTo("Value: 10");
    }
    
    @Test
    @DisplayName("Transformer les deux éléments avec map()")
    void testMap() {
        // Arrange
        Pair<String, Integer> pair = Pair.of("price", 100);
        
        // Act
        Pair<Integer, String> result = pair.map(
            String::length,
            v -> v + " EUR"
        );
        
        // Assert
        assertThat(result.getKey()).isEqualTo(5); // length of "price"
        assertThat(result.getValue()).isEqualTo("100 EUR");
    }
    
    @Test
    @DisplayName("Paire avec types différents")
    void testDifferentTypes() {
        Pair<String, Product> pair = Pair.of("laptop", createProduct());
        
        assertThat(pair.getKey()).isEqualTo("laptop");
        assertThat(pair.getValue()).isInstanceOf(Product.class);
    }
    
    private Product createProduct() {
        return Product.builder()
            .id(1L)
            .name("Laptop")
            .price(999.99)
            .build();
    }
}
```

#### ✅ Critères de validation

- [ ] La classe `Pair` compile sans erreur
- [ ] Les génériques fonctionnent avec différents types
- [ ] Tous les tests passent (5 tests)

---

## 🚀 Exercice 5 : Classe Repository générique (30 min - BONUS)

### 5.1 Créer une interface Repository générique

**Fichier** : `common/src/main/java/ma/ensaf/ecommerce/common/util/Repository.java`

```java
package ma.ensaf.ecommerce.common.util;

import java.util.*;
import java.util.function.Predicate;

/**
 * Interface générique pour un repository en mémoire
 * 
 * @param <T> Type de l'entité
 * @param <ID> Type de l'identifiant
 */
public interface Repository<T, ID> {
    
    /**
     * Sauvegarde une entité
     */
    T save(T entity);
    
    /**
     * Trouve une entité par son ID
     */
    Optional<T> findById(ID id);
    
    /**
     * Trouve toutes les entités
     */
    List<T> findAll();
    
    /**
     * Supprime une entité par son ID
     */
    void deleteById(ID id);
    
    /**
     * Vérifie si une entité existe
     */
    boolean existsById(ID id);
    
    /**
     * Compte le nombre d'entités
     */
    long count();
    
    /**
     * Trouve les entités correspondant au prédicat
     */
    List<T> findBy(Predicate<T> predicate);
}
```

### 5.2 Implémenter un ProductRepository

**Fichier** : `common/src/main/java/ma/ensaf/ecommerce/common/util/InMemoryProductRepository.java`

```java
package ma.ensaf.ecommerce.common.util;

import ma.ensaf.ecommerce.common.model.Product;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implémentation en mémoire du repository de produits
 */
public class InMemoryProductRepository implements Repository<Product, Long> {
    
    private final Map<Long, Product> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.getAndIncrement());
        }
        storage.put(product.getId(), product);
        return product;
    }
    
    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }
    
    @Override
    public List<Product> findAll() {
        return new ArrayList<>(storage.values());
    }
    
    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
    
    @Override
    public long count() {
        return storage.size();
    }
    
    @Override
    public List<Product> findBy(Predicate<Product> predicate) {
        return storage.values().stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    /**
     * Méthode spécifique : trouve par catégorie
     */
    public List<Product> findByCategory(String category) {
        return findBy(p -> category.equals(p.getCategory()));
    }
    
    /**
     * Méthode spécifique : trouve les produits disponibles
     */
    public List<Product> findAvailable() {
        return findBy(Product::isAvailableForSale);
    }
    
    /**
     * Nettoie le repository (utile pour les tests)
     */
    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }
}
```

### 5.3 Tests du Repository

**Fichier** : `common/src/test/java/ma/ensaf/ecommerce/common/util/InMemoryProductRepositoryTest.java`

```java
package ma.ensaf.ecommerce.common.util;

import ma.ensaf.ecommerce.common.model.Product;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

class InMemoryProductRepositoryTest {
    
    private InMemoryProductRepository repository;
    
    @BeforeEach
    void setUp() {
        repository = new InMemoryProductRepository();
    }
    
    @Test
    @DisplayName("Save devrait générer un ID automatiquement")
    void testSaveGeneratesId() {
        // Arrange
        Product product = Product.builder()
            .name("Laptop")
            .price(999.99)
            .build();
        
        // Act
        Product saved = repository.save(product);
        
        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("FindById devrait retourner le produit")
    void testFindById() {
        // Arrange
        Product product = repository.save(
            Product.builder().name("Laptop").price(999.99).build()
        );
        
        // Act
        Optional<Product> found = repository.findById(product.getId());
        
        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Laptop");
    }
    
    @Test
    @DisplayName("FindById avec ID inexistant devrait retourner Optional.empty()")
    void testFindByIdNotFound() {
        Optional<Product> found = repository.findById(999L);
        assertThat(found).isEmpty();
    }
    
    @Test
    @DisplayName("FindAll devrait retourner tous les produits")
    void testFindAll() {
        // Arrange
        repository.save(Product.builder().name("Product 1").build());
        repository.save(Product.builder().name("Product 2").build());
        repository.save(Product.builder().name("Product 3").build());
        
        // Act
        List<Product> all = repository.findAll();
        
        // Assert
        assertThat(all).hasSize(3);
    }
    
    @Test
    @DisplayName("DeleteById devrait supprimer le produit")
    void testDeleteById() {
        // Arrange
        Product product = repository.save(
            Product.builder().name("Laptop").build()
        );
        
        // Act
        repository.deleteById(product.getId());
        
        // Assert
        assertThat(repository.findById(product.getId())).isEmpty();
        assertThat(repository.count()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("ExistsById devrait retourner true si le produit existe")
    void testExistsById() {
        Product product = repository.save(
            Product.builder().name("Laptop").build()
        );
        
        assertThat(repository.existsById(product.getId())).isTrue();
        assertThat(repository.existsById(999L)).isFalse();
    }
    
    @Test
    @DisplayName("Count devrait retourner le nombre de produits")
    void testCount() {
        assertThat(repository.count()).isEqualTo(0);
        
        repository.save(Product.builder().name("P1").build());
        repository.save(Product.builder().name("P2").build());
        
        assertThat(repository.count()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("FindBy avec prédicat devrait filtrer correctement")
    void testFindBy() {
        // Arrange
        repository.save(Product.builder().name("Laptop").price(999.99).build());
        repository.save(Product.builder().name("Mouse").price(29.99).build());
        repository.save(Product.builder().name("Keyboard").price(79.99).build());
        
        // Act
        List<Product> expensive = repository.findBy(p -> p.getPrice() > 50);
        
        // Assert
        assertThat(expensive).hasSize(2);
        assertThat(expensive).allMatch(p -> p.getPrice() > 50);
    }
    
    @Test
    @DisplayName("FindByCategory devrait filtrer par catégorie")
    void testFindByCategory() {
        // Arrange
        repository.save(Product.builder().category("Electronics").build());
        repository.save(Product.builder().category("Electronics").build());
        repository.save(Product.builder().category("Clothing").build());
        
        // Act
        List<Product> electronics = repository.findByCategory("Electronics");
        
        // Assert
        assertThat(electronics).hasSize(2);
    }
}
```

#### ✅ Critères de validation

- [ ] L'interface `Repository` est correctement définie
- [ ] `InMemoryProductRepository` implémente toutes les méthodes
- [ ] Les génériques fonctionnent correctement
- [ ] Tous les tests passent (9 tests)

---

## 📊 Récapitulatif et Validation Finale (30 min)

### Checklist complète du Module 1

#### Structure du projet
- [ ] Projet Maven multi-modules créé
- [ ] 4 modules présents (common, catalogue-service, order-service, user-service)
- [ ] POM parent correctement configuré
- [ ] Lombok configuré dans tous les modules
- [ ] JUnit 6 et AssertJ configurés

#### Code implémenté
- [ ] Classe `Product` avec Lombok (@Data, @SuperBuilder)
- [ ] Classe `ProductFilter` avec toutes les méthodes Streams
- [ ] Classe générique `Pair<K, V>`
- [ ] Interface `Repository<T, ID>` (bonus)
- [ ] Classe `InMemoryProductRepository` (bonus)

#### Tests
- [ ] `ProductTest` : 10 tests ✅
- [ ] `ProductFilterTest` : 23 tests ✅
- [ ] `PairTest` : 5 tests ✅
- [ ] `InMemoryProductRepositoryTest` : 9 tests (bonus) ✅

#### Validation Maven

```bash
# À la racine du projet
mvn clean install

# Vérifier que tous les tests passent
mvn test

# Vérifier le nombre de tests exécutés
# Devrait afficher : Tests run: 38+ (si bonus fait)
```

#### Couverture attendue

```
Tests run: 47, Failures: 0, Errors: 0, Skipped: 0
```

### 🎯 Livrables

À la fin du Module 1, vous devez avoir :

1. **Structure projet Maven** multi-modules fonctionnelle
2. **Module common** avec :
   - Classe `Product` (modèle de base)
   - Classe `ProductFilter` (utilitaires Streams)
   - Classe `Pair` (génériques)
   - Interface `Repository` + implémentation (bonus)
3. **Tests unitaires** complets avec excellente couverture
4. **Documentation** : README.md expliquant comment compiler et tester

### 📝 Questions de compréhension

Pour valider votre compréhension, répondez à ces questions :

1. **Génériques** : Quelle est la différence entre `List<?>`, `List<? extends Number>` et `List<? super Integer>` ?
2. **Lambda** : Réécrivez ce code avec une lambda : `products.stream().filter(new Predicate<Product>() { public boolean test(Product p) { return p.getPrice() > 100; } })`
3. **Streams** : Quelle est la différence entre `map()` et `flatMap()` ?
4. **Optional** : Pourquoi utiliser `orElseGet()` plutôt que `orElse()` ?
5. **Lombok** : Pourquoi préférer `@SuperBuilder` à `@Builder` ?

---

## 🚀 Pour aller plus loin (optionnel)

Si vous avez terminé en avance :

1. **Ajouter d'autres méthodes dans ProductFilter** :
   - `findByPriceGreaterThan()`
   - `findByStockLessThan()`
   - `calculateTotalStock()`

2. **Créer une classe `Order`** dans le module `common`

3. **Implémenter un `OrderRepository`** en mémoire

4. **Écrire des tests paramétrés** avec `@ParameterizedTest`

---

## 📚 Ressources

- [Maven Getting Started](https://maven.apache.org/guides/getting-started/)
- [Lombok Documentation](https://projectlombok.org/)
- [Java Streams API](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)
- [JUnit 6 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/doc/)

---

**Félicitations ! Vous avez terminé le Module 1 !** 🎉

Vous maîtrisez maintenant les fondamentaux Java modernes et êtes prêts à attaquer le Module 2 : Premier Microservice avec Spring Boot.
