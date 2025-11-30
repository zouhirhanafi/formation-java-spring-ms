# Présentation Fonctionnelle - Plateforme E-Commerce (Essentiel)

> 💡 **Version simplifiée** pour démarrer rapidement. Voir `presentation-fonctionnelle.md` pour les détails complets.

---

## 🎯 Vue d'ensemble

**Projet** : Plateforme e-commerce en microservices
**Objectif** : Construire une architecture moderne, scalable et résiliente

---

## 📦 Les 4 Services Métier

### 1. Service Catalogue
**Responsabilité** : Gestion des produits

**Fonctionnalités** :
- ✅ CRUD produits
- ✅ Recherche et filtres (catégorie, prix, disponibilité)
- ✅ Gestion du stock

**Endpoints** :
```
GET    /api/products              # Liste avec filtres
GET    /api/products/{id}         # Détail produit
POST   /api/products              # Créer (ADMIN)
PUT    /api/products/{id}         # Modifier (ADMIN)
DELETE /api/products/{id}         # Supprimer (ADMIN)
```

---

### 2. Service Commandes
**Responsabilité** : Gestion du cycle de vie des commandes

**Fonctionnalités** :
- ✅ Créer une commande
- ✅ Valider la disponibilité des produits (appel au Service Catalogue)
- ✅ Calculer le montant total
- ✅ Suivre le statut
- ✅ Historique des commandes

**Workflow de statut** :
```
CREATED → VALIDATED → PAYMENT_PENDING → PAID → SHIPPED → DELIVERED
   ↓
CANCELLED
```

**Endpoints** :
```
POST   /api/orders                # Créer commande
GET    /api/orders/{id}           # Détail
GET    /api/orders/user/{userId}  # Commandes d'un utilisateur
PUT    /api/orders/{id}/status    # Mettre à jour statut (ADMIN)
DELETE /api/orders/{id}           # Annuler
```

---

### 3. Service Utilisateurs
**Responsabilité** : Authentification et gestion des utilisateurs

**Fonctionnalités** :
- ✅ Inscription / Connexion
- ✅ Authentification JWT
- ✅ Gestion des rôles (USER, ADMIN)
- ✅ Gestion du profil

**Endpoints** :
```
POST   /api/auth/register         # Inscription
POST   /api/auth/login            # Connexion → JWT
GET    /api/users/me              # Profil
PUT    /api/users/me              # Modifier profil
```

---

### 4. Service Paiement
**Responsabilité** : Gestion des transactions

**Fonctionnalités** :
- ✅ Initier un paiement
- ✅ Valider une transaction
- ✅ Gérer les remboursements (ADMIN)

**Endpoints** :
```
POST   /api/payments/initiate     # Initier paiement
GET    /api/payments/{id}         # Statut paiement
POST   /api/payments/{id}/refund  # Remboursement (ADMIN)
```

---

## 🔐 Sécurité

### Permissions par Rôle

| Action | Anonyme | USER | ADMIN |
|--------|---------|------|-------|
| Consulter catalogue | ✅ | ✅ | ✅ |
| Créer compte | ✅ | ✅ | ✅ |
| Passer commande | ❌ | ✅ | ✅ |
| Voir ses commandes | ❌ | ✅ (siennes) | ✅ (toutes) |
| Gérer produits | ❌ | ❌ | ✅ |
| Gérer utilisateurs | ❌ | ❌ | ✅ |

### Mécanisme JWT

```
1. Client → POST /auth/login → Service Utilisateurs
2. Service Utilisateurs → Valide credentials → Génère JWT
3. Client reçoit JWT
4. Client → Requêtes avec header "Authorization: Bearer {JWT}"
5. Gateway valide JWT avant de router
```

---

## 🔄 Flux Principal : Passer une Commande

```
1. Client consulte le catalogue (GET /api/products)
2. Client crée un compte (POST /api/auth/register) → reçoit JWT
3. Client passe commande (POST /api/orders + JWT)
   ├─ Service Commandes vérifie le stock (appel Service Catalogue)
   ├─ Service Commandes crée la commande
   ├─ Service Commandes initie le paiement (appel Service Paiement)
   └─ Service Paiement valide → Commande statut = PAID
4. Client reçoit confirmation
```

---

## 🚀 Infrastructure

### Configuration Centralisée
- **Config Server** : Gère les configs de tous les services
- **Profiles** : dev, test, prod

### Cache & Performance
- **Redis** : Cache des produits fréquemment consultés
- **Circuit Breaker** : Protection des appels inter-services
- **Retry** : Nouvelle tentative automatique en cas d'échec

### Déploiement
- **Docker** : Chaque service = 1 conteneur
- **Docker Compose** : Orchestration locale (dev)

---

## 📊 Progression du Projet (12 Modules)

| Module | Livrable |
|--------|----------|
| **1** | Setup + Fondamentaux Java |
| **2** | Service Catalogue (CRUD + H2) |
| **3** | Documentation Swagger + Gestion erreurs |
| **4** | Migration PostgreSQL + Liquibase |
| **5** | Service Commandes + OpenFeign |
| **6** | Logging (Logback) |
| **7** | Tests automatisés (JUnit + Mockito + TestContainers) |
| **8** | Sécurité JWT |
| **9** | Dockerisation |
| **10** | Config centralisée |
| **11** | Cache Redis + Resilience4j |
| **12** | API Gateway + Intégration finale |

---

## 🎯 Livrables Finaux

À l'issue de la formation :

**Application Complète** :
- ✅ 3-4 microservices opérationnels
- ✅ Communication inter-services (OpenFeign)
- ✅ Authentification JWT
- ✅ Cache distribué (Redis)
- ✅ Tests automatisés (couverture > 70%)

**Documentation** :
- 📄 API Swagger pour chaque service
- 📐 Schémas d'architecture
- 📋 Instructions de déploiement

**Infrastructure** :
- 🐳 Images Docker
- 🔧 Docker Compose prêt à l'emploi
- ⚙️ Configuration multi-environnements

---

## 💡 Cas d'Usage Couverts

### En tant que Client
1. Découvrir les produits (sans compte)
2. Créer un compte
3. Passer une commande
4. Payer en ligne
5. Suivre mes commandes

### En tant qu'Admin
1. Gérer le catalogue (CRUD produits)
2. Gérer les stocks
3. Voir toutes les commandes
4. Modifier le statut des commandes
5. Gérer les utilisateurs et rôles

---

**🔗 Pour plus de détails** : Consultez `presentation-fonctionnelle.md`
