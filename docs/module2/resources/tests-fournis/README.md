# Tests Fournis pour Validation - Module 2

> **⚠️ IMPORTANT** : Ces fichiers de tests sont fournis pour **VALIDER** votre implémentation.
> **NE PAS** essayer de les comprendre en détail pour l'instant.

## Contenu

Ce dossier contient les tests de validation pour le Module 2 :

- **ProductServiceTest.java** - Tests du service Product (Mockito)
- **ProductControllerTest.java** - Tests du controller Product (MockMvc)
- **CategoryServiceTest.java** - Tests du service Category (Mockito)
- **CategoryControllerTest.java** - Tests du controller Category (MockMvc)

## Comment utiliser ces tests ?

### 1. Copier les fichiers dans votre projet

Copiez chaque fichier dans le bon répertoire de votre projet :

```bash
# ProductServiceTest.java
cp ProductServiceTest.java <votre-projet>/src/test/java/ma/ensaf/ecommerce/catalogue/service/

# ProductControllerTest.java
cp ProductControllerTest.java <votre-projet>/src/test/java/ma/ensaf/ecommerce/catalogue/controller/

# CategoryServiceTest.java
cp CategoryServiceTest.java <votre-projet>/src/test/java/ma/ensaf/ecommerce/catalogue/service/

# CategoryControllerTest.java
cp CategoryControllerTest.java <votre-projet>/src/test/java/ma/ensaf/ecommerce/catalogue/controller/
```

### 2. Lancer les tests

```bash
# Tous les tests
mvn test

# Tests d'un fichier spécifique
mvn test -Dtest=ProductServiceTest
mvn test -Dtest=ProductControllerTest
mvn test -Dtest=CategoryServiceTest
mvn test -Dtest=CategoryControllerTest
```

### 3. Interpréter les résultats

- ✅ **Tous les tests passent** → Votre code est correct !
- ❌ **Des tests échouent** → Corrigez votre implémentation

**Exemple d'erreur** :

```
[ERROR] shouldCreateProduct - Expected: IllegalArgumentException when SKU exists
[ERROR] Actual: No exception thrown
```

→ Votre méthode `create()` ne vérifie pas si le SKU existe déjà.

## Technologies utilisées

Ces tests utilisent des frameworks qui seront détaillés au **Module 7** :

| Framework | Usage | Module |
|-----------|-------|--------|
| **Mockito** | Mock des dépendances (repository) | Module 7 |
| **MockMvc** | Simulation requêtes HTTP | Module 7 |
| **AssertJ** | Assertions fluides | Module 7 |

**Pour l'instant** : NE PAS essayer de comprendre le code de ces tests.
**Module 7** : Vous apprendrez à écrire ces tests vous-même.

## Que vérifient ces tests ?

### ProductServiceTest & CategoryServiceTest

- ✅ Création avec validation (SKU/code unique, prix positif)
- ✅ Lecture (findAll, findById)
- ✅ Mise à jour
- ✅ Suppression
- ✅ Gestion des erreurs (entité non trouvée, validation échouée)

### ProductControllerTest & CategoryControllerTest

- ✅ Endpoints REST (GET, POST, PUT, DELETE)
- ✅ Codes HTTP appropriés (200, 201, 204, 404)
- ✅ Sérialisation JSON
- ✅ Gestion des erreurs (404 Not Found)

## Aide au débogage

### Tests Service échouent

**Problème** : `NullPointerException` dans les tests

**Solution** : Vérifiez que votre Service :
- A l'annotation `@Service`
- Utilise `@RequiredArgsConstructor` avec `final` sur le repository
- Implémente toutes les méthodes (`findAll`, `findById`, `create`, `update`, `deleteById`)

### Tests Controller échouent

**Problème** : `404 Not Found` alors que le test attend `200 OK`

**Solution** : Vérifiez que votre Controller :
- A l'annotation `@RestController`
- A `@RequestMapping("/api/v1/products")` ou `@RequestMapping("/api/v1/categories")`
- Les méthodes ont les bonnes annotations (`@GetMapping`, `@PostMapping`, etc.)

### Tests de validation échouent

**Problème** : `Expected IllegalArgumentException but nothing was thrown`

**Solution** : Vérifiez vos validations :
```java
// ProductService.create() doit vérifier :
if (productRepository.existsBySku(product.getSku())) {
    throw new IllegalArgumentException("Product with SKU ... already exists");
}
if (product.getPrice() == null || product.getPrice() <= 0) {
    throw new IllegalArgumentException("Price must be positive");
}
```

## Questions fréquentes

**Q : Dois-je comprendre le code de ces tests ?**
R : Non ! Utilisez-les juste pour valider. Module 7 expliquera tout.

**Q : Puis-je modifier ces tests ?**
R : Non ! Ils définissent le contrat que votre code doit respecter.

**Q : Et les tests Repository ?**
R : Vous devez les écrire vous-même (voir exercices.md) avec `@DataJpaTest`.

**Q : Pourquoi ces tests utilisent Mockito ?**
R : Pour tester le Service/Controller de manière **isolée**, sans vraie base de données ni serveur HTTP.

---

**Bon courage ! 🚀**
