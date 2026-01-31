# 🏥 Système de Gestion Médicale - Architecture Backend

## 📐 Architecture Microservices
<img width="1538" height="734" alt="microservice" src="https://github.com/user-attachments/assets/b1ef7a89-80e2-4540-b145-480efd6086e7" />



## 🏗️ Description de l'Architecture

### Couche Client
**Clients** (Web, Mobile) → Accès multi-plateforme au système

### Couche Frontend
**Angular Frontend** → Interface utilisateur moderne et réactive

### Couche Infrastructure
- **Eureka Server** → Service Discovery et Registry
- **Config Server** → Configuration centralisée (GitHub)

### Couche Microservices
- **Ms-Patients** → Gestion des patients et dossiers médicaux
- **appointments-service** → Gestion des rendez-vous

### Couche Communication
- **Communication Synchrone** → REST APIs entre services
- **Message Queue** → Communication asynchrone pour notifications
- **Notification Service** → Envoi des alertes et rappels

### Couche Données
- **MySQL** → Bases de données dédiées par microservice
- **PostgreSQL** → Base pour service de notifications

### Couche Conteneurs
- **Private Docker Registry** → Stockage sécurisé des images Docker

## 🔄 Flux de Communication

1. **Client → Frontend** : Les utilisateurs accèdent via le frontend Angular
2. **Frontend → Eureka** : Découverte des services disponibles
3. **Frontend → Microservices** : Appels REST synchrones
4. **Microservices ↔ MySQL** : Accès aux données
5. **Microservices → Message Queue** : Communication asynchrone
6. **Message Queue → Notification** : Déclenchement des notifications
7. **Config Server → Services** : Distribution de la configuration
8. **Docker Registry** : Déploiement des images conteneurisées

## 🛠️ Technologies par Composant

| Composant | Technologie | Port |
|-----------|-------------|------|
| Frontend | Angular 18 | 4200 |
| Eureka Server | Spring Cloud Netflix | 8761 |
| Config Server | Spring Cloud Config | 9999 |
| Ms-Patients | Spring Boot | 8081 |
| Appointments Service | Spring Boot | 8082 |
| Notification Service | Spring Boot | 8084 |
| Message Queue | RabbitMQ / Kafka | 5672 / 9092 |
| Databases | MySQL 8.0, PostgreSQL | 3306, 5432 |
| Docker Registry | Harbor / ECR | - |

## 📊 Patterns Implémentés

- **Service Discovery** : Eureka pour la localisation dynamique des services
- **Configuration Centralisée** : Config Server avec Git
- **Communication Synchrone** : REST APIs entre services
- **Communication Asynchrone** : Message Queue pour les notifications
- **Database per Service** : Isolation des données par microservice
- **Containerization** : Docker pour le déploiement

## 🚀 Avantages de l'Architecture

✅ **Scalabilité** : Chaque service peut être mis à l'échelle indépendamment  
✅ **Résilience** : Une défaillance d'un service n'affecte pas les autres  
✅ **Flexibilité** : Technologies différentes par service si nécessaire  
✅ **Déploiement Indépendant** : Mise à jour d'un service sans impact global  
✅ **Isolation des Données** : Chaque service gère sa propre base de données

---

**Architecture Microservices pour un système médical moderne et évolutif**
