# Formation Pratique : Spring Boot & Microservices par le Projet (62h)

## Concept

Formation basée sur un **projet fil rouge** : développement progressif d'une plateforme e-commerce en microservices. Chaque module combine théorie courte et mise en pratique immédiate sur le projet.

---

## 📖 Documentation

La formation propose **deux niveaux de documentation** :

- **Version Essentielle** (`*-essentiel.md`) : Documentation condensée pour démarrer rapidement, utilisée pendant les cours
  - `cours-essentiel.md` : Concepts clés et exemples pratiques
  - `presentation-fonctionnelle-essentiel.md` : Vue d'ensemble du projet
  - `conception-essentiel.md` : Modèle de données simplifié

- **Version Complète** (`*.md`) : Documentation détaillée pour approfondir et servir de référence
  - `cours-complet.md` : Tous les concepts avec explications approfondies
  - `presentation-fonctionnelle.md` : Flux détaillés et diagrammes complets
  - `conception.md` : Schémas SQL, contraintes et optimisations

---

## Projet Fil Rouge : Plateforme E-Commerce

**Architecture finale** :

- Service Catalogue (produits)
- Service Commandes
- Service Utilisateurs
- Service Paiement
- API Gateway
- Configuration centralisée

---

## Module 1 : Fondamentaux Java Moderne & Setup Projet (8h)

### Théorie (3h)

- Rappels génériques et collections
- Lambda et Streams API (concepts nouveaux pour Java 7)
- Optional et programmation fonctionnelle
- Introduction aux tests unitaires avec JUnit 6 et AssertJ

### Projet (5h)

**Exercice pratique** : Initialisation des projets Maven

- Créer la structure du projet avec Maven
- Configurer Lombok
- Implémenter des utilitaires avec Streams et Lambda
- **Livrable** : Structure projet + classe `ProductFilter` utilisant Streams

---

## Module 2 : Premier Microservice - Service Catalogue (6h)

### Théorie (2h)

- Spring Boot basics
- JPA et H2 pour démarrer rapidement
- Endpoints REST

### Projet (4h)

**Exercice pratique** : Développer le service Catalogue (Product)

- Modèle `Product` avec JPA
- Repository et Service layer
- CRUD REST endpoints
- Tests avec base H2 en mémoire
- **Livrable** : Service Catalogue fonctionnel avec 5 endpoints REST

> **💡 Bonus** : L'exercice Category (2h supplémentaires) est disponible pour les apprentis souhaitant approfondir

---

## Module 3 : Documentation & Gestion d'Erreurs (4h)

### Théorie (1h)

- Swagger/OpenAPI
- Exception handling et GlobalExceptionHandler

### Projet (3h)

**Exercice pratique** : Améliorer le service Catalogue

- Intégrer Swagger UI
- Créer DTOs avec MapStruct
- Implémenter gestion d'erreurs globale
- **Livrable** : Documentation Swagger complète + gestion erreurs

---

## Module 4 : Base de Données & Migrations (5h)

### Théorie (1h30)

- PostgreSQL vs H2
- Liquibase pour migrations
- Auditing (createdAt, updatedAt)

### Projet (3h30)

**Exercice pratique** : Migrer vers PostgreSQL

- Configurer PostgreSQL avec Docker
- Créer changelogs Liquibase
- Ajouter auditing sur les entités
- **Livrable** : Service Catalogue avec PostgreSQL + historique migrations

---

## Module 5 : Deuxième Microservice - Service Commandes (7h)

### Théorie (1h30)

- Architecture microservices
- Communication inter-services avec OpenFeign
- Pagination et tri

### Projet (5h30)

**Exercice pratique** : Développer le service Commandes

- Modèles `Order` et `OrderItem`
- Communication avec Service Catalogue via OpenFeign
- Endpoints avec pagination
- Validation des commandes
- **Livrable** : Service Commandes communiquant avec Service Catalogue

---

## Module 6 : Logging & Monitoring (3h)

### Théorie (45min)

- Logback configuration
- Niveaux de logs et bonnes pratiques

### Projet (2h15)

**Exercice pratique** : Ajouter logging aux services

- Configurer Logback avec rotation de fichiers
- Ajouter logs métier pertinents
- Créer appenders par environnement
- **Livrable** : Configuration logging complète sur les 2 services

---

## Module 7 : Tests Automatisés (6h)

### Théorie (1h30)

- JUnit 6 et Mockito
- Tests d'intégration avec @SpringBootTest
- TestContainers pour PostgreSQL

### Projet (4h30)

**Exercice pratique** : Tests sur Service Commandes

- Tests unitaires du service layer (Mockito)
- Tests d'intégration des repositories
- Tests des contrôleurs avec MockMvc
- Tests avec TestContainers
- **Livrable** : Couverture de tests > 70% sur Service Commandes

---

## Module 8 : Sécurité avec Spring Security (6h)

### Théorie (2h)

- Spring Security basics
- JWT et authentification stateless
- Autorisation par rôles

### Projet (4h)

**Exercice pratique** : Sécuriser les services

- Créer Service Utilisateurs (authentification)
- Implémenter JWT
- Sécuriser endpoints Catalogue et Commandes
- Gérer rôles USER et ADMIN
- **Livrable** : Services sécurisés avec JWT

---

## Module 9 : Conteneurisation Docker (5h)

### Théorie (1h)

- Docker basics
- Dockerfile multi-stage
- Docker Compose

### Projet (4h)

**Exercice pratique** : Dockeriser l'application

- Créer Dockerfile pour chaque service
- docker-compose.yml complet (services + PostgreSQL + Redis)
- Gestion des variables d'environnement
- **Livrable** : Application complète démarrable avec `docker-compose up`

---

## Module 10 : Configuration Centralisée & Profiles (4h)

### Théorie (1h)

- Spring Cloud Config Server
- Profiles (dev, prod)
- Externalisation configuration

### Projet (3h)

**Exercice pratique** : Centraliser la configuration

- Créer Config Server
- Externaliser configs dans Git repository
- Configurer profiles par environnement
- **Livrable** : Configuration centralisée pour tous les services

---

## Module 11 : Cache & Résilience (4h)

### Théorie (1h)

- Redis pour cache distribué
- Circuit Breaker pattern
- Resilience4j

### Projet (3h)

**Exercice pratique** : Optimiser la performance

- Intégrer Redis pour cache produits
- Implémenter Circuit Breaker sur OpenFeign
- Ajouter retry et timeout
- **Livrable** : Services résilients avec cache

---

## Module 12 : Intégration & Déploiement (3h)

### Théorie (45min)

- Keycloak basics (optionnel)
- API Gateway
- Bonnes pratiques microservices

### Projet (2h15)

**Exercice pratique finale** : Finalisation

- Tests end-to-end
- Documentation architecture
- Présentation du projet
- **Livrable** : Application microservices complète et documentée

---

## Livrables Finaux

À la fin de la formation, chaque apprenti dispose de :

1. **Application fonctionnelle** : Plateforme e-commerce microservices complète
2. **Code source** : Repository Git avec historique propre
3. **Documentation** :
   - README avec instructions de déploiement
   - Documentation API (Swagger)
   - Schéma d'architecture
4. **Compétences pratiques** :
   - 3 microservices opérationnels
   - Tests automatisés
   - Déploiement Docker
   - Sécurité JWT
   - Communication inter-services

---

## Méthode

- **20% Théorie** : Concepts clés expliqués au début de chaque module
- **80% Pratique** : Développement progressif sur le projet fil rouge
- **Validation continue** : Chaque module produit un livrable fonctionnel
- **Autonomie progressive** : Les exercices deviennent plus ouverts au fil de la formation
- **Pair programming** : Encouragé pendant les exercices pratiques

---

## Prérequis

- Java 17+
- Connaissance de base Spring Boot
- Git
- IDE (IntelliJ IDEA ou VS Code)
- Docker Desktop

---

## Évaluation

- **Contrôle continu** : Livrables de chaque module (40%)
- **Projet final** : Application complète et fonctionnelle (40%)
- **Présentation** : Démonstration et explication des choix techniques (20%)
