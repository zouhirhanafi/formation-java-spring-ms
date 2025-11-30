# Module 1 : Fondamentaux Java Moderne & Tests Unitaires de Base

**Durée** : 6h (2h théorie + 4h pratique)

---

## Objectifs

À la fin de ce module, vous serez capable de :

- Utiliser les génériques Java de manière avancée
- Maîtriser les expressions lambda et les références de méthodes
- Exploiter la Streams API pour des traitements fonctionnels
- Gérer l'absence de valeur avec `Optional`
- Configurer Lombok pour réduire le code boilerplate
- **Écrire des tests unitaires avec JUnit 6 et AssertJ**

---

## 1. Les Génériques (Generics)

### 1.1 Rappels de base

Les génériques permettent de créer des classes, interfaces et méthodes avec des types paramétrés.

**Avantages** :

- Sécurité de type à la compilation
- Élimination des castings
- Code réutilisable

```java
// Sans génériques (ancien code)
List list = new ArrayList();
list.add("Hello");
String str = (String) list.get(0); // Cast nécessaire

// Avec génériques
List<String> list = new ArrayList<>();
list.add("Hello");
String str = list.get(0); // Pas de cast
```

### 1.2 Classes génériques

```java
public class Box<T> {
    private T content;
    
    public void set(T content) {
        this.content = content;
    }
    
    public T get() {
        return content;
    }
}

// Utilisation
Box<String> stringBox = new Box<>();
stringBox.set("Hello");
String value = stringBox.get();

Box<Integer> intBox = new Box<>();
intBox.set(42);
Integer number = intBox.get();
```

### 1.3 Méthodes génériques

```java
public class Utils {
    // Méthode générique statique
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.println(element);
        }
    }
    
    // Méthode générique avec type de retour
    public static <T> T getFirst(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }
}

// Utilisation
String[] names = {"Alice", "Bob", "Charlie"};
Utils.printArray(names);

List<Integer> numbers = Arrays.asList(1, 2, 3);
Integer first = Utils.getFirst(numbers);
```

### 1.4 Bornes (Bounds)

#### Upper Bound (extends)

```java
// Accepte Number et tous ses sous-types
public static <T extends Number> double sum(List<T> numbers) {
    return numbers.stream()
                  .mapToDouble(Number::doubleValue)
                  .sum();
}

// Utilisation
List<Integer> ints = Arrays.asList(1, 2, 3);
List<Double> doubles = Arrays.asList(1.5, 2.5, 3.5);
sum(ints);    // OK
sum(doubles); // OK
```

#### Wildcards (?)

```java
// Wildcard avec upper bound
public static void processNumbers(List<? extends Number> numbers) {
    for (Number num : numbers) {
        System.out.println(num.doubleValue());
    }
}

// Wildcard avec lower bound
public static void addIntegers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
}
```

### 1.5 Types multiples

```java
public class Pair<K, V> {
    private K key;
    private V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() { return key; }
    public V getValue() { return value; }
}

// Utilisation
Pair<String, Integer> pair = new Pair<>("Age", 25);
```

---

## 2. Lambda et Programmation Fonctionnelle

### 2.1 Interfaces fonctionnelles

Une interface fonctionnelle ne contient qu'**une seule méthode abstraite**.

```java
@FunctionalInterface
public interface Calculable {
    int calculate(int a, int b);
}
```

**Interfaces fonctionnelles standards** (package `java.util.function`) :

| Interface | Méthode | Description |
|-----------|---------|-------------|
| `Predicate<T>` | `boolean test(T t)` | Teste une condition |
| `Function<T,R>` | `R apply(T t)` | Transforme T en R |
| `Consumer<T>` | `void accept(T t)` | Consomme une valeur |
| `Supplier<T>` | `T get()` | Fournit une valeur |
| `BiFunction<T,U,R>` | `R apply(T t, U u)` | Fonction à 2 paramètres |

### 2.2 Expressions Lambda

**Syntaxe de base** :

```java
(paramètres) -> expression
(paramètres) -> { instructions; }
```

**Exemples** :

```java
// Sans lambda (classe anonyme)
Calculable addition = new Calculable() {
    @Override
    public int calculate(int a, int b) {
        return a + b;
    }
};

// Avec lambda
Calculable addition = (a, b) -> a + b;
Calculable multiplication = (a, b) -> a * b;

// Predicate
Predicate<String> isLong = s -> s.length() > 5;
Predicate<Integer> isEven = n -> n % 2 == 0;

// Function
Function<String, Integer> stringLength = s -> s.length();
Function<Integer, String> intToString = n -> "Nombre: " + n;

// Consumer
Consumer<String> printer = s -> System.out.println(s);
List<String> names = Arrays.asList("Alice", "Bob");
names.forEach(printer);

// Supplier
Supplier<Double> randomSupplier = () -> Math.random();
```

### 2.3 Références de méthodes

Les références de méthodes sont un raccourci pour les lambdas qui appellent une méthode existante.

**Types de références** :

```java
// 1. Référence à une méthode statique
Function<String, Integer> parse1 = s -> Integer.parseInt(s);
Function<String, Integer> parse2 = Integer::parseInt;

// 2. Référence à une méthode d'instance sur un objet
String prefix = "Hello, ";
Function<String, String> greeter1 = name -> prefix.concat(name);
Function<String, String> greeter2 = prefix::concat;

// 3. Référence à une méthode d'instance sur un type
Function<String, String> upper1 = s -> s.toUpperCase();
Function<String, String> upper2 = String::toUpperCase;

// 4. Référence à un constructeur
Supplier<List<String>> list1 = () -> new ArrayList<>();
Supplier<List<String>> list2 = ArrayList::new;

Function<Integer, List<String>> listWithSize = ArrayList::new;
```

**Exemples pratiques** :

```java
List<String> names = Arrays.asList("alice", "bob", "charlie");

// Avec lambda
names.stream()
     .map(s -> s.toUpperCase())
     .forEach(s -> System.out.println(s));

// Avec références de méthodes (plus concis)
names.stream()
     .map(String::toUpperCase)
     .forEach(System.out::println);
```

---

## 3. Streams API

### 3.1 Introduction

La Streams API permet de traiter des collections de données de manière **déclarative** et **fonctionnelle**.

**Caractéristiques** :

- Ne modifie pas la source de données
- Opérations lazy (évaluation paresseuse)
- Peut être parallélisé facilement

### 3.2 Création de Streams

```java
// À partir d'une collection
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream1 = list.stream();

// À partir d'un tableau
String[] array = {"a", "b", "c"};
Stream<String> stream2 = Arrays.stream(array);

// Avec Stream.of()
Stream<String> stream3 = Stream.of("a", "b", "c");

// Stream vide
Stream<String> empty = Stream.empty();

// Stream infini
Stream<Integer> infinite = Stream.iterate(0, n -> n + 1);
Stream<Double> randoms = Stream.generate(Math::random);
```

### 3.3 Opérations intermédiaires (lazy)

Ces opérations retournent un Stream et peuvent être chaînées.

#### filter() - Filtrer

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());
// Résultat: [2, 4, 6]
```

#### map() - Transformer

```java
List<String> names = Arrays.asList("alice", "bob", "charlie");

List<String> upperNames = names.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
// Résultat: ["ALICE", "BOB", "CHARLIE"]

List<Integer> lengths = names.stream()
    .map(String::length)
    .collect(Collectors.toList());
// Résultat: [5, 3, 7]
```

#### flatMap() - Aplatir

```java
List<List<Integer>> nestedList = Arrays.asList(
    Arrays.asList(1, 2),
    Arrays.asList(3, 4),
    Arrays.asList(5, 6)
);

List<Integer> flat = nestedList.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());
// Résultat: [1, 2, 3, 4, 5, 6]

// Exemple avec des chaînes
List<String> words = Arrays.asList("Hello", "World");
List<String> letters = words.stream()
    .flatMap(word -> Arrays.stream(word.split("")))
    .collect(Collectors.toList());
// Résultat: ["H", "e", "l", "l", "o", "W", "o", "r", "l", "d"]
```

#### distinct() - Éliminer les doublons

```java
List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4);
List<Integer> unique = numbers.stream()
    .distinct()
    .collect(Collectors.toList());
// Résultat: [1, 2, 3, 4]
```

#### sorted() - Trier

```java
List<String> names = Arrays.asList("Charlie", "Alice", "Bob");

List<String> sorted = names.stream()
    .sorted()
    .collect(Collectors.toList());
// Résultat: ["Alice", "Bob", "Charlie"]

// Tri personnalisé
List<String> sortedByLength = names.stream()
    .sorted(Comparator.comparingInt(String::length))
    .collect(Collectors.toList());
// Résultat: ["Bob", "Alice", "Charlie"]
```

#### limit() et skip()

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

List<Integer> first5 = numbers.stream()
    .limit(5)
    .collect(Collectors.toList());
// Résultat: [1, 2, 3, 4, 5]

List<Integer> skip3 = numbers.stream()
    .skip(3)
    .collect(Collectors.toList());
// Résultat: [4, 5, 6, 7, 8, 9, 10]

// Pagination
List<Integer> page2 = numbers.stream()
    .skip(5)
    .limit(5)
    .collect(Collectors.toList());
// Résultat: [6, 7, 8, 9, 10]
```

### 3.4 Opérations terminales (eager)

Ces opérations déclenchent le traitement du stream.

#### collect() - Collecter les résultats

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// Vers List
List<String> list = names.stream().collect(Collectors.toList());

// Vers Set
Set<String> set = names.stream().collect(Collectors.toSet());

// Vers Map
Map<String, Integer> map = names.stream()
    .collect(Collectors.toMap(
        name -> name,           // key
        name -> name.length()   // value
    ));

// Joining
String joined = names.stream()
    .collect(Collectors.joining(", "));
// Résultat: "Alice, Bob, Charlie"

// Grouping
Map<Integer, List<String>> grouped = names.stream()
    .collect(Collectors.groupingBy(String::length));
// Résultat: {5=[Alice], 3=[Bob], 7=[Charlie]}
```

#### forEach() - Itérer

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.stream().forEach(System.out::println);
```

#### count() - Compter

```java
long count = names.stream()
    .filter(name -> name.length() > 4)
    .count();
```

#### reduce() - Réduire

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Somme
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);
// ou
int sum = numbers.stream().reduce(0, Integer::sum);

// Produit
int product = numbers.stream()
    .reduce(1, (a, b) -> a * b);

// Trouver le maximum
Optional<Integer> max = numbers.stream()
    .reduce((a, b) -> a > b ? a : b);
// ou
Optional<Integer> max = numbers.stream().reduce(Integer::max);
```

#### Autres opérations terminales

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// anyMatch, allMatch, noneMatch
boolean hasEven = numbers.stream().anyMatch(n -> n % 2 == 0);
boolean allPositive = numbers.stream().allMatch(n -> n > 0);
boolean noNegative = numbers.stream().noneMatch(n -> n < 0);

// findFirst, findAny
Optional<Integer> first = numbers.stream().findFirst();
Optional<Integer> any = numbers.stream().findAny();

// min, max
Optional<Integer> min = numbers.stream().min(Integer::compareTo);
Optional<Integer> max = numbers.stream().max(Integer::compareTo);
```

### 3.5 Streams parallèles

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Stream séquentiel
long sum = numbers.stream()
    .mapToLong(Integer::longValue)
    .sum();

// Stream parallèle
long parallelSum = numbers.parallelStream()
    .mapToLong(Integer::longValue)
    .sum();
```

⚠️ **Attention** : N'utilisez les streams parallèles que si :

- Le volume de données est important
- Les opérations sont coûteuses
- L'ordre n'est pas important

---

## 4. Optional

### 4.1 Pourquoi Optional ?

`Optional` est un conteneur qui peut ou non contenir une valeur non-nulle. Il permet d'éviter les `NullPointerException`.

**Avant Optional** :

```java
public String getUserName(Long userId) {
    User user = findUserById(userId);
    if (user != null) {
        String name = user.getName();
        if (name != null) {
            return name.toUpperCase();
        }
    }
    return "UNKNOWN";
}
```

**Avec Optional** :

```java
public String getUserName(Long userId) {
    return findUserById(userId)
        .map(User::getName)
        .map(String::toUpperCase)
        .orElse("UNKNOWN");
}
```

### 4.2 Création d'Optional

```java
// Optional vide
Optional<String> empty = Optional.empty();

// Optional avec valeur non-nulle
Optional<String> opt = Optional.of("Hello");

// Optional qui peut être null
String value = null;
Optional<String> nullable = Optional.ofNullable(value);
```

### 4.3 Vérifier la présence

```java
Optional<String> opt = Optional.of("Hello");

// Vérifier si présent
if (opt.isPresent()) {
    String value = opt.get();
}

// Vérifier si vide
if (opt.isEmpty()) {
    // Java 11+
}
```

### 4.4 Consommer la valeur

```java
Optional<String> opt = Optional.of("Hello");

// ifPresent
opt.ifPresent(value -> System.out.println(value));
opt.ifPresent(System.out::println);

// ifPresentOrElse (Java 9+)
opt.ifPresentOrElse(
    value -> System.out.println("Found: " + value),
    () -> System.out.println("Not found")
);
```

### 4.5 Transformer la valeur

```java
Optional<String> opt = Optional.of("hello");

// map
Optional<String> upper = opt.map(String::toUpperCase);
Optional<Integer> length = opt.map(String::length);

// flatMap (quand la fonction retourne déjà un Optional)
Optional<String> result = opt.flatMap(s -> Optional.of(s.toUpperCase()));
```

### 4.6 Filtrer

```java
Optional<String> opt = Optional.of("hello");

Optional<String> filtered = opt.filter(s -> s.length() > 3);
// Résultat: Optional["hello"]

Optional<String> filtered2 = opt.filter(s -> s.length() > 10);
// Résultat: Optional.empty
```

### 4.7 Valeurs par défaut

```java
Optional<String> opt = Optional.empty();

// orElse - valeur par défaut
String value1 = opt.orElse("default");

// orElseGet - fournisseur de valeur (lazy)
String value2 = opt.orElseGet(() -> "default");

// orElseThrow - lever une exception
String value3 = opt.orElseThrow(() -> new RuntimeException("Not found"));

// or - retourner un autre Optional (Java 9+)
Optional<String> alternative = opt.or(() -> Optional.of("alternative"));
```

### 4.8 Exemple complet

```java
public class UserService {
    private Map<Long, User> users = new HashMap<>();
    
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
    
    public String getUserEmail(Long userId) {
        return findById(userId)
            .map(User::getEmail)
            .filter(email -> email.contains("@"))
            .orElse("no-email@example.com");
    }
    
    public void printUserInfo(Long userId) {
        findById(userId)
            .ifPresentOrElse(
                user -> System.out.println("User: " + user.getName()),
                () -> System.out.println("User not found")
            );
    }
    
    public List<String> getActiveUserEmails() {
        return users.values().stream()
            .filter(User::isActive)
            .map(User::getEmail)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }
}
```

---

## 5. Lombok

### 5.1 Introduction

Lombok réduit le code boilerplate en générant automatiquement du code à la compilation.

### 5.2 Annotations principales

```java
import lombok.*;

// Génère getters pour tous les champs
@Getter
// Génère setters pour tous les champs non-final
@Setter
// Génère toString()
@ToString
// Génère equals() et hashCode()
@EqualsAndHashCode
// Génère constructeur avec tous les champs
@AllArgsConstructor
// Génère constructeur sans paramètres
@NoArgsConstructor
// Génère constructeur avec les champs final/non-null
@RequiredArgsConstructor
public class Product {
    private Long id;
    private String name;
    private double price;
    private String category;
}

// Équivalent avec @Data (combine @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id;
    private String name;
    private double price;
    private String category;
}
```

### 5.3 @SuperBuilder

`@SuperBuilder` est une version améliorée de `@Builder` qui supporte l'héritage. Utilisez toujours `@SuperBuilder` au lieu de `@Builder` pour éviter les problèmes avec les hiérarchies de classes.

```java
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id;
    private String name;
    private double price;
    private String category;
    private boolean available;
}

// Utilisation
Product product = Product.builder()
    .id(1L)
    .name("Laptop")
    .price(999.99)
    .category("Electronics")
    .available(true)
    .build();
```

**Avantage avec l'héritage** :

```java
@Data
@SuperBuilder
@NoArgsConstructor
public class BaseEntity {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

@Data
@SuperBuilder
@NoArgsConstructor
public class Product extends BaseEntity {
    private String name;
    private double price;
    private String category;
}

// Utilisation - tous les champs sont accessibles dans le builder
Product product = Product.builder()
    .id(1L)                                    // du parent
    .createdAt(LocalDateTime.now())            // du parent
    .name("Laptop")                            // de Product
    .price(999.99)                             // de Product
    .build();
```

> **Note** : Utilisez `@Builder` uniquement si vous êtes sûr qu'il n'y aura jamais d'héritage. Dans le doute, préférez toujours `@SuperBuilder`.

### 5.4 Autres annotations utiles

```java
// @Slf4j - Logger automatique
@Slf4j
public class ProductService {
    public void process() {
        log.info("Processing product");
        log.error("Error occurred");
    }
}

// @NonNull - Vérification null
public class User {
    public void setName(@NonNull String name) {
        this.name = name;
    }
}

// @Value - Classe immuable
@Value
public class ProductDto {
    Long id;
    String name;
    double price;
}
```

---

## 6. Tests Unitaires de Base avec JUnit 6 et AssertJ

### 6.1 Introduction aux tests unitaires

Les **tests unitaires** sont essentiels pour garantir la qualité du code. Ils permettent de :

- ✅ Valider que le code fonctionne comme attendu
- ✅ Détecter les régressions lors des modifications
- ✅ Documenter le comportement attendu
- ✅ Faciliter le refactoring en toute confiance

**Principe de base** : Un test unitaire teste une **unité de code** (méthode ou classe) de manière **isolée**.

### 6.2 Structure d'un test : AAA Pattern

Tous les tests suivent le pattern **AAA** :

```java
@Test
void testMethodName() {
    // Arrange (Préparer) : Initialiser les données et dépendances
    ProductFilter filter = new ProductFilter();
    List<Product> products = createSampleProducts();
    
    // Act (Agir) : Exécuter la méthode à tester
    List<Product> result = filter.filterByCategory(products, "Electronics");
    
    // Assert (Vérifier) : Vérifier que le résultat est correct
    assertThat(result).hasSize(4);
    assertThat(result).allMatch(p -> Objects.equals(p.getCategory(), "Electronics"));
}
```

### 6.3 JUnit 6 : Annotations de base

```java
import org.junit.jupiter.api.*;

class MyTest {
    
    @BeforeAll
    static void setupAll() {
        // Exécuté une seule fois avant tous les tests de cette classe
        // Cas d'utilisation : Initialiser une connexion DB, démarrer un serveur,
        // charger des données de référence coûteuses à créer
        System.out.println("Setup before all tests");
    }
    
    @BeforeEach
    void setup() {
        // Exécuté avant chaque test
        // Cas d'utilisation : Créer des objets de test frais, réinitialiser l'état,
        // préparer des données de test spécifiques
        System.out.println("Setup before each test");
    }
    
    @Test
    void testSomething() {
        // Un test simple
        assertThat(2 + 2).isEqualTo(4);
    }
    
    @Test
    @DisplayName("Test avec un nom personnalisé")
    void testWithCustomName() {
        assertThat("Hello").isNotEmpty();
    }
    
    @Disabled("Test temporairement désactivé")
    @Test
    void testDisabled() {
        // Ce test ne sera pas exécuté
    }
    
    @AfterEach
    void tearDown() {
        // Exécuté après chaque test
        // Cas d'utilisation : Nettoyer les ressources, supprimer les fichiers temporaires,
        // réinitialiser les mocks, fermer les connexions
        System.out.println("Cleanup after each test");
    }
    
    @AfterAll
    static void tearDownAll() {
        // Exécuté une seule fois après tous les tests de cette classe
        // Cas d'utilisation : Fermer la connexion DB, arrêter le serveur,
        // libérer les ressources partagées
        System.out.println("Cleanup after all tests");
    }
}
```

### 6.4 AssertJ : Assertions fluides et expressives

**AssertJ** est une bibliothèque d'assertions qui offre une API fluide et lisible.

#### 6.4.1 Introduction à AssertJ

Nativement, JUnit permet de faire des assertions avec `org.junit.jupiter.api.Assertions` :

```java
assertEquals(expectedValue, actualValue);
```

AssertJ utilise `org.assertj.core.api.Assertions` avec une syntaxe plus fluide :

```java
assertThat(actualValue).isEqualTo(expectedValue);
```

**Avantages d'AssertJ** :

- ✅ Syntaxe plus lisible et naturelle
- ✅ Messages d'erreur plus clairs et détaillés
- ✅ Beaucoup plus d'assertions disponibles
- ✅ Support des vérifications complexes sur collections, exceptions, etc.

#### 6.4.2 Assertions de base

```java
import static org.assertj.core.api.Assertions.*;

@Test
void testBasicAssertions() {
    // Égalité
    assertThat(actualValue).isEqualTo(expectedValue);
    assertThat(actualValue).isNotEqualTo(otherValue);
    
    // Nullité
    assertThat(object).isNull();
    assertThat(object).isNotNull();
    
    // Booléens
    assertThat(condition).isTrue();
    assertThat(condition).isFalse();
    
    // Nombres
    assertThat(42).isPositive();
    assertThat(-5).isNegative();
    assertThat(10).isGreaterThan(5);
    assertThat(3).isLessThan(10);
    assertThat(5.5).isBetween(5.0, 6.0);
    
    // Chaînes de caractères
    assertThat("Hello").isNotEmpty();
    assertThat("Hello World").startsWith("Hello");
    assertThat("Hello World").endsWith("World");
    assertThat("Hello World").contains("lo Wo");
    assertThat("hello").isEqualToIgnoringCase("HELLO");
}
```

#### 6.4.3 Assertions sur les collections

```java
@Test
void testCollectionAssertions() {
    List<String> fruits = Arrays.asList("Apple", "Banana", "Cherry");
    
    // Taille
    assertThat(fruits).hasSize(3);
    assertThat(fruits).isNotEmpty();
    
    // Contenu
    assertThat(fruits).contains("Apple");
    assertThat(fruits).containsExactly("Apple", "Banana", "Cherry");
    assertThat(fruits).containsExactlyInAnyOrder("Cherry", "Apple", "Banana");
    assertThat(fruits).doesNotContain("Orange");
    
    // Premier et dernier
    assertThat(fruits).first().isEqualTo("Apple");
    assertThat(fruits).last().isEqualTo("Cherry");
    
    // Prédicat
    assertThat(fruits).allMatch(f -> f.length() > 4);
    assertThat(fruits).anyMatch(f -> f.startsWith("B"));
    assertThat(fruits).noneMatch(f -> f.isEmpty());
}
```

#### 6.4.4 Assertions sur les exceptions

Avec AssertJ, tester qu'une exception est lancée est très simple :

```java
@Test
void testExceptions() {
    // Vérifier qu'une exception est lancée
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> myMethod())
        .withMessage("This is the expected message");
    
    // Vérifier le message contient un texte
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> myMethod())
        .withMessageContaining("expected text");
    
    // Exceptions communes (raccourcis)
    assertThatIllegalArgumentException().isThrownBy(() -> myMethod());
    assertThatIllegalStateException().isThrownBy(() -> myMethod());
    assertThatNullPointerException().isThrownBy(() -> myMethod());
    assertThatIOException().isThrownBy(() -> myMethod());
}
```

**Exemple concret** :

```java
@Test
void testReduceStockThrowsException() {
    Product product = Product.builder()
        .stock(5)
        .build();
    
    assertThatIllegalStateException()
        .isThrownBy(() -> product.reduceStock(10))
        .withMessage("Insufficient stock");
}
```

#### 6.4.5 Vérifier le type d'un objet

```java
@Test
void testInstanceOf() {
    Object obj = "Hello";
    
    // Avec AssertJ : plus clair et message d'erreur précis
    assertThat(obj).isInstanceOf(String.class);
    
    // Si obj est null, AssertJ le dira clairement dans le message d'erreur
}
```

#### 6.4.6 Comparaison récursive d'objets

AssertJ permet de comparer deux objets champ par champ, même s'ils sont de types différents :

```java
@Test
void testRecursiveComparison() {
    Product product = Product.builder()
        .id(1L)
        .name("Laptop")
        .price(999.99)
        .build();
    
    ProductDto dto = new ProductDto(1L, "Laptop", 999.99);
    
    // Compare tous les champs communs récursivement
    assertThat(product).usingRecursiveComparison()
                       .isEqualTo(dto);
    
    // Ignorer certains champs
    assertThat(product).usingRecursiveComparison()
                       .ignoringFields("createdAt", "updatedAt")
                       .isEqualTo(dto);
}
```

### 6.5 Comparaison : JUnit vs AssertJ

| Assertion | JUnit 5 (natif) | AssertJ | Commentaire |
|-----------|----------------|---------|-------------|
| Objet non null | `assertNull(obj)` | `assertThat(obj).isNull()` | Comportement identique |
| Objets égaux | `assertEquals(expected, actual)` | `assertThat(actual).isEqualTo(expected)` | AssertJ plus lisible |
| Exception lancée | `assertThrows(NPE.class, () -> method())` | `assertThatNullPointerException().isThrownBy(() -> method())` | AssertJ plus expressif et flexible |
| Instance d'une classe | `assertTrue(obj instanceof Class)` | `assertThat(obj).isInstanceOf(Class.class)` | AssertJ offre une assertion dédiée |
| Liste non vide | `assertTrue(!list.isEmpty())` | `assertThat(list).isNotEmpty()` | AssertJ plus clair et message d'erreur meilleur |
| Comparaison d'objets | Nécessite `equals()` | `assertThat(objA).usingRecursiveComparison().isEqualTo(objB)` | AssertJ peut comparer sans `equals()` |

### 6.6 Exemple complet : Tester un filtre de produits

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
        products = createSampleProducts();
    }
    
    @Test
    @DisplayName("Filtrer par catégorie retourne uniquement les produits de cette catégorie")
    void testFilterByCategory() {
        // Act
        List<Product> electronics = filter.filterByCategory(products, "Electronics");
        
        // Assert
        assertThat(electronics).hasSize(4);
        assertThat(electronics).allMatch(p -> Objects.equals(p.getCategory(), "Electronics"));
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
    @DisplayName("Calculer le prix moyen")
    void testCalculateAveragePrice() {
        // Act
        double average = filter.calculateAveragePrice(products);
        
        // Assert
        assertThat(average).isGreaterThan(0);
        assertThat(average).isLessThan(1500); // Prix max dans les données
    }
    
    @Test
    @DisplayName("Grouper par catégorie")
    void testGroupByCategory() {
        // Act
        Map<String, List<Product>> grouped = filter.groupByCategory(products);
        
        // Assert
        assertThat(grouped).hasSize(3);
        assertThat(grouped).containsKeys("Electronics", "Clothing", "Furniture");
        assertThat(grouped.get("Electronics")).hasSize(4);
        assertThat(grouped.get("Clothing")).hasSize(2);
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
    
    private List<Product> createSampleProducts() {
        return Arrays.asList(
            Product.builder()
                .id(1L).name("Laptop Dell XPS 13").price(1299.99)
                .category("Electronics").available(true).stock(15)
                .build(),
            Product.builder()
                .id(2L).name("iPhone 15 Pro").price(1199.99)
                .category("Electronics").available(true).stock(25)
                .build(),
            Product.builder()
                .id(3L).name("T-Shirt Nike").price(29.99)
                .category("Clothing").available(true).stock(100)
                .build()
            // ... autres produits
        );
    }
}
```

### 6.7 Bonnes pratiques des tests unitaires

✅ **Un test = un concept** : Chaque test doit vérifier une seule chose  
✅ **Noms explicites** : Le nom du test doit décrire ce qui est testé  
✅ **Tests indépendants** : Chaque test doit pouvoir s'exécuter seul  
✅ **Données de test** : Utiliser des données réalistes et variées  
✅ **Tester les cas limites** : Listes vides, valeurs nulles, exceptions  
✅ **Tests rapides** : Les tests unitaires doivent s'exécuter rapidement  
✅ **Messages clairs** : AssertJ fournit automatiquement de bons messages  

### 6.8 Configuration Maven pour les tests

```xml
<dependencies>
    <!-- JUnit 6 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>6.0.1</version>
        <scope>test</scope>
    </dependency>
    
    <!-- AssertJ -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>3.27.6</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Exécuter les tests** :

```bash
# Compiler et exécuter tous les tests
mvn test

# Exécuter un test spécifique
mvn test -Dtest=ProductFilterTest

# Exécuter avec affichage détaillé
mvn test -Dtest=ProductFilterTest -X
```

---

## Récapitulatif

Dans ce module, vous avez appris :

✅ Les **génériques** pour créer du code type-safe et réutilisable  
✅ Les **lambda** et les **références de méthodes** pour un code plus concis  
✅ La **Streams API** pour traiter les collections de manière fonctionnelle  
✅ **Optional** pour gérer l'absence de valeur proprement  
✅ **Lombok** pour réduire le code boilerplate  
✅ **JUnit 6** pour écrire des tests unitaires  
✅ **AssertJ** pour des assertions fluides et expressives  

Ces concepts sont **essentiels** pour le développement moderne en Java et seront utilisés tout au long de la formation.

---

## Ressources complémentaires

- [Java Generics Tutorial](https://docs.oracle.com/javase/tutorial/java/generics/)
- [Java Streams API Guide](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)
- [Optional in Java](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)
- [Project Lombok](https://projectlombok.org/)
- [JUnit 6 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
