# Module 1 : Guide de Dépannage (Troubleshooting)

**Objectif** : Résoudre rapidement les erreurs courantes rencontrées lors des exercices Module 1

---

## 🔧 Problèmes de Compilation

### 1. "Variable used in lambda should be final or effectively final"

**Erreur** :
```java
String category = "Electronics";
category = "Clothing"; // Modification
products.stream()
    .filter(p -> p.getCategory().equals(category)) // ❌ Erreur
    .collect(Collectors.toList());
```

**Cause** : Les variables utilisées dans une lambda doivent être "effectively final" (non modifiées après initialisation).

**Solution** :
```java
// Option 1 : Ne pas modifier la variable
final String category = "Electronics";
products.stream()
    .filter(p -> p.getCategory().equals(category)) // ✅ OK
    .collect(Collectors.toList());

// Option 2 : Utiliser une variable différente
String tempCategory = "Electronics";
String category = tempCategory;
products.stream()
    .filter(p -> p.getCategory().equals(category)) // ✅ OK
    .collect(Collectors.toList());
```

---

### 2. "Incompatible types: Object cannot be converted to Product"

**Erreur** :
```java
List list = new ArrayList(); // ❌ Pas de type générique
list.add(Product.builder().build());
Product p = list.get(0); // ❌ Erreur de compilation
```

**Cause** : Absence de génériques → la liste est de type `List<Object>`.

**Solution** :
```java
List<Product> list = new ArrayList<>(); // ✅ Avec générique
list.add(Product.builder().build());
Product p = list.get(0); // ✅ OK
```

---

### 3. "Cannot resolve method 'builder()' in 'Product'"

**Erreur** :
```java
Product p = Product.builder() // ❌ builder() introuvable
    .name("Laptop")
    .build();
```

**Cause** : Lombok n'est pas configuré correctement ou annotation manquante.

**Solutions** :

#### a) Vérifier l'annotation
```java
@Data
@SuperBuilder  // ⚠️ Doit être @SuperBuilder (pas @Builder)
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    // ...
}
```

#### b) Vérifier la dépendance Lombok dans pom.xml
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.42</version>
    <scope>provided</scope>
</dependency>
```

#### c) Vérifier le plugin maven-compiler
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.13.0</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

#### d) Recompiler
```bash
mvn clean compile
```

---

### 4. "Package org.junit.jupiter.api does not exist"

**Erreur** :
```java
import org.junit.jupiter.api.*; // ❌ Package introuvable
```

**Cause** : Dépendance JUnit manquante ou mauvaise version.

**Solution** : Vérifier dans `pom.xml`
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>6.0.1</version>
    <scope>test</scope>
</dependency>
```

Puis recharger Maven :
```bash
mvn clean test
```

---

### 5. "Cannot resolve symbol 'assertThat'"

**Erreur** :
```java
assertThat(result).hasSize(3); // ❌ assertThat introuvable
```

**Cause** : Import manquant ou dépendance AssertJ absente.

**Solution** :

#### a) Ajouter l'import
```java
import static org.assertj.core.api.Assertions.*; // ✅ Import statique
```

#### b) Vérifier la dépendance
```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.27.6</version>
    <scope>test</scope>
</dependency>
```

---

## 🧪 Problèmes de Tests

### 6. "No tests found for given includes"

**Erreur** :
```bash
mvn test -Dtest=ProductTest
[WARNING] No tests found for given includes
```

**Causes possibles** :

#### a) Classe de test mal placée
```text
❌ Mauvais :
src/main/java/ProductTest.java

✅ Correct :
src/test/java/ma/ensaf/ecommerce/common/model/ProductTest.java
```

#### b) Nom de classe invalide
```java
// ❌ Mauvais noms
public class ProductTestCase { ... }
public class TestProduct { ... }

// ✅ Bons noms (JUnit détecte automatiquement)
public class ProductTest { ... }
```

#### c) Méthode test sans @Test
```java
// ❌ Sans annotation
void testSomething() { ... }

// ✅ Avec annotation
@Test
void testSomething() { ... }
```

---

### 7. "Test ignored/skipped"

**Erreur** :
```bash
Tests run: 5, Failures: 0, Errors: 0, Skipped: 1
```

**Cause** : Annotation `@Disabled` ou condition non remplie.

**Solution** :
```java
// Retirer @Disabled
// @Disabled("Work in progress") // ❌ Retire cette ligne
@Test
void testSomething() {
    // Test
}
```

---

### 8. "NullPointerException in test"

**Erreur** :
```java
@Test
void testFilterByCategory() {
    List<Product> products = null; // ❌ Liste null
    List<Product> result = filter.filterByCategory(products, "Electronics");
    // NullPointerException !
}
```

**Solution** : Toujours initialiser les données de test
```java
@BeforeEach
void setUp() {
    products = createSampleProducts(); // ✅ Initialisation
}

@Test
void testFilterByCategory() {
    List<Product> result = filter.filterByCategory(products, "Electronics");
    assertThat(result).isNotNull();
}
```

---

## 🔄 Problèmes de Streams API

### 9. "Stream has already been operated upon or closed"

**Erreur** :
```java
Stream<Product> stream = products.stream();
long count = stream.count();
List<Product> list = stream.collect(Collectors.toList()); // ❌ Stream déjà utilisé
```

**Cause** : Un stream ne peut être utilisé qu'une seule fois.

**Solution** : Créer un nouveau stream
```java
long count = products.stream().count();
List<Product> list = products.stream().collect(Collectors.toList()); // ✅ Nouveau stream
```

---

### 10. "IllegalStateException: stream has been closed"

**Erreur** :
```java
Stream<Product> stream = products.stream();
stream.close();
stream.forEach(System.out::println); // ❌ Stream fermé
```

**Cause** : Tentative d'utilisation après fermeture.

**Solution** : Ne pas fermer explicitement les streams créés depuis des collections
```java
// ✅ Pas besoin de fermer
products.stream()
    .filter(p -> p.getPrice() > 100)
    .forEach(System.out::println);
```

---

### 11. "No value present" (Optional)

**Erreur** :
```java
Optional<Product> opt = products.stream()
    .filter(p -> p.getPrice() > 10000) // Aucun produit > 10000
    .findFirst();

Product p = opt.get(); // ❌ NoSuchElementException
```

**Cause** : Appel de `.get()` sur un Optional vide.

**Solutions** :
```java
// Option 1 : Vérifier avec isPresent()
if (opt.isPresent()) {
    Product p = opt.get(); // ✅ Sûr
}

// Option 2 : Utiliser orElse()
Product p = opt.orElse(null); // ✅ Retourne null si vide

// Option 3 : Utiliser orElseThrow()
Product p = opt.orElseThrow(() -> new RuntimeException("Not found")); // ✅ Exception custom

// Option 4 : Utiliser ifPresent()
opt.ifPresent(p -> System.out.println(p.getName())); // ✅ N'exécute que si présent
```

---

## 📦 Problèmes Maven

### 12. "Failed to execute goal on project: Could not resolve dependencies"

**Erreur** :
```bash
[ERROR] Failed to execute goal on project common:
Could not resolve dependencies for project ma.ensaf.ecommerce:common:jar:1.0.0-SNAPSHOT
```

**Solutions** :

#### a) Vérifier la connexion Internet
```bash
ping repo.maven.apache.org
```

#### b) Nettoyer le cache Maven
```bash
mvn dependency:purge-local-repository
mvn clean install
```

#### c) Forcer la mise à jour
```bash
mvn clean install -U
```

---

### 13. "Parent POM not found"

**Erreur** :
```bash
[ERROR] Non-resolvable parent POM: Could not find artifact ma.ensaf.ecommerce:ecommerce-platform:pom:1.0.0-SNAPSHOT
```

**Cause** : Le POM parent n'a pas été installé dans le repository local.

**Solution** : Compiler le parent d'abord
```bash
# À la racine du projet
mvn clean install

# Puis dans le module
cd common
mvn clean test
```

---

### 14. "Tests are skipped"

**Erreur** :
```bash
mvn package
[INFO] Tests are skipped.
```

**Cause** : Configuration ou flag `-DskipTests`.

**Solution** :
```bash
# Exécuter les tests
mvn test

# Package sans skip
mvn package

# Si les tests sont skipés dans le POM, vérifier :
<properties>
    <maven.test.skip>false</maven.test.skip> <!-- ✅ Doit être false -->
</properties>
```

---

## 🐛 Problèmes Logiques Courants

### 15. "Test échoue : liste vide au lieu de produits filtrés"

**Erreur** :
```java
@Test
void testFilterByCategory() {
    List<Product> result = filter.filterByCategory(products, "Electronics");
    assertThat(result).hasSize(3); // ❌ Expected: 3, Actual: 0
}
```

**Causes possibles** :

#### a) Comparaison inversée
```java
// ❌ Mauvais
.filter(p -> p.getCategory().equals(category))
// Si p.getCategory() est null → NullPointerException

// ✅ Bon (null-safe)
.filter(p -> category.equals(p.getCategory()))
```

#### b) Catégorie avec casse différente
```java
// Données de test
Product.builder().category("electronics").build() // Minuscule

// Filtre
filterByCategory(products, "Electronics") // Majuscule → 0 résultat

// Solution : ignorer la casse
.filter(p -> category.equalsIgnoreCase(p.getCategory()))
```

---

### 16. "AveragePrice retourne 0 alors qu'il y a des produits"

**Erreur** :
```java
double avg = products.stream()
    .mapToDouble(p -> p.getPrice()) // Si price est null → 0.0
    .average()
    .orElse(0.0);
// Résultat incorrect si des prix sont null
```

**Solution** : Filtrer les nulls
```java
double avg = products.stream()
    .filter(p -> p.getPrice() != null) // ✅ Filtrer les nulls
    .mapToDouble(Product::getPrice)
    .average()
    .orElse(0.0);
```

---

## ⚙️ Problèmes IDE (IntelliJ IDEA)

### 17. "Cannot resolve symbol" malgré une compilation Maven réussie

**Solution** :
1. `File` → `Invalidate Caches...` → `Invalidate and Restart`
2. Réimporter le projet Maven : `Maven` (panneau droit) → `Reload All Maven Projects`
3. Vérifier que le SDK est bien Java 21 : `File` → `Project Structure` → `Project SDK`

---

### 18. "Lombok annotations not working in IDE"

**Solution** :
1. Installer le plugin Lombok : `File` → `Settings` → `Plugins` → Rechercher "Lombok" → Installer
2. Activer l'annotation processing : `File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors` → Cocher `Enable annotation processing`
3. Redémarrer IntelliJ

---

## 📋 Checklist de Débogage

Avant de demander de l'aide, vérifiez :

- [ ] Le code compile avec `mvn clean compile`
- [ ] Les imports sont corrects
- [ ] Les annotations Lombok sont présentes (@Data, @SuperBuilder, etc.)
- [ ] Les tests sont dans `src/test/java` (pas `src/main/java`)
- [ ] Le package des tests correspond au package du code
- [ ] La méthode test a l'annotation `@Test`
- [ ] Les données de test sont initialisées dans `@BeforeEach`
- [ ] Pas de variables null non gérées
- [ ] Les streams ne sont pas réutilisés
- [ ] Les Optional sont manipulés avec `orElse()`, `ifPresent()`, etc.

---

## 🆘 Obtenir de l'Aide

Si le problème persiste :

1. **Consulter le cours** : `docs/module1/cours-essentiel.md`
2. **Consulter la correction** : `docs/module1/correction.md`
3. **Documentation Java** :
   - Streams : https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html
   - Optional : https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html
4. **Lombok** : https://projectlombok.org/features/
5. **AssertJ** : https://assertj.github.io/doc/

---

**Bon courage ! 💪**

La plupart des erreurs sont communes et faciles à résoudre une fois identifiées.
