# Module 1 : Exercices Pratiques - Fondamentaux Java Moderne

**Durée** : 4h  
**Mode** : Travail autonome avec validation par tests

---

## 🎯 Objectifs

À la fin de ces exercices, vous aurez :

- Créé la structure du projet Maven multi-modules
- Configuré Lombok et les dépendances de test  
- Implémenté une classe `ProductFilter` avec Streams API
- Écrit des tests unitaires avec JUnit 6 et AssertJ

---

## 📚 Ressources

- **Cours théorique** : `docs/module1/cours.md`
- **Correction** : `docs/module1/correction.md` ⚠️ À consulter APRÈS vos tentatives
- **Documentation** : `docs/stack-technique.md` et `docs/conception.md`

---

## 📦 Exercice 1 : Setup du Projet Maven (45 min)

### Objectif

Créer un projet Maven multi-modules avec 4 modules : `common`, `catalogue-service`, `order-service`, `user-service`.

### Structure attendue

```text
formation-java-spring-ms/
├── pom.xml                    # POM parent
├── common/
│   ├── pom.xml
│   └── src/main/java/ma/ensaf/ecommerce/common/
├── catalogue-service/
│   ├── pom.xml
│   └── src/...
├── order-service/
│   ├── pom.xml
│   └── src/...
└── user-service/
    ├── pom.xml
    └── src/...
```

### Instructions

#### 1. Créer le POM parent

Créez `pom.xml` à la racine avec :

template de base Maven

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- Compléter ici  -->

    <modules>
        <module>common</module>
        <!-- Compléter ici  -->
    </modules>

    <properties>
        <java.version>21</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        
        <!-- Versions des dépendances -->
        <!-- Compléter ici  -->
    </properties>

    <dependencies>
        <!-- Compléter ici  -->
    </dependencies>

    <dependencyManagement>
        <dependencies>            
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

- **packaging** : `pom`
- **groupId** : `ma.ensaf.ecommerce`
- **artifactId** : `ecommerce-platform`
- **version** : `1.0.0-SNAPSHOT`
- **name** : E-Commerce Platform
- **description** : Plateforme e-commerce en microservices
- **modules** : common, catalogue-service, order-service, user-service
- **properties** :
  - Java 21
  - Lombok 1.18.42
  - JUnit 6.0.1
  - AssertJ 3.27.6

Configurez dans `<dependencies>` :

- Lombok (scope `provided`)
- JUnit Jupiter (scope `test`)
- AssertJ (scope `test`)

#### 2. Créer le module common

Dans `common/pom.xml` :

- Déclarez le `<parent>`
- Définissez `artifactId` et `name` (Common - Utilities and Shared Classes)

Créez l'arborescence des packages :

```bash
mkdir -p common/src/main/java/ma/ensaf/ecommerce/common/{model,util,exception}
mkdir -p common/src/test/java/ma/ensaf/ecommerce/common/{model,util}
```

#### 3. Créer les modules services

Créez chaque module avec :

- Un `pom.xml` déclarant le parent
- Définissez `artifactId` et `name`
- Dépendance vers `common`

#### 4. Valider

Exécutez :

```bash
mvn clean compile
```

✅ **Succès attendu** : `BUILD SUCCESS` sans erreur

---

## 🏗️ Exercice 2 : Classe Product avec Lombok (30 min)

### Objectif

Créer une classe `Product` en utilisant Lombok et écrire ses tests.

### Instructions

#### Classe Product

Créez `common/src/main/java/ma/ensaf/ecommerce/common/model/Product.java` avec :

**Attributs** :

- `Long id`
- `String name`
- `String description`
- `Double price`
- `String category`
- `Integer stock`
- `boolean available`
- `LocalDateTime createdAt`
- `LocalDateTime updatedAt`

**Annotations Lombok** :

- `@Data` (getters/setters/toString/equals/hashCode)
- `@SuperBuilder` (pattern builder avec héritage)
- `@NoArgsConstructor`
- `@AllArgsConstructor`

**Méthodes métier à implémenter** :

```java
public boolean isOutOfStock() {
    // Retourne true si stock null ou <= 0
}

public boolean isAvailableForSale() {
    // Retourne true si available ET pas en rupture
}

public void reduceStock(int quantity) {
    // Réduit le stock de quantity
    // Lever IllegalArgumentException si quantity < 0
    // Lever IllegalStateException si stock insuffisant
}

public void increaseStock(int quantity) {
    // Augmente le stock (initialise à 0 si null)
    // Lever IllegalArgumentException si quantity < 0
}
```

#### Tests à écrire

Créez `common/src/test/java/ma/ensaf/ecommerce/common/model/ProductTest.java`.

Implémentez les tests suivants (utilisez AssertJ) :

1. `testProductBuilder()` - Vérifie que le builder fonctionne
2. `testIsOutOfStock()` - Stock = 0 → true
3. `testIsNotOutOfStock()` - Stock > 0 → false
4. `testIsAvailableForSale()` - available=true, stock=10 → true
5. `testIsNotAvailableForSale_WhenNotAvailable()` - available=false → false
6. `testReduceStock()` - Stock 10, réduit de 3 → stock=7
7. `testReduceStockThrowsExceptionForNegativeQuantity()` - quantity < 0 → Exception
8. `testReduceStockThrowsExceptionForInsufficientStock()` - réduire plus que stock → Exception
9. `testIncreaseStock()` - Augmente correctement
10. `testIncreaseStockWhenStockIsNull()` - Stock null → initialise à quantity

### Validation

```bash
cd common
mvn test -Dtest=ProductTest
```

✅ **Attendu** : `Tests run: 10, Failures: 0`

---

## 🔍 Exercice 3 : ProductFilter avec Streams API (1h30)

### Objectif

Créer une classe utilitaire pour filtrer des produits avec la Streams API.

### Instructions

Créez `common/src/main/java/ma/ensaf/ecommerce/common/util/ProductFilter.java`.

Cette classe doit contenir les méthodes suivantes (**sans constructeur, méthodes d'instance**) :

#### Méthodes à implémenter

| Méthode | Signature | Description |
|---------|-----------|-------------|
| `filterByCategory` | `List<Product> filterByCategory(List<Product> products, String category)` | Filtre par catégorie exacte |
| `filterByPriceRange` | `List<Product> filterByPriceRange(List<Product> products, double min, double max)` | Produits entre min et max |
| `filterAvailableProducts` | `List<Product> filterAvailableProducts(List<Product> products)` | Produits disponibles (isAvailableForSale) |
| `searchByNameContaining` | `List<Product> searchByNameContaining(List<Product> products, String term)` | Recherche insensible à la casse |
| `findCheapest` | `Optional<Product> findCheapest(List<Product> products)` | Produit le moins cher |
| `findMostExpensive` | `Optional<Product> findMostExpensive(List<Product> products)` | Produit le plus cher |
| `calculateAveragePrice` | `double calculateAveragePrice(List<Product> products)` | Prix moyen (0 si liste vide) |
| `calculateTotalValue` | `double calculateTotalValue(List<Product> products)` | Somme de (prix * stock) |
| `groupByCategory` | `Map<String, List<Product>> groupByCategory(List<Product> products)` | Groupe par catégorie |
| `countByCategory` | `Map<String, Long> countByCategory(List<Product> products)` | Compte par catégorie |
| `getProductNamesUpperCase` | `List<String> getProductNamesUpperCase(List<Product> products)` | Noms en majuscules |
| `sortByPrice` | `List<Product> sortByPrice(List<Product> products)` | Tri croissant par prix |
| `sortByName` | `List<String> sortByName(List<Product> products)` | Tri alphabétique |
| `getTopNCheapest` | `List<Product> getTopNCheapest(List<Product> products, int n)` | N premiers moins chers |
| `areAllProductsAvailable` | `boolean areAllProductsAvailable(List<Product> products)` | Tous disponibles ? |
| `isAnyProductAvailable` | `boolean isAnyProductAvailable(List<Product> products)` | Au moins 1 disponible ? |
| `countOutOfStockProducts` | `long countOutOfStockProducts(List<Product> products)` | Nombre en rupture |

### 💡 Conseils

- Toutes les méthodes doivent gérer le cas `products == null` → retourner collection vide ou valeur par défaut
- Utilisez les méthodes de Stream : `filter()`, `map()`, `collect()`, `sorted()`, `min()`, `max()`, `count()`, etc.
- Pour `Optional`, utilisez `orElse()` ou `orElseGet()`
- Pour grouper : `Collectors.groupingBy()`
- Pour compter : `Collectors.counting()`

### Tests à écrire

Créez `common/src/test/java/ma/ensaf/ecommerce/common/util/ProductFilterTest.java`.

Écrivez au minimum ces tests :

1. Tester `filterByCategory()` avec catégorie "Electronics"
2. Tester `filterByCategory()` avec null → liste vide
3. Tester `filterByPriceRange()` entre 50 et 150
4. Tester `filterAvailableProducts()`
5. Tester `searchByNameContaining()` avec "pro" (insensible casse)
6. Tester `findCheapest()` → produit le moins cher
7. Tester `findCheapest()` avec liste vide → Optional.empty()
8. Tester `findMostExpensive()`
9. Tester `calculateAveragePrice()`
10. Tester `calculateAveragePrice()` liste vide → 0.0
11. Tester `calculateTotalValue()`
12. Tester `groupByCategory()` → 3 catégories
13. Tester `countByCategory()`
14. Tester `getProductNamesUpperCase()`
15. Tester `sortByPrice()` → ordre croissant
16. Tester `sortByName()` → ordre alphabétique
17. Tester `getTopNCheapest(3)` → 3 premiers
18. Tester `areAllProductsAvailable()` → false (données mixtes)
19. Tester `isAnyProductAvailable()` → true
20. Tester `countOutOfStockProducts()`

### Données de test

Créez une méthode `createSampleProducts()` retournant :

- 3 produits "Electronics" (Laptop 1299€, iPhone 1199€, Samsung 999€)
- 2 produits "Clothing" (T-Shirt 29€ available, Jean 89€ not available)
- 1 produit "Furniture" (Chaise 299€)

### Validation

```bash
cd common
mvn test -Dtest=ProductFilterTest
```

✅ **Attendu** : `Tests run: 20+, Failures: 0`

---

## 🎓 Exercice 4 : Classe générique Pair (45 min)

### Objectif

Créer une classe générique `Pair<K, V>` pour stocker deux valeurs.

### Instructions

Créez `common/src/main/java/ma/ensaf/ecommerce/common/util/Pair.java`.

**Attributs** :

- `K key`
- `V value`

**Constructeurs** :

- Constructeur avec paramètres
- Constructeur sans paramètres

**Annotations Lombok** :

- `@Data`
- `@AllArgsConstructor`
- `@NoArgsConstructor`

**Méthodes à implémenter** :

```java
// Factory method
public static <K, V> Pair<K, V> of(K key, V value)

// Transforme la key
public <R> Pair<R, V> mapKey(Function<K, R> mapper)

// Transforme la value
public <R> Pair<K, R> mapValue(Function<V, R> mapper)

// Transforme les deux
public <R1, R2> Pair<R1, R2> map(Function<K, R1> keyMapper, Function<V, R2> valueMapper)
```

### Tests à écrire

Créez `PairTest.java` avec :

1. Test création avec `of("Age", 25)`
2. Test `mapKey()` - transformer key en majuscules
3. Test `mapValue()` - transformer value en String
4. Test `map()` - transformer les deux
5. Test avec types différents (String, Product)

### Validation

```bash
cd common
mvn test -Dtest=PairTest
```

✅ **Attendu** : `Tests run: 5, Failures: 0`

---

## 🚀 Exercice 5 : Repository générique (BONUS - 30 min)

### Objectif

Créer une interface générique `Repository<T, ID>` et son implémentation en mémoire.

### Instructions

#### Interface Repository

Créez `Repository.java` avec les méthodes :

- `T save(T entity)`
- `Optional<T> findById(ID id)`
- `List<T> findAll()`
- `void deleteById(ID id)`
- `boolean existsById(ID id)`
- `long count()`
- `List<T> findBy(Predicate<T> predicate)`

#### Implémentation InMemoryProductRepository

Créez `InMemoryProductRepository.java` qui implémente `Repository<Product, Long>`.

Utilisez une `Map<Long, Product>` interne et un générateur d'ID (`AtomicLong`).

Ajoutez des méthodes spécifiques :

- `List<Product> findByCategory(String category)`
- `List<Product> findAvailable()`
- `void clear()` (pour les tests)

### Tests à écrire

Au minimum 8 tests :

1. `save()` génère un ID
2. `findById()` retourne le produit
3. `findById()` avec ID inexistant → empty
4. `findAll()` retourne tous les produits
5. `deleteById()` supprime
6. `existsById()` retourne true/false
7. `count()` retourne le bon nombre
8. `findBy()` filtre avec prédicat

### Validation

```bash
cd common
mvn test -Dtest=InMemoryProductRepositoryTest
```

---

## 📊 Validation Finale

### Exécuter tous les tests

```bash
# À la racine du projet
mvn clean test
```

### Résultat attendu

```text
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] ------------------------------------------------------------------------
[INFO] common .............................................. SUCCESS
[INFO] catalogue-service ................................... SUCCESS
[INFO] order-service ....................................... SUCCESS
[INFO] user-service ........................................ SUCCESS
[INFO] ecommerce-platform .................................. SUCCESS
[INFO] ------------------------------------------------------------------------

Tests run: 43+, Failures: 0, Errors: 0, Skipped: 0
```

### Checklist

- [ ] Projet compile sans erreur
- [ ] 4 modules créés et configurés
- [ ] Lombok fonctionne (pas d'erreur compilation)
- [ ] Classe `Product` avec tests (10 tests)
- [ ] Classe `ProductFilter` avec tests (20+ tests)
- [ ] Classe `Pair` avec tests (5 tests)
- [ ] `InMemoryProductRepository` avec tests (8+ tests - bonus)

---

## 🎯 Livrables

À remettre :

1. **Code source** du module `common` complet
2. **Tests** passant à 100%

---

## 💡 Conseils

### Si vous êtes bloqué

1. **Relisez le cours** (`docs/module1/cours.md`)
2. **Consultez la JavaDoc** des classes Stream, Optional, Collectors
3. **Testez dans un main()** pour comprendre
4. **En dernier recours** : regardez la correction

### Stratégie recommandée

1. **Commencez simple** : Setup Maven d'abord
2. **Testez souvent** : `mvn test` après chaque méthode
3. **Utilisez IntelliJ** : Alt+Enter pour les imports, génération getters/setters
4. **TDD** : Écrivez le test avant le code si possible

---

## 📚 Ressources utiles

### Documentation Maven

```bash
# Créer la structure
mvn archetype:generate

# Compiler
mvn compile

# Tester
mvn test

# Voir l'arbre des dépendances
mvn dependency:tree
```
---

**Bon courage ! 💪**

Prenez le temps de bien comprendre chaque concept avant de passer au suivant.
La qualité prime sur la vitesse. Les tests sont là pour vous guider.
