# Configuration Wazuh personnalisée — AuthService

Ces fichiers sont déployés sur le Wazuh Manager (VM) pour reconnaître et classifier
les événements de sécurité générés par `security.log` de l'application AuthService.

## Fichiers
- `local_decoder.xml` : décodeur qui parse le format `AUTHSVC <timestamp> | EVENT_TYPE | username=X | ip=Y [| path=Z]`
- `local_rules.xml` : règles qui déclenchent des alertes classifiées (avec tags MITRE ATT&CK) selon le type d'événement

## Emplacement sur le Manager
- `/var/ossec/etc/decoders/local_decoder.xml`
- `/var/ossec/etc/rules/local_rules.xml`

## Événements couverts
| Événement | Rule ID | Niveau | MITRE ATT&CK |
|---|---|---|---|
| LOGIN_FAILED | 100101 | 5 | T1110 (Brute Force) |
| LOGIN_SUCCESS | 100102 | 3 | - |
| RATE_LIMIT_BLOCKED | 100103 | 10 | T1110 |
| MFA_FAILED | 100104 | 8 | T1556 |
| ACCOUNT_DISABLED_LOGIN_ATTEMPT | 100105 | 7 | - |
| ADMIN_ACCESS_DENIED | 100106 | 12 | T1078 (Valid Accounts) |
| Brute-force corrélé (5 échecs / 2min, même IP) | 100107 | 10 | T1110 |