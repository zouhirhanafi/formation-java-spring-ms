# 🛠️ Installation de l'Environnement de Développement

Ce guide couvre l'installation des outils nécessaires pour le développement Java Spring Boot sur Windows, Linux et macOS.

---

## 🔧 Installation de Git Bash (Windows)

**Git Bash** est le premier outil à installer sur Windows pour avoir un environnement Unix-like.

### Windows

Télécharger et installer depuis : <https://git-scm.com/download/win>

Git Bash inclut :

- Git
- Bash shell
- Outils Unix essentiels (curl, ssh, etc.)

---

## ☕ Installation de Java avec SDKMAN

**SDKMAN** est l'outil recommandé pour gérer les versions de Java et d'autres outils du SDK.

### Installation de SDKMAN

#### Linux / macOS

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

#### Windows (Git Bash)

**Prérequis** : Git Bash nécessite des outils Unix (zip, unzip, etc.) pour SDKMAN.

**Option 1 : Via winget**

```powershell
winget install gow
```

**Option 2 : Via Chocolatey**

1. Ouvrir **PowerShell en tant qu'administrateur**
2. Installer Chocolatey :

   ```powershell
   Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
   ```

3. Installer Gow (inclut zip, vim, etc.) :

   ```powershell
   choco install gow
   ```

**Ensuite, dans Git Bash** :

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

**En cas d'erreur "Please install zip"** : Fermez et rouvrez Git Bash après l'installation de Gow.

### Installation de Java 21+

```bash
# Lister les versions disponibles
sdk list java

# Installer Java 21 (Amazon Corretto recommandé)
sdk install java 21.0.5-amzn

# Ou Temurin (anciennement AdoptOpenJDK)
sdk install java 21.0.5-tem

# Définir comme version par défaut
sdk default java 21.0.5-amzn

# Vérifier l'installation
java -version
```

### Installation de Maven

```bash
sdk install maven 3.9.6
mvn -version
```

---

## 💻 Installation des IDEs

### IntelliJ IDEA

#### Windows / Linux / macOS

1. Télécharger depuis : <https://www.jetbrains.com/idea/download/>
2. Choisir **Community Edition** (gratuit) ou **Ultimate** (payant, 30 jours d'essai)
3. Installer et lancer

**Plugins recommandés** :

- Spring Boot
- Lombok
- Docker
- Database Tools

### Visual Studio Code

#### Windows

```bash
# Via winget
winget install Microsoft.VisualStudioCode
```

#### Linux (Ubuntu/Debian)

```bash
wget -qO- https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > packages.microsoft.gpg
sudo install -o root -g root -m 644 packages.microsoft.gpg /etc/apt/trusted.gpg.d/
sudo sh -c 'echo "deb [arch=amd64] https://packages.microsoft.com/repos/vscode stable main" > /etc/apt/sources.list.d/vscode.list'
sudo apt update
sudo apt install code
```

#### macOS

```bash
# Via Homebrew
brew install --cask visual-studio-code
```

**Extensions recommandées** :

- Extension Pack for Java (Microsoft)
- Spring Boot Extension Pack
- Lombok Annotations Support
- Docker
- REST Client

### Eclipse

#### Tous OS

1. Télécharger **Eclipse IDE for Enterprise Java and Web Developers** : <https://www.eclipse.org/downloads/>
2. Extraire et lancer
3. Installer **Spring Tools 4** depuis Eclipse Marketplace

---

## 🐳 Installation de Docker

### Windows

Documentation officielle : <https://docs.docker.com/desktop/setup/install/windows-install/>

1. Télécharger **Docker Desktop for Windows**
2. Installer et redémarrer
3. Vérifier dans **Git Bash** : `docker --version`

### Linux

Documentation officielle : <https://docs.docker.com/desktop/setup/install/linux/>

#### Ubuntu

Documentation : <https://docs.docker.com/desktop/setup/install/linux/ubuntu/>

**OU installation Docker Engine (sans Desktop)** :

```bash
# Désinstaller anciennes versions
sudo apt remove docker docker-engine docker.io containerd runc

# Installer dépendances
sudo apt update
sudo apt install ca-certificates curl gnupg lsb-release

# Ajouter la clé GPG
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Ajouter le repository
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Installer Docker
sudo apt update
sudo apt install docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Ajouter l'utilisateur au groupe docker
sudo usermod -aG docker $USER
newgrp docker

# Vérifier
docker --version
docker compose version
```

### macOS

Documentation officielle : <https://docs.docker.com/desktop/setup/install/mac-install/>

**Via Homebrew** :

```bash
brew install --cask docker
```

**OU télécharger Docker Desktop directement**

---

## 🗄️ Bases de Données et Services (via Docker)

Tous les services (PostgreSQL, Redis, etc.) seront démarrés via **Docker Compose** avec un réseau externe partagé.

### 1. Créer le réseau Docker externe

```bash
docker network create local_network
```

### 2. Structure des dossiers

Organiser chaque service dans son propre dossier avec un fichier `compose.yml` :

```
projet/
├── postgres/
│   └── compose.yml
├── redis/
│   └── compose.yml
└── autre-service/
    └── compose.yml
```

### 3. PostgreSQL

Créer le dossier `postgres/compose.yml` :

```yaml
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: ecommerce_db
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - local_network

volumes:
  postgres_data:

networks:
  local_network:
    external: true
```

### 4. Redis

Créer le dossier `redis/compose.yml` :

```yaml
services:
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    networks:
      - local_network

networks:
  local_network:
    external: true
```

### Démarrer les services

```bash
# Créer le réseau (une seule fois)
docker network create local_network

# Démarrer PostgreSQL
cd postgres
docker compose up -d
cd ..

# Démarrer Redis
cd redis
docker compose up -d
cd ..

# Option : Démarrer depuis un seul endroit avec -f (sans naviguer)
docker compose -f postgres/compose.yml up -d
docker compose -f redis/compose.yml up -d

# Vérifier l'état
docker compose ps

# Vérifier le réseau
docker network inspect local_network

# Arrêter un service (depuis son dossier)
cd postgres
docker compose down

# Ou avec -f (optionnel)
docker compose -f postgres/compose.yml down

# Arrêter et supprimer les volumes
docker compose down -v
```

### Clients Base de Données

#### DBeaver (Recommandé)

- Télécharger : <https://dbeaver.io/download/>
- Client universel gratuit et open-source
- Supporte PostgreSQL, MySQL, MongoDB, etc.

#### Extensions VS Code

- **PostgreSQL** (Chris Kolkman)
- **Database Client** (Weijan Chen)

#### IntelliJ IDEA

- **Database Tools** (intégré dans Ultimate, plugin pour Community)

---

## 🔧 Outils Complémentaires

### Postman (Tests API)

Télécharger depuis : <https://www.postman.com/downloads/>

Ou utiliser **REST Client** dans VS Code.

---

## ✅ Vérification de l'Installation

```bash
# Java
java -version

# Maven
mvn -version

# Docker
docker --version
docker compose version

# Git
git --version

# Vérifier les services Docker
docker compose ps
```

---

## 📝 Notes

- **SDKMAN** permet de basculer facilement entre différentes versions de Java : `sdk use java 21.0.5-amzn`
- Tous les services (PostgreSQL, Redis) utilisent le réseau Docker externe `local_network`
- Chaque service a son propre dossier avec un fichier `compose.yml` (pas besoin de `-f`)
- Sur Windows, utilisez **Git Bash** pour toutes les commandes terminal
- H2 (base en mémoire) est également disponible pour les tests unitaires
