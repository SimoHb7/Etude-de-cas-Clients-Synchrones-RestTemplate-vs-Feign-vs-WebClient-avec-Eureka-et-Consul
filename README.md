# Étude de cas : Clients Synchrones (RestTemplate vs Feign vs WebClient) avec Eureka et Consul

## Architecture du Projet

Ce projet compare trois clients HTTP synchrones dans un environnement de microservices :
- **RestTemplate** : Client HTTP synchrone classique
- **Feign** : Client déclaratif avec interface
- **WebClient** : Client réactif utilisé en mode synchrone (avec `.block()`)

### Services

1. **Eureka Server** (port 8761) : Serveur de découverte de services
2. **Service Voiture** (port 8081) : Expose l'API des voitures
3. **Service Client** (port 8080) : Consomme l'API voiture avec les 3 méthodes

## Structure du Projet

```
.
├── eureka-server/          # Serveur Eureka
├── service-voiture/        # Service fournisseur
├── service-client/         # Service consommateur
├── docker-compose.yml      # Configuration Docker (Eureka + Consul)
└── README.md              # Ce fichier
```

## Prérequis

- Java 17+
- Maven 3.6+
- Docker & Docker Compose (optionnel, pour Consul)
- JMeter ou Postman (pour tests de charge)

## Démarrage avec Eureka

### Étape 1 : Démarrer Eureka Server

```bash
cd eureka-server
mvn clean install
mvn spring-boot:run
```

Vérifier : http://localhost:8761

### Étape 2 : Démarrer Service Voiture (mode Eureka)

```bash
cd service-voiture
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=eureka
```

Tester : http://localhost:8081/api/cars/byClient/1

### Étape 3 : Démarrer Service Client (mode Eureka)

```bash
cd service-client
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=eureka
```

### Étape 4 : Tester les 3 endpoints

```bash
# RestTemplate
curl http://localhost:8080/api/clients/1/car/rest

# Feign
curl http://localhost:8080/api/clients/1/car/feign

# WebClient
curl http://localhost:8080/api/clients/1/car/webclient
```

**Résultat attendu :**
```json
{
  "id": 10,
  "marque": "Toyota",
  "modele": "Yaris",
  "clientId": 1
}
```

## Démarrage avec Consul

### Étape 1 : Démarrer Consul avec Docker

```bash
docker-compose up -d consul
```

Vérifier : http://localhost:8500

### Étape 2 : Modifier les pom.xml

Dans `service-voiture/pom.xml` et `service-client/pom.xml` :

1. Commenter la dépendance Eureka :
```xml
<!--
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
-->
```

2. Décommenter la dépendance Consul :
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```

### Étape 3 : Démarrer les services (mode Consul)

```bash
# Service Voiture
cd service-voiture
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=consul

# Service Client
cd service-client
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=consul
```

### Étape 4 : Vérifier l'enregistrement dans Consul

UI Consul : http://localhost:8500/ui/dc1/services

Vous devriez voir :
- service-voiture
- service-client

## Tests de Performance

### Test manuel avec curl

```bash
# Boucle de 100 requêtes pour chaque méthode
for i in {1..100}; do
  curl -s http://localhost:8080/api/clients/1/car/rest > /dev/null
done

for i in {1..100}; do
  curl -s http://localhost:8080/api/clients/1/car/feign > /dev/null
done

for i in {1..100}; do
  curl -s http://localhost:8080/api/clients/1/car/webclient > /dev/null
done
```

### Test avec JMeter

1. Créer un Thread Group avec :
   - Nombre de threads : 10, 50, 100, 200, 500
   - Ramp-up period : 10 secondes
   - Loop count : 10

2. Ajouter 3 HTTP Request :
   - `/api/clients/1/car/rest`
   - `/api/clients/1/car/feign`
   - `/api/clients/1/car/webclient`

3. Ajouter des Listeners :
   - Summary Report
   - Aggregate Report
   - Graph Results

### Métriques à collecter

1. **Latence** (ms) :
   - Temps moyen
   - P95 (95e percentile)
   - Min/Max

2. **Débit** (req/s) :
   - Throughput

3. **Ressources** :
   - CPU% (via Task Manager)
   - RAM MB

## Tests de Résilience

### Scénario 1 : Panne du Service Voiture

```bash
# Pendant un test de charge, arrêter le service voiture
cd service-voiture
# Ctrl+C pour arrêter

# Observer les erreurs côté client
# Redémarrer après 10-20 secondes
mvn spring-boot:run -Dspring-boot.run.profiles=eureka
```

**À noter :**
- Taux d'erreur (%)
- Temps de reprise
- Comportement de chaque client

### Scénario 2 : Panne du Discovery Server

```bash
# Arrêter Eureka pendant un test
cd eureka-server
# Ctrl+C

# Observer si les appels continuent (cache local)
# Redémarrer
mvn spring-boot:run
```

### Scénario 3 : Panne du Service Client

```bash
# Arrêter et redémarrer le service client
cd service-client
# Ctrl+C
mvn spring-boot:run -Dspring-boot.run.profiles=eureka

# Vérifier la re-registration dans Eureka UI
```

## Résultats Attendus

### Tableau 1 : Performance (exemple)

| Méthode      | Eureka (ms) | Eureka (req/s) | Consul (ms) | Consul (req/s) |
|--------------|-------------|----------------|-------------|----------------|
| RestTemplate | 25-30       | 320-350        | 25-30       | 320-350        |
| Feign        | 25-30       | 320-350        | 25-30       | 320-350        |
| WebClient    | 20-25       | 350-380        | 20-25       | 350-380        |

*Note : Valeurs approximatives avec délai simulé de 20ms*

### Tableau 2 : Complexité

| Méthode      | Lignes de code | Lisibilité | Configuration |
|--------------|----------------|------------|---------------|
| RestTemplate | ~15            | Moyenne    | Simple        |
| Feign        | ~5             | Excellente | Simple        |
| WebClient    | ~10            | Bonne      | Simple        |

## Analyse et Discussion

### Points clés

1. **Performance** :
   - WebClient légèrement plus rapide (non-bloquant par nature)
   - RestTemplate et Feign similaires
   - Le discovery (Eureka vs Consul) a peu d'impact sur la latence

2. **Simplicité** :
   - Feign : le plus simple et lisible (interface déclarative)
   - RestTemplate : nécessite plus de code manuel
   - WebClient : puissant mais plus complexe

3. **Résilience** :
   - Sans fallback, tous échouent immédiatement en cas de panne
   - Eureka/Consul maintiennent un cache local temporaire
   - Recommandation : ajouter Circuit Breaker (Resilience4j)

4. **Choix recommandé** :
   - **Feign** pour la simplicité et lisibilité
   - **WebClient** pour applications réactives ou haute performance
   - **RestTemplate** est déprécié (maintenance mode)

## Métriques et Monitoring

### Actuator Endpoints

- Service Client : http://localhost:8080/actuator
  - `/actuator/health` : Santé du service
  - `/actuator/metrics` : Métriques
  - `/actuator/prometheus` : Export Prometheus

- Service Voiture : http://localhost:8081/actuator

### Prometheus + Grafana (optionnel)

```bash
# Démarrer avec Docker Compose
docker-compose up -d prometheus grafana

# Grafana : http://localhost:3000 (admin/admin)
# Ajouter datasource Prometheus : http://prometheus:9090
```

## Livrables

1. ✅ Code des 2 services (client + voiture)
2. ✅ Configuration Eureka et Consul
3. ✅ 3 implémentations de clients HTTP
4. 📊 Résultats de tests (à compléter)
5. 📝 Analyse comparée (à rédiger)

## Troubleshooting

### Service ne s'enregistre pas dans Eureka

- Vérifier que Eureka Server est démarré
- Vérifier `spring.application.name` dans application.yml
- Vérifier les logs : `Failed to register with Eureka`

### Feign renvoie 404

- Vérifier le nom du service dans `@FeignClient(name = "service-voiture")`
- Vérifier que le service est bien enregistré dans Eureka/Consul

### WebClient timeout

- Augmenter le timeout : `.timeout(Duration.ofSeconds(10))`

## Commandes Utiles

```bash
# Compiler tous les projets
mvn clean install

# Démarrer avec profil spécifique
mvn spring-boot:run -Dspring-boot.run.profiles=eureka
mvn spring-boot:run -Dspring-boot.run.profiles=consul

# Voir les logs
mvn spring-boot:run | grep -i error

# Package en JAR
mvn clean package

# Exécuter le JAR
java -jar target/service-voiture-1.0.0.jar --spring.profiles.active=eureka
```

## Contact et Support

Pour toute question sur ce lab, consultez :
- Documentation Spring Cloud : https://spring.io/projects/spring-cloud
- Eureka : https://github.com/Netflix/eureka
- Consul : https://www.consul.io/
