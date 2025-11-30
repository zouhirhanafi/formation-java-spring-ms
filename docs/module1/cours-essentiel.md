# Module 1 : Java Moderne - Guide Essentiel (Quick Start)

**Durée** : 3h théorie
**Objectif** : Maîtriser les fondamentaux Java 8+ pour développer efficacement avec Spring Boot

> 💡 **Note** : Ce guide contient l'essentiel. Pour approfondir, consultez `cours-complet.md`

---

## 🎯 Ce que vous allez apprendre

- ✅ **Génériques** : Créer du code type-safe et réutilisable
- ✅ **Lambda** : Écrire du code fonctionnel concis
- ✅ **Streams API** : Traiter des collections de manière déclarative
- ✅ **Optional** : Gérer l'absence de valeur sans `null`
- ✅ **Lombok** : Réduire le code boilerplate
- ✅ **Tests** : JUnit 6 + AssertJ
- ✅ **Structure Maven** : Comprendre src/main et src/test

---

## 1. Les Génériques (Generics)

### Pourquoi les génériques ?

Les génériques permettent de créer des classes et méthodes **type-safe** qui fonctionnent avec différents types.

**Sans génériques** (ancien code) :
```java
List list = new ArrayList();
list.add("Hello");
String str = (String) list.get(0); // Cast obligatoire, risque d'erreur
```

**Avec génériques** :
```java
List<String> list = new ArrayList<>();
list.add("Hello");
String str = list.get(0); // Pas de cast, sécurité garantie
```

### Classe générique

```java
public class Box<T> {
    private T content;

    public void set(T content) { this.content = content; }
    public T get() { return content; }
}

// Utilisation
Box<String> stringBox = new Box<>();
Box<Integer> intBox = new Box<>();
```

### Types multiples

```java
public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

// Utilisation
Pair<String, Integer> pair = new Pair<>("Age", 25);
```

**💡 À retenir** : Les génériques évitent les casts et garantissent la sécurité des types à la compilation.

---

## 2. Lambda & Références de Méthodes

### Pourquoi Lambda ?

**Avant (Java 7)** :
```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// Filtrer les noms > 4 caractères
List<String> longNames = new ArrayList<>();
for (String name : names) {
    if (name.length() > 4) {
        longNames.add(name);
    }
}

// Trier
Collections.sort(names, new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
});
```

**Maintenant (Java 8+)** :
```java
// Filtrer
List<String> longNames = names.stream()
    .filter(name -> name.length() > 4)
    .collect(Collectors.toList());

// Trier
Collections.sort(names, (a, b) -> a.compareTo(b));
// ou encore plus simple :
Collections.sort(names, String::compareTo);
```

### Syntaxe Lambda

```java
// Format de base
(paramètres) -> expression
(paramètres) -> { instructions; }

// Exemples concrets
Predicate<Integer> isEven = n -> n % 2 == 0;
Function<String, Integer> length = s -> s.length();
Consumer<String> print = s -> System.out.println(s);
Supplier<Double> random = () -> Math.random();
```

### Interfaces Fonctionnelles Essentielles

| Interface | Méthode | Exemple |
|-----------|---------|---------|
| `Predicate<T>` | `boolean test(T t)` | `n -> n > 0` |
| `Function<T,R>` | `R apply(T t)` | `s -> s.length()` |
| `Consumer<T>` | `void accept(T t)` | `s -> System.out.println(s)` |
| `Supplier<T>` | `T get()` | `() -> new ArrayList<>()` |

### Références de Méthodes

**Raccourci pour les lambda qui appellent une méthode existante** :

```java
// Lambda classique
Function<String, Integer> parse = s -> Integer.parseInt(s);
// Référence de méthode
Function<String, Integer> parse = Integer::parseInt;

// Lambda classique
list.forEach(s -> System.out.println(s));
// Référence de méthode
list.forEach(System.out::println);

// Lambda classique
list.stream().map(s -> s.toUpperCase());
// Référence de méthode
list.stream().map(String::toUpperCase);
```

**Types de références** :
- `Class::staticMethod` → méthode statique
- `object::instanceMethod` → méthode d'instance sur un objet
- `Class::instanceMethod` → méthode d'instance sur un type
- `Class::new` → constructeur

---

## 3. Streams API

### Concept

**Stream** = flux de données que l'on peut transformer, filtrer, agréger **sans modifier la source**.

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Sans Stream (Java 7)
List<Integer> doubled = new ArrayList<>();
for (Integer n : numbers) {
    if (n % 2 == 0) {
        doubled.add(n * 2);
    }
}

// Avec Stream (Java 8+)
List<Integer> doubled = numbers.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2)
    .collect(Collectors.toList());
```

### Opérations Essentielles

#### Créer un Stream

```java
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream = list.stream();

String[] array = {"a", "b", "c"};
Stream<String> stream = Arrays.stream(array);

Stream<String> stream = Stream.of("a", "b", "c");
```

#### Opérations Intermédiaires (lazy)

```java
// filter() - Filtrer
numbers.stream()
    .filter(n -> n % 2 == 0)  // Garde seulement les pairs

// map() - Transformer
names.stream()
    .map(String::toUpperCase)  // Transforme en majuscules

// sorted() - Trier
names.stream()
    .sorted()  // Tri naturel
    .sorted(Comparator.reverseOrder())  // Tri inverse

// distinct() - Éliminer doublons
numbers.stream().distinct()

// limit() et skip() - Pagination
numbers.stream()
    .skip(5)   // Saute les 5 premiers
    .limit(10) // Prend 10 éléments
```

#### Opérations Terminales (eager)

```java
// collect() - Collecter en collection
List<String> list = stream.collect(Collectors.toList());
Set<String> set = stream.collect(Collectors.toSet());

// forEach() - Itérer
stream.forEach(System.out::println);

// count() - Compter
long count = stream.count();

// anyMatch(), allMatch(), noneMatch()
boolean hasEven = numbers.stream().anyMatch(n -> n % 2 == 0);
boolean allPositive = numbers.stream().allMatch(n -> n > 0);

// findFirst(), findAny()
Optional<Integer> first = numbers.stream().findFirst();

// min(), max()
Optional<Integer> min = numbers.stream().min(Integer::compareTo);
Optional<Integer> max = numbers.stream().max(Integer::compareTo);

// reduce() - Réduire
int sum = numbers.stream().reduce(0, (a, b) -> a + b);
int sum = numbers.stream().reduce(0, Integer::sum); // équivalent
```

### Collectors Utiles

```java
// Vers List
.collect(Collectors.toList())

// Vers Set
.collect(Collectors.toSet())

// Joining (concaténer)
String result = names.stream()
    .collect(Collectors.joining(", "));
// Résultat: "Alice, Bob, Charlie"

// Grouping (grouper)
Map<String, List<Product>> byCategory = products.stream()
    .collect(Collectors.groupingBy(Product::getCategory));

// Counting (compter)
Map<String, Long> countByCategory = products.stream()
    .collect(Collectors.groupingBy(
        Product::getCategory,
        Collectors.counting()
    ));
```

### Exemple Complet

```java
List<Product> products = getProducts();

// Trouver les 5 produits Electronics les moins chers, disponibles
List<Product> topCheap = products.stream()
    .filter(p -> "Electronics".equals(p.getCategory()))
    .filter(Product::isAvailable)
    .sorted(Comparator.comparing(Product::getPrice))
    .limit(5)
    .collect(Collectors.toList());

// Prix moyen des produits disponibles
double avgPrice = products.stream()
    .filter(Product::isAvailable)
    .mapToDouble(Product::getPrice)
    .average()
    .orElse(0.0);

// Grouper par catégorie et compter
Map<String, Long> stats = products.stream()
    .collect(Collectors.groupingBy(
        Product::getCategory,
        Collectors.counting()
    ));
```

---

## 4. Optional

### Problème du `null`

**Avant (Java 7)** :
```java
public String getUserEmail(Long userId) {
    User user = findUserById(userId);
    if (user != null) {
        String email = user.getEmail();
        if (email != null) {
            return email.toUpperCase();
        }
    }
    return "NO_EMAIL";
}
```

**Avec Optional (Java 8+)** :
```java
public String getUserEmail(Long userId) {
    return findUserById(userId)
        .map(User::getEmail)
        .map(String::toUpperCase)
        .orElse("NO_EMAIL");
}
```

### Créer un Optional

```java
// Optional vide
Optional<String> empty = Optional.empty();

// Optional avec valeur non-null
Optional<String> opt = Optional.of("Hello");

// Optional qui accepte null
String value = null;
Optional<String> nullable = Optional.ofNullable(value);
```

### Opérations Essentielles

```java
Optional<String> opt = Optional.of("hello");

// Vérifier présence
if (opt.isPresent()) {
    String value = opt.get();
}

// Consumer si présent
opt.ifPresent(value -> System.out.println(value));
opt.ifPresent(System.out::println);

// Transformer
Optional<String> upper = opt.map(String::toUpperCase);
Optional<Integer> length = opt.map(String::length);

// Filtrer
Optional<String> filtered = opt.filter(s -> s.length() > 3);

// Valeur par défaut
String value = opt.orElse("default");
String value = opt.orElseGet(() -> "default");
String value = opt.orElseThrow(() -> new RuntimeException("Not found"));
```

### Cas d'Usage avec Repository

```java
public class UserService {
    private UserRepository repository;

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public String getUserName(Long userId) {
        return findById(userId)
            .map(User::getName)
            .orElse("Unknown");
    }

    public void printUser(Long userId) {
        findById(userId)
            .ifPresent(user -> System.out.println(user.getName()));
    }
}
```

---

## 5. Lombok

### Problème du Boilerplate

**Avant Lombok** :
```java
public class Product {
    private Long id;
    private String name;
    private Double price;

    // Constructeur vide
    public Product() {}

    // Constructeur complet
    public Product(Long id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(Double price) { this.price = price; }

    // equals, hashCode, toString...
    // 50+ lignes de code boilerplate
}
```

**Avec Lombok** :
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private Double price;
}
// 8 lignes au lieu de 50+
```

### Annotations Essentielles

```java
@Data  // Génère getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Constructeur sans paramètres
@AllArgsConstructor  // Constructeur avec tous les paramètres
@SuperBuilder  // Pattern Builder (préférer à @Builder)
public class Product {
    private Long id;
    private String name;
    private Double price;
}

// Utilisation du Builder
Product p = Product.builder()
    .id(1L)
    .name("Laptop")
    .price(999.99)
    .build();
```

**Autres annotations utiles** :

```java
@Slf4j  // Logger automatique
public class ProductService {
    public void process() {
        log.info("Processing...");
        log.error("Error: {}", message);
    }
}

@Value  // Classe immuable (tous les champs final)
public class ProductDto {
    Long id;
    String name;
    Double price;
}
```

---

## 6. Tests avec JUnit 6 + AssertJ

### Structure d'un Test : Pattern AAA

```java
@Test
void testFilterByCategory() {
    // Arrange (Préparer)
    ProductFilter filter = new ProductFilter();
    List<Product> products = createSampleProducts();

    // Act (Agir)
    List<Product> result = filter.filterByCategory(products, "Electronics");

    // Assert (Vérifier)
    assertThat(result).hasSize(3);
    assertThat(result).allMatch(p -> "Electronics".equals(p.getCategory()));
}
```

### Annotations JUnit

```java
class ProductFilterTest {

    private ProductFilter filter;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        // Exécuté AVANT chaque test
        filter = new ProductFilter();
        products = createSampleProducts();
    }

    @Test
    @DisplayName("Filtrer par catégorie retourne les bons produits")
    void testFilterByCategory() {
        // Test
    }

    @AfterEach
    void tearDown() {
        // Exécuté APRÈS chaque test (nettoyage)
    }
}
```

### AssertJ : Assertions Fluides

**Pourquoi AssertJ ?** Plus lisible et messages d'erreur plus clairs que JUnit natif

```java
import static org.assertj.core.api.Assertions.*;

@Test
void testAssertions() {
    // Égalité
    assertThat(actual).isEqualTo(expected);
    assertThat(actual).isNotEqualTo(other);

    // Nullité
    assertThat(object).isNull();
    assertThat(object).isNotNull();

    // Booléens
    assertThat(condition).isTrue();
    assertThat(condition).isFalse();

    // Nombres
    assertThat(42).isPositive();
    assertThat(10).isGreaterThan(5);
    assertThat(5.5).isBetween(5.0, 6.0);

    // Chaînes
    assertThat("Hello").startsWith("Hel");
    assertThat("Hello").contains("ll");
    assertThat("hello").isEqualToIgnoringCase("HELLO");

    // Collections
    assertThat(list).hasSize(3);
    assertThat(list).contains("Alice");
    assertThat(list).allMatch(s -> s.length() > 2);

    // Optional
    assertThat(optional).isPresent();
    assertThat(optional).isEmpty();

    // Exceptions
    assertThatIllegalArgumentException()
        .isThrownBy(() -> method())
        .withMessage("Error message");
}
```

### Exemple Complet

```java
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;
import java.util.*;

class ProductFilterTest {

    private ProductFilter filter;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        filter = new ProductFilter();
        products = Arrays.asList(
            Product.builder().name("Laptop").price(1200.0).category("Electronics").build(),
            Product.builder().name("iPhone").price(1000.0).category("Electronics").build(),
            Product.builder().name("T-Shirt").price(29.99).category("Clothing").build()
        );
    }

    @Test
    @DisplayName("Filter by category returns only matching products")
    void testFilterByCategory() {
        // Act
        List<Product> electronics = filter.filterByCategory(products, "Electronics");

        // Assert
        assertThat(electronics).hasSize(2);
        assertThat(electronics).allMatch(p -> "Electronics".equals(p.getCategory()));
        assertThat(electronics).extracting(Product::getName)
            .containsExactlyInAnyOrder("Laptop", "iPhone");
    }

    @Test
    @DisplayName("Find cheapest returns product with lowest price")
    void testFindCheapest() {
        // Act
        Optional<Product> cheapest = filter.findCheapest(products);

        // Assert
        assertThat(cheapest).isPresent();
        assertThat(cheapest.get().getPrice()).isEqualTo(29.99);
    }

    @Test
    @DisplayName("Find cheapest on empty list returns empty Optional")
    void testFindCheapestEmptyList() {
        // Act
        Optional<Product> cheapest = filter.findCheapest(Collections.emptyList());

        // Assert
        assertThat(cheapest).isEmpty();
    }
}
```

---

## 7. Structure Standard Maven

### Structure d'un Module Maven

```text
mon-module/
├── pom.xml                          # Configuration Maven
├── src/
│   ├── main/                        # Code de production
│   │   ├── java/                    # Sources Java
│   │   └── resources/               # Fichiers de configuration
│   └── test/                        # Code de test ⚠️ SÉPARÉ
│       ├── java/                    # Tests (même structure de packages)
│       └── resources/               # Ressources de test
└── target/                          # Généré par Maven
```

### Pourquoi `src/test` est séparé ?

**Maven sépare le code de production du code de test** pour 3 raisons principales :

1. **JAR final plus léger** : Les tests ne sont PAS inclus dans le déploiement
2. **Dépendances isolées** : JUnit/AssertJ (scope `test`) ne vont jamais en production
3. **Organisation claire** : Code facile à naviguer

**Exemple** :

```java
// ✅ Structure Maven standard
src/main/java/
  └── Product.java              // Code de production

src/test/java/
  └── ProductTest.java          // Tests séparés
```

**💡 Règle importante** : Toujours reproduire la même structure de packages dans `src/test` que dans `src/main`.

---

## 📚 Récapitulatif

### Génériques
```java
// Avant
List list = new ArrayList();
String str = (String) list.get(0); // Cast
// Après
List<String> list = new ArrayList<>();
String str = list.get(0); // Pas de cast
```

### Lambda
```java
// Avant
Collections.sort(list, new Comparator<String>() { ... });
// Après
Collections.sort(list, (a, b) -> a.compareTo(b));
```

### Streams
```java
// Avant
List<String> result = new ArrayList<>();
for (String s : list) {
    if (s.length() > 5) {
        result.add(s.toUpperCase());
    }
}
// Après
List<String> result = list.stream()
    .filter(s -> s.length() > 5)
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

### Optional
```java
// Avant
if (user != null && user.getEmail() != null) {
    return user.getEmail();
}
return "default";
// Après
return Optional.ofNullable(user)
    .map(User::getEmail)
    .orElse("default");
```

### Lombok
```java
// Avant : 50+ lignes de boilerplate
// Après : 5 lignes
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product { ... }
```

### Tests
```java
// Pattern AAA
@Test
void test() {
    // Arrange
    ProductFilter filter = new ProductFilter();

    // Act
    List<Product> result = filter.filterByCategory(products, "Electronics");

    // Assert
    assertThat(result).hasSize(3);
}
```

---

## 🎯 Pour Aller Plus Loin

Consultez `cours-complet.md` pour :
- Génériques avancés (bounds, wildcards)
- flatMap et opérations complexes
- Streams parallèles
- Tous les Collectors disponibles
- Tests paramétrés et tests avancés

---

## ⚡ Commandes Utiles

```bash
# Compiler
mvn compile

# Lancer les tests
mvn test

# Lancer un test spécifique
mvn test -Dtest=ProductFilterTest

# Voir les dépendances
mvn dependency:tree
```

---

**Bon apprentissage ! 🚀**

La clé : **pratiquer**, **expérimenter**, **tester**.
