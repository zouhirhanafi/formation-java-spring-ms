# Module 1 : Fondamentaux Java Moderne (8h)

## 1.1 Les Génériques (2h)

### Objectifs

- Comprendre le concept de programmation générique
- Éviter les cast explicites et améliorer la sécurité du code
- Maîtriser les types paramétrés et les wildcard

### Concepts Clés

#### Qu'est-ce qu'un générique ?

Les génériques permettent de créer des classes, interfaces et méthodes qui fonctionnent avec différents types tout en maintenant la sécurité des types à la compilation.

```java
// Sans génériques (ancien style)
List list = new ArrayList();
list.add("Hello");
String s = (String) list.get(0); // Cast nécessaire

// Avec génériques
List<String> list = new ArrayList<>();
list.add("Hello");
String s = list.get(0); // Pas de cast nécessaire
```

#### Classes Génériques

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
intBox.set(123);
Integer number = intBox.get();
```

#### Méthodes Génériques

```java
public class Util {
    // Méthode générique statique
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.println(element);
        }
    }
    
    // Méthode générique avec type de retour
    public static <T, U> Pair<T, U> makePair(T first, U second) {
        return new Pair<>(first, second);
    }
}

// Utilisation
String[] names = {"Alice", "Bob", "Charlie"};
Util.printArray(names);

Pair<String, Integer> pair = Util.makePair("Age", 25);
```

#### Types Bornés (Bounded Types)

```java
// Borne supérieure (extends)
public class NumberBox<T extends Number> {
    private T number;
    
    public double getDoubleValue() {
        return number.doubleValue(); // Possible car T extends Number
    }
}

// Utilisation
NumberBox<Integer> intBox = new NumberBox<>(); // OK
NumberBox<Double> doubleBox = new NumberBox<>(); // OK
// NumberBox<String> stringBox = new NumberBox<>(); // ERREUR

// Méthode avec borne
public static <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) > 0 ? a : b;
}
```

#### Wildcards (?)

```java
// Wildcard illimité
public void printList(List<?> list) {
    for (Object obj : list) {
        System.out.println(obj);
    }
}

// Wildcard avec borne supérieure (? extends)
public double sumOfList(List<? extends Number> list) {
    double sum = 0.0;
    for (Number num : list) {
        sum += num.doubleValue();
    }
    return sum;
}

// Wildcard avec borne inférieure (? super)
public void addNumbers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
    list.add(3);
}
```

#### PECS (Producer Extends, Consumer Super)

```java
// Producer (on lit depuis la collection) : extends
public void copyElements(List<? extends Number> source, 
                         List<? super Number> destination) {
    for (Number num : source) {  // Producer : on lit
        destination.add(num);     // Consumer : on écrit
    }
}
```

### Exercices Pratiques

#### Exercice 1 : Classe Générique Box

Créer une classe `Box<T>` générique avec :

- Un attribut privé `content` de type `T`
- Un constructeur qui initialise le contenu
- Les méthodes `set(T content)` et `get()`
- Une méthode `isEmpty()` qui retourne `true` si le contenu est `null`
- Tester avec différents types (String, Integer, Double)

#### Exercice 2 : Classe Paire Générique

Créer une classe `Pair<K, V>` avec :

- Deux attributs : `key` de type `K` et `value` de type `V`
- Constructeur pour initialiser la clé et la valeur
- Getters pour `key` et `value`
- Méthode `swap()` qui retourne un nouveau `Pair<V, K>`
- Méthode statique générique `of(K key, V value)` pour créer une paire
- Méthode `toString()` pour afficher la paire

**Exemple d'utilisation :**

```java
Pair<String, Integer> pair = Pair.of("Age", 25);
System.out.println(pair); // (Age, 25)
Pair<Integer, String> swapped = pair.swap();
System.out.println(swapped); // (25, Age)
```

#### Exercice 3 : Méthode Générique de Recherche

Implémenter une méthode générique `findFirst` qui trouve le premier élément dans une liste selon un critère.

**Exemple d'utilisation :**

```java
// Interface pour le critère de recherche
interface Checker<T> {
    boolean test(T element);
}

List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

// Trouver le premier nombre pair
Integer firstEven = SearchUtil.findFirst(numbers, new Checker<Integer>() {
    @Override
    public boolean test(Integer n) {
        return n % 2 == 0;
    }
});
System.out.println(firstEven); // 2

// Trouver le premier nombre > 4
Integer firstBig = SearchUtil.findFirst(numbers, new Checker<Integer>() {
    @Override
    public boolean test(Integer n) {
        return n > 4;
    }
});
System.out.println(firstBig); // 5
```

#### Exercice 4 : Repository Générique

Créer une classe `Repository<T>` générique qui simule un stockage en mémoire :

- Un attribut `Map<Long, T> storage` pour stocker les entités
- Un attribut `Long nextId` pour générer les IDs
- Méthode `save(T entity)` : retourne l'ID généré (Long)
- Méthode `findById(Long id)` : retourne l'entité ou `null` si non trouvé
- Méthode `findAll()` : retourne `List<T>` de toutes les entités
- Méthode `delete(Long id)` : retourne `true` si supprimé, `false` sinon
- Méthode `count()` : retourne le nombre d'entités

**Exemple d'utilisation :**

```java
class Student {
    private String name;
    private int age;
    
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public String toString() {
        return name + " (" + age + " ans)";
    }
}

Repository<Student> repo = new Repository<>();
Long id1 = repo.save(new Student("Alice", 20));
Long id2 = repo.save(new Student("Bob", 22));

System.out.println("Total: " + repo.count()); // 2
System.out.println(repo.findById(id1)); // Alice (20 ans)
System.out.println(repo.findAll()); // [Alice (20 ans), Bob (22 ans)]
```

---

## 1.2 Collections Java (2h)

### Objectifs

- Maîtriser les principales interfaces et implémentations des collections
- Choisir la bonne structure de données selon le besoin
- Comprendre les complexités algorithmiques

### Hiérarchie des Collections

```
Collection (interface)
├── List (interface)
│   ├── ArrayList
│   ├── LinkedList
│   └── Vector (legacy)
├── Set (interface)
│   ├── HashSet
│   ├── LinkedHashSet
│   └── TreeSet (SortedSet)
└── Queue (interface)
    ├── LinkedList
    ├── PriorityQueue
    └── Deque (interface)
        └── ArrayDeque

Map (interface)
├── HashMap
├── LinkedHashMap
├── TreeMap (SortedMap)
└── Hashtable (legacy)
```

### List : Listes Ordonnées

#### ArrayList

```java
// ArrayList : tableau dynamique, accès rapide par index
List<String> arrayList = new ArrayList<>();
arrayList.add("Java");
arrayList.add("Spring");
arrayList.add(1, "Boot"); // Insertion à l'index 1

// Accès
String item = arrayList.get(0); // O(1)

// Complexités
// Accès : O(1)
// Insertion/Suppression en fin : O(1) amortie
// Insertion/Suppression au milieu : O(n)
```

#### LinkedList

```java
// LinkedList : liste doublement chaînée
List<String> linkedList = new LinkedList<>();
linkedList.add("First");
linkedList.add("Second");
linkedList.addFirst("New First"); // Spécifique à LinkedList

// Complexités
// Accès : O(n)
// Insertion/Suppression en début/fin : O(1)
// Insertion/Suppression au milieu : O(n) (recherche) + O(1) (insertion)
```

#### Quand utiliser quoi ?

- **ArrayList** : Accès fréquent par index, peu d'insertions/suppressions
- **LinkedList** : Insertions/suppressions fréquentes, peu d'accès par index

### Set : Collections Sans Doublons

#### HashSet

```java
// HashSet : basé sur une table de hachage, pas d'ordre
Set<String> hashSet = new HashSet<>();
hashSet.add("Java");
hashSet.add("Spring");
hashSet.add("Java"); // Ignoré, pas de doublons

// Complexités : O(1) en moyenne pour add, remove, contains
```

#### LinkedHashSet

```java
// LinkedHashSet : préserve l'ordre d'insertion
Set<String> linkedHashSet = new LinkedHashSet<>();
linkedHashSet.add("First");
linkedHashSet.add("Second");
// Itération dans l'ordre d'insertion
```

#### TreeSet

```java
// TreeSet : ensemble trié (ordre naturel ou Comparator)
Set<Integer> treeSet = new TreeSet<>();
treeSet.add(5);
treeSet.add(1);
treeSet.add(3);
// Itération : 1, 3, 5 (ordre naturel)

// Complexités : O(log n) pour add, remove, contains
```

### Map : Associations Clé-Valeur

#### HashMap

```java
Map<String, Integer> map = new HashMap<>();
map.put("Alice", 25);
map.put("Bob", 30);
map.put("Charlie", 35);

// Accès
Integer age = map.get("Alice"); // 25
boolean exists = map.containsKey("Bob");

// Parcours
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + " : " + entry.getValue());
}

// Java 8+
map.forEach((key, value) -> 
    System.out.println(key + " : " + value)
);

// Complexités : O(1) en moyenne
```

#### LinkedHashMap

```java
// Préserve l'ordre d'insertion ou d'accès
Map<String, Integer> linkedMap = new LinkedHashMap<>();
```

#### TreeMap

```java
// Map triée par clé
Map<String, Integer> treeMap = new TreeMap<>();
treeMap.put("Charlie", 35);
treeMap.put("Alice", 25);
treeMap.put("Bob", 30);
// Itération : Alice, Bob, Charlie (ordre alphabétique)

// Complexités : O(log n)
```

### Queue et Deque

```java
// Queue FIFO
Queue<String> queue = new LinkedList<>();
queue.offer("First");
queue.offer("Second");
String head = queue.poll(); // "First"

// PriorityQueue (tas)
Queue<Integer> priorityQueue = new PriorityQueue<>();
priorityQueue.offer(5);
priorityQueue.offer(1);
priorityQueue.offer(3);
System.out.println(priorityQueue.poll()); // 1 (plus petit élément)

// Deque (double-ended queue)
Deque<String> deque = new ArrayDeque<>();
deque.addFirst("First");
deque.addLast("Last");
deque.removeFirst();
```

### Méthodes Utiles des Collections

```java
// Collections utility class
List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5);

// Tri
Collections.sort(numbers);
Collections.sort(numbers, Collections.reverseOrder());

// Recherche (liste triée)
int index = Collections.binarySearch(numbers, 4);

// Min/Max
Integer min = Collections.min(numbers);
Integer max = Collections.max(numbers);

// Shuffle
Collections.shuffle(numbers);

// Collections immuables
List<String> immutableList = List.of("a", "b", "c"); // Java 9+
Set<String> immutableSet = Set.of("a", "b", "c");
Map<String, Integer> immutableMap = Map.of("a", 1, "b", 2);
```

### Exercices Pratiques

#### Exercice 1 : Gestion d'Étudiants

Créer un système de gestion d'étudiants avec :

- `Map<String, Student>` pour stocker les étudiants (clé = ID)
- Méthodes : ajouter, supprimer, rechercher par ID
- Méthode pour obtenir la liste triée par nom

#### Exercice 2 : Supprimer les Doublons

Écrire une méthode qui prend une `List<Integer>` et retourne une nouvelle liste sans doublons, en préservant l'ordre.

#### Exercice 3 : Intersection de Sets

Implémenter une méthode qui trouve l'intersection de deux `Set<String>`.

---

## 1.3 Expressions Lambda et Programmation Fonctionnelle (2h)

### Objectifs

- Comprendre les interfaces fonctionnelles
- Maîtriser la syntaxe des expressions lambda
- Utiliser les références de méthodes
- Connaître les interfaces fonctionnelles standards

### Interfaces Fonctionnelles

Une interface fonctionnelle est une interface avec **une seule méthode abstraite**.

```java
@FunctionalInterface
public interface Calculator {
    int calculate(int a, int b);
}

// Utilisation traditionnelle (classe anonyme)
Calculator addition = new Calculator() {
    @Override
    public int calculate(int a, int b) {
        return a + b;
    }
};

// Avec lambda
Calculator addition = (a, b) -> a + b;
Calculator multiplication = (a, b) -> a * b;

int result = addition.calculate(5, 3); // 8
```

### Syntaxe des Lambdas

```java
// Syntaxe complète
(Type param1, Type param2) -> {
    // Corps de la méthode
    return result;
}

// Syntaxe simplifiée (types inférés)
(param1, param2) -> result

// Un seul paramètre (parenthèses optionnelles)
param -> param * 2

// Sans paramètre
() -> System.out.println("Hello")

// Exemples
Comparator<String> byLength = (s1, s2) -> s1.length() - s2.length();
Runnable task = () -> System.out.println("Running");
Function<String, Integer> length = s -> s.length();
```

### Interfaces Fonctionnelles Standard (java.util.function)

#### Predicate<T> : Test conditionnel

```java
// boolean test(T t)
Predicate<String> isEmpty = s -> s.isEmpty();
Predicate<Integer> isPositive = n -> n > 0;
Predicate<String> startsWithA = s -> s.startsWith("A");

// Utilisation
boolean result = isEmpty.test(""); // true

// Combinaison
Predicate<String> longAndStartsWithA = 
    s -> s.length() > 5 && s.startsWith("A");

// Ou avec méthodes par défaut
Predicate<String> combined = startsWithA.and(s -> s.length() > 5);
Predicate<String> either = startsWithA.or(s -> s.length() < 3);
Predicate<String> notStartsWithA = startsWithA.negate();
```

#### Function<T, R> : Transformation

```java
// R apply(T t)
Function<String, Integer> length = s -> s.length();
Function<Integer, Integer> square = n -> n * n;
Function<String, String> toUpper = s -> s.toUpperCase();

Integer len = length.apply("Hello"); // 5

// Composition
Function<String, Integer> lengthSquared = 
    length.andThen(square); // apply puis square
    
Function<Integer, String> squareThenString = 
    square.andThen(Object::toString);
```

#### Consumer<T> : Action sans retour

```java
// void accept(T t)
Consumer<String> printer = s -> System.out.println(s);
Consumer<List<String>> listPrinter = 
    list -> list.forEach(System.out::println);

printer.accept("Hello"); // Affiche : Hello

// Chaînage
Consumer<String> printUpper = 
    printer.andThen(s -> System.out.println(s.toUpperCase()));
```

#### Supplier<T> : Fournisseur de valeur

```java
// T get()
Supplier<String> helloSupplier = () -> "Hello";
Supplier<Double> randomSupplier = () -> Math.random();
Supplier<LocalDateTime> nowSupplier = () -> LocalDateTime.now();

String greeting = helloSupplier.get(); // "Hello"
```

#### BiFunction<T, U, R> : Fonction à 2 paramètres

```java
// R apply(T t, U u)
BiFunction<String, String, String> concat = 
    (s1, s2) -> s1 + s2;
    
BiFunction<Integer, Integer, Integer> add = 
    (a, b) -> a + b;

String result = concat.apply("Hello", "World"); // "HelloWorld"
```

#### UnaryOperator<T> et BinaryOperator<T>

```java
// T apply(T t) - extends Function<T, T>
UnaryOperator<Integer> square = n -> n * n;

// T apply(T t1, T t2) - extends BiFunction<T, T, T>
BinaryOperator<Integer> add = (a, b) -> a + b;
BinaryOperator<String> concat = (s1, s2) -> s1 + s2;

// Avec comparator
BinaryOperator<Integer> max = BinaryOperator.maxBy(Integer::compare);
Integer result = max.apply(5, 10); // 10
```

### Références de Méthodes

```java
// Référence à une méthode statique
Function<String, Integer> parser = Integer::parseInt;
// Équivalent : s -> Integer.parseInt(s)

// Référence à une méthode d'instance
String str = "Hello";
Supplier<Integer> lengthGetter = str::length;
// Équivalent : () -> str.length()

// Référence à une méthode d'instance arbitraire
Function<String, Integer> lengthFunc = String::length;
// Équivalent : s -> s.length()

// Référence à un constructeur
Supplier<List<String>> listSupplier = ArrayList::new;
// Équivalent : () -> new ArrayList<>()

Function<Integer, List<String>> listCreator = ArrayList::new;
// Équivalent : size -> new ArrayList<>(size)
```

### Exemples Pratiques

```java
// Tri avec lambda
List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
names.sort((s1, s2) -> s1.compareTo(s2));
// Ou avec référence de méthode
names.sort(String::compareTo);

// Filtrage
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
Predicate<Integer> isEven = n -> n % 2 == 0;
numbers.removeIf(isEven.negate());

// ForEach avec Consumer
List<String> words = Arrays.asList("Java", "Spring", "Boot");
words.forEach(System.out::println);
words.forEach(word -> System.out.println(word.toUpperCase()));

// Transformation avec replaceAll
List<String> names = new ArrayList<>(Arrays.asList("alice", "bob"));
names.replaceAll(String::toUpperCase);
```

### Exercices Pratiques

#### Exercice 1 : Validateur de Données

Créer différents `Predicate<String>` pour valider :

- Email (contient @)
- Mot de passe fort (longueur > 8, contient chiffre et majuscule)
- Combiner les prédicats

#### Exercice 2 : Pipeline de Transformation

Créer une chaîne de `Function` pour transformer une chaîne :

1. Trim
2. ToLowerCase
3. Remplacer les espaces par des underscores

#### Exercice 3 : Calculatrice Fonctionnelle

Créer une interface `Operation` et implémenter différentes opérations mathématiques avec des lambdas.

---

## 1.4 Streams API et Optional (2h)

### Objectifs

- Maîtriser l'API Stream pour le traitement de collections
- Comprendre les opérations intermédiaires et terminales
- Utiliser Optional pour éviter NullPointerException

### Introduction aux Streams

Un Stream est une séquence d'éléments qui supporte des opérations de traitement de données de manière déclarative.

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");

// Style impératif
List<String> result = new ArrayList<>();
for (String name : names) {
    if (name.length() > 3) {
        result.add(name.toUpperCase());
    }
}

// Style déclaratif avec Stream
List<String> result = names.stream()
    .filter(name -> name.length() > 3)
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

### Création de Streams

```java
// Depuis une collection
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream1 = list.stream();

// Depuis un tableau
String[] array = {"a", "b", "c"};
Stream<String> stream2 = Arrays.stream(array);

// Stream.of
Stream<String> stream3 = Stream.of("a", "b", "c");

// Stream vide
Stream<String> empty = Stream.empty();

// Stream infini
Stream<Integer> infinite = Stream.iterate(0, n -> n + 1);
Stream<Double> random = Stream.generate(Math::random);

// Stream depuis un fichier
Stream<String> lines = Files.lines(Paths.get("file.txt"));
```

### Opérations Intermédiaires (Lazy)

Les opérations intermédiaires retournent un Stream et ne sont exécutées que lorsqu'une opération terminale est appelée.

#### filter : Filtrage

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());
// [2, 4, 6, 8, 10]
```

#### map : Transformation

```java
List<String> names = Arrays.asList("alice", "bob", "charlie");

List<String> upperNames = names.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
// ["ALICE", "BOB", "CHARLIE"]

List<Integer> lengths = names.stream()
    .map(String::length)
    .collect(Collectors.toList());
// [5, 3, 7]
```

#### flatMap : Aplatissement

```java
List<List<Integer>> nestedList = Arrays.asList(
    Arrays.asList(1, 2),
    Arrays.asList(3, 4),
    Arrays.asList(5, 6)
);

List<Integer> flat = nestedList.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());
// [1, 2, 3, 4, 5, 6]

// Exemple avec mots
List<String> sentences = Arrays.asList("Hello World", "Java Stream");
List<String> words = sentences.stream()
    .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
    .collect(Collectors.toList());
// ["Hello", "World", "Java", "Stream"]
```

#### distinct, sorted, limit, skip

```java
List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 5, 5);

// distinct : supprime les doublons
List<Integer> unique = numbers.stream()
    .distinct()
    .collect(Collectors.toList());
// [1, 2, 3, 4, 5]

// sorted : tri
List<Integer> sorted = numbers.stream()
    .sorted()
    .collect(Collectors.toList());

List<String> reverseSort = Arrays.asList("c", "a", "b").stream()
    .sorted(Comparator.reverseOrder())
    .collect(Collectors.toList());

// limit : limite le nombre d'éléments
List<Integer> first3 = numbers.stream()
    .limit(3)
    .collect(Collectors.toList());

// skip : ignore les n premiers éléments
List<Integer> after3 = numbers.stream()
    .skip(3)
    .collect(Collectors.toList());
```

#### peek : Effet de bord (debug)

```java
List<String> result = names.stream()
    .filter(n -> n.length() > 3)
    .peek(n -> System.out.println("Filtered: " + n))
    .map(String::toUpperCase)
    .peek(n -> System.out.println("Mapped: " + n))
    .collect(Collectors.toList());
```

### Opérations Terminales (Eager)

#### collect : Collecte en collection

```java
List<String> list = stream.collect(Collectors.toList());
Set<String> set = stream.collect(Collectors.toSet());
Map<String, Integer> map = stream
    .collect(Collectors.toMap(
        s -> s,                    // clé
        String::length             // valeur
    ));

// Joining
String joined = Arrays.asList("a", "b", "c").stream()
    .collect(Collectors.joining(", "));
// "a, b, c"

// Grouping
Map<Integer, List<String>> byLength = names.stream()
    .collect(Collectors.groupingBy(String::length));

// Partitioning
Map<Boolean, List<Integer>> partitioned = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));
```

#### forEach : Itération

```java
names.stream()
    .forEach(System.out::println);
```

#### count, min, max

```java
long count = numbers.stream().count();

Optional<Integer> min = numbers.stream()
    .min(Integer::compareTo);

Optional<Integer> max = numbers.stream()
    .max(Integer::compareTo);
```

#### reduce : Réduction

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Somme
Optional<Integer> sum = numbers.stream()
    .reduce((a, b) -> a + b);
// Ou
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);

// Produit
int product = numbers.stream()
    .reduce(1, (a, b) -> a * b);

// Concaténation
String concat = Arrays.asList("a", "b", "c").stream()
    .reduce("", (a, b) -> a + b);
```

#### anyMatch, allMatch, noneMatch

```java
boolean hasEven = numbers.stream()
    .anyMatch(n -> n % 2 == 0);

boolean allPositive = numbers.stream()
    .allMatch(n -> n > 0);

boolean noNegative = numbers.stream()
    .noneMatch(n -> n < 0);
```

#### findFirst, findAny

```java
Optional<String> first = names.stream()
    .filter(n -> n.startsWith("A"))
    .findFirst();

Optional<String> any = names.stream()
    .filter(n -> n.startsWith("A"))
    .findAny(); // Utile en parallèle
```

### Streams Parallèles

```java
// Stream séquentiel
long count = numbers.stream()
    .filter(n -> n % 2 == 0)
    .count();

// Stream parallèle
long count = numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .count();

// Conversion
Stream<String> parallel = names.stream().parallel();
Stream<String> sequential = parallel.sequential();
```

### Optional

`Optional<T>` est un conteneur pour une valeur qui peut être présente ou absente.

#### Création

```java
// Avec une valeur
Optional<String> optional = Optional.of("Hello");

// Peut être null
Optional<String> nullable = Optional.ofNullable(getString());

// Vide
Optional<String> empty = Optional.empty();
```

#### Vérification et Accès

```java
// isPresent / isEmpty
if (optional.isPresent()) {
    String value = optional.get();
}

if (optional.isEmpty()) {
    // Java 11+
}

// orElse : valeur par défaut
String value = optional.orElse("default");

// orElseGet : valeur calculée si absent
String value = optional.orElseGet(() -> computeDefault());

// orElseThrow : exception si absent
String value = optional.orElseThrow();
String value = optional.orElseThrow(
    () -> new IllegalStateException("Value not present")
);
```

#### Transformations

```java
Optional<String> name = Optional.of("alice");

// map
Optional<Integer> length = name.map(String::length);
Optional<String> upper = name.map(String::toUpperCase);

// flatMap (quand la fonction retourne déjà un Optional)
Optional<String> result = name
    .flatMap(n -> findByName(n)); // findByName retourne Optional<String>

// filter
Optional<String> longName = name
    .filter(n -> n.length() > 5);
```

#### ifPresent / ifPresentOrElse

```java
optional.ifPresent(value -> 
    System.out.println("Value: " + value)
);

optional.ifPresentOrElse(
    value -> System.out.println("Value: " + value),
    () -> System.out.println("No value")
);
```

#### Exemple Complet

```java
public class UserService {
    public Optional<User> findById(Long id) {
        // Recherche dans la base de données
        User user = repository.findById(id);
        return Optional.ofNullable(user);
    }
    
    public String getUserName(Long id) {
        return findById(id)
            .map(User::getName)
            .orElse("Unknown");
    }
    
    public void processUser(Long id) {
        findById(id)
            .filter(user -> user.isActive())
            .map(User::getEmail)
            .ifPresent(email -> sendEmail(email));
    }
}
```

### Exemples Pratiques Complets

#### Exemple 1 : Traitement de Produits

```java
class Product {
    private String name;
    private double price;
    private String category;
    // constructeur, getters, setters
}

List<Product> products = getProducts();

// Prix moyen des produits électroniques
double avgPrice = products.stream()
    .filter(p -> p.getCategory().equals("Electronics"))
    .mapToDouble(Product::getPrice)
    .average()
    .orElse(0.0);

// Grouper par catégorie
Map<String, List<Product>> byCategory = products.stream()
    .collect(Collectors.groupingBy(Product::getCategory));

// Top 3 des produits les plus chers
List<Product> top3 = products.stream()
    .sorted(Comparator.comparing(Product::getPrice).reversed())
    .limit(3)
    .collect(Collectors.toList());
```

#### Exemple 2 : Traitement de Texte

```java
String text = "Hello world hello java world";

Map<String, Long> wordCount = Arrays.stream(text.split(" "))
    .map(String::toLowerCase)
    .collect(Collectors.groupingBy(
        word -> word,
        Collectors.counting()
    ));
// {hello=2, world=2, java=1}
```

### Exercices Pratiques

#### Exercice 1 : Statistiques sur Employés

Créer une classe `Employee` avec nom, âge, salaire, département.
Implémenter :

- Salaire moyen par département
- Liste des employés de plus de 30 ans triés par salaire
- Département avec le salaire total le plus élevé

#### Exercice 2 : Chaîne de Traitement

À partir d'une liste de transactions, calculer :

- Total des transactions d'un type donné
- Transactions groupées par devise
- Les 5 transactions les plus importantes

#### Exercice 3 : Manipulation de Texte

À partir d'un fichier texte :

- Compter les mots uniques
- Trouver les 10 mots les plus fréquents
- Calculer la longueur moyenne des lignes

---

## Ressources et Bonnes Pratiques

### Bonnes Pratiques

1. **Génériques**
   - Toujours utiliser les génériques pour les collections
   - Éviter les raw types
   - Utiliser les wildcards de manière appropriée (PECS)

2. **Collections**
   - Choisir la bonne structure de données
   - Utiliser les collections immuables quand possible
   - Initialiser avec une capacité appropriée si connue

3. **Lambdas**
   - Garder les lambdas courtes et lisibles
   - Extraire en méthodes si la logique est complexe
   - Utiliser les références de méthodes quand approprié

4. **Streams**
   - Ne pas réutiliser un stream (ils sont à usage unique)
   - Attention aux streams parallèles (pas toujours plus rapides)
   - Préférer les méthodes spécialisées (mapToInt, mapToDouble)
   - Utiliser Optional au lieu de null

### Documentation

- [Java Generics Tutorial](https://docs.oracle.com/javase/tutorial/java/generics/)
- [Collections Framework](https://docs.oracle.com/javase/tutorial/collections/)
- [Lambda Expressions](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html)
- [Stream API](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)

### Projet Pratique du Module

**Mini-Projet : Système de Gestion de Bibliothèque**

Créer un système avec :

- Classes : `Book`, `Author`, `Member`, `Loan`
- Utiliser les génériques pour un repository générique
- Implémenter des recherches avec Streams :
  - Livres par auteur
  - Livres disponibles
  - Membres avec emprunts en retard
  - Statistiques (livres les plus empruntés, etc.)
- Utiliser Optional pour les recherches
- Utiliser les interfaces fonctionnelles pour les filtres personnalisés
