# Module 1 : Exercices Pratiques - Fondamentaux Java Moderne

**Durée** : 7h30
**Mode** : Travail autonome avec validation par tests

---

## 🎯 Objectifs

À la fin de ces exercices, vous aurez :

- Créé la structure du projet Maven multi-modules
- Configuré Lombok et les dépendances de test
- Maîtrisé les génériques avec la classe `Pair<K, V>`
- Implémenté une classe `ProductFilter` avec Streams API
- Créé un pattern Repository générique (bonus)
- Écrit des tests unitaires avec JUnit 6 et AssertJ

---

## 📚 Ressources

- **Cours théorique** : `docs/module1/cours-essentiel.md` (guide rapide) ou `cours-complet.md` (approfondi)
- **Correction** : `docs/module1/correction.md` ⚠️ À consulter APRÈS vos tentatives
- **Troubleshooting** : `docs/module1/TROUBLESHOOTING.md` 🆘 Guide de dépannage des erreurs courantes
- **Documentation** : `docs/stack-technique.md` et `docs/conception.md`

---

## 📦 Exercice 1 : Setup du Projet Maven (1h)

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

> **⚠️ IMPORTANT** : Les tests doivent **toujours** être dans `src/test/java`, jamais dans `src/main/java` !
>
> Pourquoi ? Le JAR final ne contiendra PAS les tests (déploiement plus léger). Voir section 6 du cours pour détails.

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

## 🏗️ Exercice 2 : Classe Product avec Lombok (1h)

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

## 🎓 Exercice 3 : Classe générique Pair (1h)

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

## 🔍 Exercice 4 : ProductFilter avec Streams API (2h30)

### Objectif

Créer une classe utilitaire pour filtrer des produits avec la Streams API.

Cet exercice est **découpé en 2 parties** pour une progression graduelle :
- **Partie A (1h30)** : Méthodes de base (filter, map, sorted, min, max)
- **Partie B (1h)** : Méthodes avancées (groupBy, reduce, collectors complexes)

---

### 🎯 Partie A : Méthodes de Base (1h30)

Créez `common/src/main/java/ma/ensaf/ecommerce/common/util/ProductFilter.java`.

#### 💡 Rappel : Pourquoi les Streams ?

**❌ Avant (Java 7)** :
```java
public List<Product> filterByCategory(List<Product> products, String category) {
    List<Product> result = new ArrayList<>();
    for (Product p : products) {
        if (category.equals(p.getCategory())) {
            result.add(p);
        }
    }
    return result;
}
```

**✅ Maintenant (Java 8+)** :
```java
public List<Product> filterByCategory(List<Product> products, String category) {
    return products.stream()
        .filter(p -> category.equals(p.getCategory()))
        .collect(Collectors.toList());
}
```

**Pourquoi c'est mieux ?**
- Code plus lisible et déclaratif (on dit "quoi" faire, pas "comment")
- Moins de code boilerplate (pas de liste temporaire, pas de boucle)
- Parallélisable facilement (`.parallelStream()`)
- Moins d'erreurs (pas d'oubli d'initialisation, pas d'index)

#### Méthodes à implémenter - Partie A

| Méthode | Signature | Description | Concepts Streams |
|---------|-----------|-------------|------------------|
| `filterByCategory` | `List<Product> filterByCategory(List<Product> products, String category)` | Filtre par catégorie exacte | `filter()` |
| `filterByPriceRange` | `List<Product> filterByPriceRange(List<Product> products, double min, double max)` | Produits entre min et max | `filter()` |
| `filterAvailableProducts` | `List<Product> filterAvailableProducts(List<Product> products)` | Produits disponibles (isAvailableForSale) | `filter()` |
| `searchByNameContaining` | `List<Product> searchByNameContaining(List<Product> products, String term)` | Recherche insensible à la casse | `filter()` + `toLowerCase()` |
| `findCheapest` | `Optional<Product> findCheapest(List<Product> products)` | Produit le moins cher | `min()` |
| `calculateAveragePrice` | `double calculateAveragePrice(List<Product> products)` | Prix moyen (0 si liste vide) | `mapToDouble()` + `average()` |
| `getProductNamesUpperCase` | `List<String> getProductNamesUpperCase(List<Product> products)` | Noms en majuscules | `map()` |
| `sortByPrice` | `List<Product> sortByPrice(List<Product> products)` | Tri croissant par prix | `sorted()` |

#### 💡 Conseils - Partie A

- Toutes les méthodes doivent gérer `products == null` → retourner collection vide ou valeur par défaut
- Pour `Optional`, utilisez `orElse()` ou `orElseGet()`
- Utilisez `Comparator.comparing()` pour le tri
- N'oubliez pas `.collect(Collectors.toList())` pour collecter les résultats

#### Tests à écrire - Partie A

Créez `common/src/test/java/ma/ensaf/ecommerce/common/util/ProductFilterTest.java`.

**12 tests minimum** :

1. `filterByCategory()` avec catégorie "Electronics" → 3 produits
2. `filterByCategory()` avec null → liste vide
3. `filterByPriceRange()` entre 50 et 150
4. `filterAvailableProducts()` → seulement les disponibles
5. `searchByNameContaining()` avec "pro" → insensible à la casse
6. `searchByNameContaining()` avec null → liste vide
7. `findCheapest()` → produit le moins cher
8. `findCheapest()` avec liste vide → Optional.empty()
9. `calculateAveragePrice()` → prix moyen > 0
10. `calculateAveragePrice()` liste vide → 0.0
11. `getProductNamesUpperCase()` → tous en majuscules
12. `sortByPrice()` → ordre croissant vérifié

#### Données de test

Ajoutez une méthode `createSampleProducts()` dans votre classe de test :

```java
private List<Product> createSampleProducts() {
    return Arrays.asList(
        Product.builder()
            .id(1L).name("Laptop Dell XPS 13").price(1299.99)
            .category("Electronics").available(true).stock(15).build(),
        Product.builder()
            .id(2L).name("iPhone 15 Pro").price(1199.99)
            .category("Electronics").available(true).stock(25).build(),
        Product.builder()
            .id(3L).name("Samsung Galaxy S24").price(999.99)
            .category("Electronics").available(true).stock(30).build(),
        Product.builder()
            .id(4L).name("T-Shirt Nike").price(29.99)
            .category("Clothing").available(true).stock(100).build(),
        Product.builder()
            .id(5L).name("Jean Levi's 501").price(89.99)
            .category("Clothing").available(false).stock(0).build(),
        Product.builder()
            .id(6L).name("Chaise Bureau Ergonomique").price(299.99)
            .category("Furniture").available(true).stock(5).build()
    );
}
```

#### ✅ Validation Partie A

```bash
cd common
mvn test -Dtest=ProductFilterTest
```

**Attendu** : `Tests run: 12, Failures: 0`

⚠️ **CHECKPOINT** : Validez cette partie avant de passer à la Partie B !

---

### 🚀 Partie B : Méthodes Avancées (1h)

**Prérequis** : Avoir terminé la Partie A avec succès

#### 💡 Exemple : Grouper et Compter

**❌ Avant (Java 7)** :
```java
public Map<String, Long> countByCategory(List<Product> products) {
    Map<String, Long> counts = new HashMap<>();
    for (Product p : products) {
        String category = p.getCategory();
        if (counts.containsKey(category)) {
            counts.put(category, counts.get(category) + 1);
        } else {
            counts.put(category, 1L);
        }
    }
    return counts;
}
```

**✅ Maintenant (Java 8+)** :
```java
public Map<String, Long> countByCategory(List<Product> products) {
    return products.stream()
        .collect(Collectors.groupingBy(
            Product::getCategory,
            Collectors.counting()
        ));
}
```

**Gain** : 10 lignes → 3 lignes, intention claire, moins de bugs potentiels

#### Méthodes à implémenter - Partie B

| Méthode | Signature | Description | Concepts Streams |
|---------|-----------|-------------|------------------|
| `findMostExpensive` | `Optional<Product> findMostExpensive(List<Product> products)` | Produit le plus cher | `max()` |
| `calculateTotalValue` | `double calculateTotalValue(List<Product> products)` | Somme de (prix * stock) | `mapToDouble()` + `sum()` |
| `groupByCategory` | `Map<String, List<Product>> groupByCategory(List<Product> products)` | Groupe par catégorie | `groupingBy()` |
| `countByCategory` | `Map<String, Long> countByCategory(List<Product> products)` | Compte par catégorie | `groupingBy()` + `counting()` |
| `getTopNCheapest` | `List<Product> getTopNCheapest(List<Product> products, int n)` | N premiers moins chers | `sorted()` + `limit()` |
| `countOutOfStockProducts` | `long countOutOfStockProducts(List<Product> products)` | Nombre en rupture | `filter()` + `count()` |

#### 💡 Conseils - Partie B

- Pour `groupByCategory()` : `Collectors.groupingBy(Product::getCategory)`
- Pour `countByCategory()` : `Collectors.groupingBy(Product::getCategory, Collectors.counting())`
- Pour `getTopNCheapest()` : combiner `sorted()` puis `limit(n)`
- `calculateTotalValue()` : mapper vers prix * stock, puis summer

#### Tests à écrire - Partie B

Ajoutez **8 tests supplémentaires** à `ProductFilterTest` :

13. `findMostExpensive()` → produit le plus cher
14. `calculateTotalValue()` → valeur totale du stock
15. `groupByCategory()` → Map avec 3 catégories
16. `groupByCategory()` → Taille de chaque groupe correcte
17. `countByCategory()` → Map avec comptages corrects
18. `getTopNCheapest(3)` → 3 premiers produits
19. `getTopNCheapest(3)` → ordre croissant vérifié
20. `countOutOfStockProducts()` → compte correct

#### ✅ Validation Partie B

```bash
cd common
mvn test -Dtest=ProductFilterTest
```

**Attendu** : `Tests run: 20, Failures: 0`

---

### 📊 Validation Finale Ex3

Après avoir terminé les deux parties :

```bash
cd common
mvn test -Dtest=ProductFilterTest
```

✅ **Attendu** : `Tests run: 20, Failures: 0`

---

## 🚀 Exercice 5 : Repository générique (BONUS - 1h15)

### Objectif

Créer une interface générique `Repository<T, ID>` et son implémentation en mémoire.

Cet exercice est **découpé en 2 parties** :
- **Partie A (30min)** : Interface Repository
- **Partie B (45min)** : Implémentation en mémoire + tests

---

### 🎯 Partie A : Interface Repository (30min)

#### Instructions

Créez `common/src/main/java/ma/ensaf/ecommerce/common/util/Repository.java`.

**Signature générique** :

```java
public interface Repository<T, ID> {
    // Méthodes à déclarer
}
```

**Méthodes à déclarer** :

- `T save(T entity)` - Sauvegarde une entité
- `Optional<T> findById(ID id)` - Trouve une entité par son ID
- `List<T> findAll()` - Trouve toutes les entités
- `void deleteById(ID id)` - Supprime une entité par son ID
- `boolean existsById(ID id)` - Vérifie si une entité existe
- `long count()` - Compte le nombre d'entités
- `List<T> findBy(Predicate<T> predicate)` - Trouve les entités correspondant au prédicat

#### 💡 Conseils Partie A

- C'est une **interface** → pas d'implémentation, juste les signatures
- Pensez aux imports : `java.util.Optional`, `java.util.List`, `java.util.function.Predicate`
- Ajoutez des JavaDoc pour documenter chaque méthode

#### Mini-Test de Validation

Créez un mini-test temporaire pour vérifier que l'interface compile :

```java
class RepositoryTest {
    @Test
    void interfaceShouldCompile() {
        Repository<Product, Long> repo = null; // Juste pour compiler
        assertThat(repo).isNull(); // OK
    }
}
```

⚠️ **CHECKPOINT** : Validez l'interface avant de passer à l'implémentation !

---

### 🚀 Partie B : Implémentation (45min)

**Prérequis** : Avoir créé l'interface Repository avec succès

#### Instructions

Créez `common/src/main/java/ma/ensaf/ecommerce/common/util/InMemoryProductRepository.java`.

**Structure de base** :

```java
public class InMemoryProductRepository implements Repository<Product, Long> {

    private final Map<Long, Product> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // Implémenter les 7 méthodes de Repository<T, ID>
}
```

**Imports nécessaires** :

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
```

**Méthodes à implémenter** :

1. `save(Product)` :
   - Si product.getId() est null → générer un ID avec `idGenerator.getAndIncrement()`
   - Stocker dans `storage` avec `.put(id, product)`
   - Retourner le product

2. `findById(Long id)` :
   - Utiliser `Optional.ofNullable(storage.get(id))`

3. `findAll()` :
   - Retourner `new ArrayList<>(storage.values())`

4. `deleteById(Long id)` :
   - `storage.remove(id)`

5. `existsById(Long id)` :
   - `storage.containsKey(id)`

6. `count()` :
   - `storage.size()`

7. `findBy(Predicate<Product>)` :
   - `storage.values().stream().filter(predicate).collect(Collectors.toList())`

**Méthodes spécifiques à ajouter** :

```java
// Trouve par catégorie
public List<Product> findByCategory(String category) {
    return findBy(p -> category.equals(p.getCategory()));
}

// Trouve les produits disponibles
public List<Product> findAvailable() {
    return findBy(Product::isAvailableForSale);
}

// Nettoie le repository (utile pour les tests)
public void clear() {
    storage.clear();
    idGenerator.set(1);
}
```

#### Tests à écrire - Partie B

Créez `common/src/test/java/ma/ensaf/ecommerce/common/util/InMemoryProductRepositoryTest.java`.

**9 tests minimum** :

1. `save()` génère un ID automatiquement
2. `findById()` retourne le produit
3. `findById()` avec ID inexistant → Optional.empty()
4. `findAll()` retourne tous les produits
5. `deleteById()` supprime le produit
6. `existsById()` retourne true si le produit existe
7. `count()` retourne le nombre de produits
8. `findBy()` avec prédicat filtre correctement
9. `findByCategory()` filtre par catégorie

#### 💡 Conseils Partie B

- Utilisez `@BeforeEach` pour créer un nouveau repository avant chaque test
- Testez les cas limites : liste vide, ID inexistant, etc.
- Vérifiez que l'ID généré commence à 1 et s'incrémente

#### Validation Partie B

```bash
cd common
mvn test -Dtest=InMemoryProductRepositoryTest
```

✅ **Attendu** : `Tests run: 9, Failures: 0`

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

1. **Consultez le guide de dépannage** : `docs/module1/TROUBLESHOOTING.md` 🆘
2. **Relisez le cours** : `docs/module1/cours-essentiel.md`
3. **Consultez la JavaDoc** des classes Stream, Optional, Collectors
4. **Testez dans un main()** pour comprendre
5. **En dernier recours** : regardez la correction

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
