# AuthService — Secure Authentication API (Spring Boot)

Back-end d'un module d'authentification sécurisé, conçu selon une approche **DevSecOps**.
Front-end Angular associé : [AuthFrontend](https://github.com/arijcheikhrouhou/AuthFrontend).

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Security-6DB33F) ![SQL Server](https://img.shields.io/badge/SQL%20Server-2022-CC2927) ![Redis](https://img.shields.io/badge/Redis-token%20blacklist-DC382D) ![Wazuh](https://img.shields.io/badge/Wazuh-SIEM-1B75BB)

## Fonctionnalités de sécurité

| Mesure | Implémentation |
|---|---|
| Authentification stateless | JWT signé HMAC-SHA256 (`JwtUtils`, `AuthTokenFilter`) |
| Contrôle d'accès | RBAC par rôles (`USER`, `ADMIN`) avec Spring Security |
| Double authentification | MFA / TOTP compatible Google Authenticator (`TotpService`) |
| Anti brute-force | Limitation de débit par IP avec Bucket4j (`RateLimitingService`) |
| Révocation des tokens | Blacklist des JWT au logout dans Redis (`TokenBlacklistService`) |
| Journalisation sécurité | Log dédié `security.log` (`SecurityLogger`) |
| Supervision (SIEM) | Décodeur et règles Wazuh avec mapping MITRE ATT&CK ([wazuh-config](wazuh-config/)) |
| Intégration continue | GitHub Actions : build + tests avec SQL Server et Redis en services |
| Gestion des secrets | Aucun secret dans le code : configuration par variables d'environnement |

## Architecture

```
Angular (AuthFrontend) ──HTTP + JWT──▶ AuthService (Spring Boot)
                                          ├── SQL Server   (utilisateurs, rôles)
                                          ├── Redis        (blacklist JWT)
                                          └── security.log ──▶ Wazuh Agent ──▶ Wazuh Manager (alertes)
```

## API

| Méthode | Endpoint | Accès |
|---|---|---|
| POST | `/api/auth/signup` | public |
| POST | `/api/auth/login` | public (rate limited) |
| POST | `/api/auth/logout` | authentifié — révoque le token |
| GET | `/api/auth/me` | authentifié |
| POST | `/api/mfa/setup` · `/enable` · `/disable` | authentifié |
| GET | `/api/admin/users` · `/users/{id}` | ADMIN |
| PUT | `/api/admin/users/{id}/roles` · `/status` | ADMIN |
| DELETE | `/api/admin/users/{id}` | ADMIN |

## Détection avec Wazuh

| Événement | Niveau | MITRE ATT&CK |
|---|---|---|
| Échecs de connexion répétés (5 en 2 min, même IP) | 10 | T1110 Brute Force |
| Blocage par rate limiting | 10 | T1110 |
| Échec MFA | 8 | T1556 |
| Accès admin refusé | 12 | T1078 Valid Accounts |

## Lancer le projet

Prérequis : Java 17, SQL Server, Redis.

```bash
# Variables d'environnement (voir .env.example)
export DB_PASSWORD=...
export JWT_SECRET=$(openssl rand -base64 48)

./mvnw spring-boot:run
```

Variables optionnelles : `DB_URL`, `DB_USERNAME`, `REDIS_HOST`, `REDIS_PORT`.

## Structure

```
src/main/java/com/cofat/auth_service/
├── config/        Spring Security, Redis
├── controller/    Auth, MFA, Admin
├── dto/           requêtes / réponses
├── entity/        User, Role
├── repository/    JPA
├── security/      JWT, filtre, TOTP, rate limiting, security logger
└── service/       UserDetails, blacklist des tokens
wazuh-config/      décodeur + règles Wazuh
```

## Auteur

**Arij Cheikhrouhou** — [GitHub](https://github.com/arijcheikhrouhou)
